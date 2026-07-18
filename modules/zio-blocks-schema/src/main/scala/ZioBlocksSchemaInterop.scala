/*
 * Copyright 2022 Jap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fields

import fields.fail.FailWith
import fields.typeclass.{HasErrors, PrependPath, RunSync}
import fields.value.Field
import zio.blocks.schema.{DynamicOptic, DynamicValue, Schema, SchemaError}
import zio.blocks.typeid.TypeId

/** Native error instances shared by the Value and Lens front ends. */
trait ZioBlocksSchemaErrorDsl extends SchemaErrorFailWith.Mixin {
  implicit final val schemaErrorPrependPath: PrependPath[SchemaError] = SchemaErrorFailWith.prependPath
}

/** Fields error and path integration for native ZIO Blocks schema errors. */
object SchemaErrorFailWith extends FailWith.Builder[SchemaError, Nothing] {
  implicit val prependPath: PrependPath[SchemaError] = (path, error) =>
    path.parts.reverse.foldLeft(error) {
      case (current, FieldPart.Path(name))   => current.atField(name)
      case (current, FieldPart.Index(index)) => current.atIndex(index)
      case (current, FieldPart.Key(key))     => current.atKey(DynamicValue.string(key))
    }

  override def build[P](
      path: FieldPath,
      value: P,
      errorType: => String,
      errorMessage: => Option[String],
  ): SchemaError =
    SchemaError.message(errorMessage.getOrElse(errorType), toDynamicOptic(path))

  def toDynamicOptic(path: FieldPath): DynamicOptic =
    path.parts.foldLeft(DynamicOptic.root) {
      case (current, FieldPart.Path(name))   => current.field(name)
      case (current, FieldPart.Index(index)) => current.at(index)
      case (current, FieldPart.Key(key))     => current.atKey(key)
    }
}

trait ZioBlocksSchemaSyntax {
  implicit final def toFieldsZioBlocksSchemaOps[A](schema: Schema[A]): FieldsZioBlocksSchemaOps[A] =
    new FieldsZioBlocksSchemaOps(schema)
}

object ZioBlocksSchemaSyntax extends ZioBlocksSchemaSyntax

final class FieldsZioBlocksSchemaOps[A](private val schema: Schema[A]) extends AnyVal {

  /** Runs a policy directly and preserves every native structured error and path. */
  def validateWithPolicy[F[_], V[_]](
      policy: PolicyK[Field[A], F, V, SchemaError]
  )(value: A)(implicit runSync: RunSync[F], hasErrors: HasErrors[V]): Either[SchemaError, A] =
    validate(policy(Field(value)), value)

  /** Mounts a Fields policy into this schema. The policy runs when ZIO Blocks constructs `A` while decoding and its
    * failures are emitted through ZIO Blocks' native `ConversionFailed` transform error.
    */
  def withPolicy[F[_], V[_]](
      policy: PolicyK[Field[A], F, V, SchemaError]
  )(implicit runSync: RunSync[F], hasErrors: HasErrors[V], typeId: TypeId[A]): Schema[A] =
    schema.transform[A](
      to = value => validateWithPolicy(policy)(value).fold(throw _, identity),
      from = identity,
    )

  /** Runs a lens policy directly and preserves every native structured error and path. */
  def validateWithLensPolicy[F[_], V[_]](
      policy: PolicyK[A, F, V, SchemaError]
  )(value: A)(implicit runSync: RunSync[F], hasErrors: HasErrors[V]): Either[SchemaError, A] =
    validate(policy(value), value)

  /** Mounts a Fields lens policy into this schema's decoding direction. */
  def withLensPolicy[F[_], V[_]](
      policy: PolicyK[A, F, V, SchemaError]
  )(implicit runSync: RunSync[F], hasErrors: HasErrors[V], typeId: TypeId[A]): Schema[A] =
    schema.transform[A](
      to = value => validateWithLensPolicy(policy)(value).fold(throw _, identity),
      from = identity,
    )

  private def validate[F[_], V[_]](
      rule: RuleK[F, V, SchemaError],
      value: A,
  )(implicit runSync: RunSync[F], hasErrors: HasErrors[V]): Either[SchemaError, A] = {
    val result = runSync.run(RuleK.unwrap(rule))
    hasErrors.errors(result) match {
      case Nil          => Right(value)
      case head :: tail => Left(tail.foldLeft(head)(_ ++ _))
    }
  }
}
