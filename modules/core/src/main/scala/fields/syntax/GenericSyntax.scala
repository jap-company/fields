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

package jap.fields
package syntax

import scala.reflect.ClassTag

import typeclass._
import fail._

trait ModuleGenericSyntax[F[_], V[_], E] {
  implicit final def toFieldOps[P](field: Field[P]): FieldOps[P, F, V, E] =
    new FieldOps(field)
}

object GenericSyntax extends GenericSyntax
trait GenericSyntax {
  implicit final def toFieldOps[F[_], V[_], E, P](field: Field[P]): FieldOps[P, F, V, E] =
    new FieldOps(field)
}

final class FieldOps[P, F[_], V[_], E](private val field: Field[P]) extends AnyVal {

  /** Runs validation only if true */
  def when(test: => Boolean)(f: Field[P] => Rule[F, V, E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.when(test)(f(field))

  /** Runs validation only if false */
  def unless(test: => Boolean)(f: Field[P] => Rule[F, V, E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.when(!test)(f(field))

  /** See [[Rule.when]] */
  def when(test: P => Boolean)(rule: Field[P] => Rule[F, V, E])(implicit
      F: Effect[F],
      V: Validated[V],
  ): Rule[F, V, E] = Rule.when(test(field.value))(rule(field))

  /** See [[Rule.whenF]] */
  def whenF(test: P => F[Boolean])(rule: Field[P] => Rule[F, V, E])(implicit
      F: Effect[F],
      V: Validated[V],
  ): Rule[F, V, E] = Rule.whenF(test(field.value))(rule(field))

  /** Runs rule for subtype `PP` else is valid */
  def whenType[PP <: P](rule: Field[PP] => Rule[F, V, E])(implicit
      F: Effect[F],
      V: Validated[V],
      CT: ClassTag[PP],
  ): Rule[F, V, E] =
    Rule.defer {
      field.value match {
        case _: PP => rule(field.asInstanceOf[Field[PP]])
        case _     => Rule.valid[F, V, E]
      }
    }

  /** See [[Rule.ensure]] */
  @inline def ensure(test: P => Boolean, error: Field[P] => V[E])(implicit
      F: Effect[F],
      V: Validated[V],
  ): Rule[F, V, E] = Rule.ensure(error(field))(test(field.value))

  /** See [[Rule.ensureF]] */
  @inline def ensureF(test: P => F[Boolean], error: Field[P] => V[E])(implicit
      F: Effect[F],
      V: Validated[V],
  ): Rule[F, V, E] = Rule.ensureF(error(field))(test(field.value))

  /** Like [[Rule.ensure]] but for explicit error */
  @inline def assert(test: P => Boolean, error: Field[P] => E)(implicit
      F: Effect[F],
      V: Validated[V],
  ): Rule[F, V, E] = ensure(test, error.andThen(V.invalid))

  /** Like [[Rule.ensureF]] but for explicit error */
  @inline def assertF(test: P => F[Boolean], error: Field[P] => E)(implicit
      F: Effect[F],
      V: Validated[V],
  ): Rule[F, V, E] = ensureF(test, error.andThen(V.invalid))

  /** Returns Suspended Outcome of applying `f` to `field` */
  @inline def check(f: Field[P] => V[E])(implicit F: Effect[F]): Rule[F, V, E] =
    Rule.pure(f(field))

  /** Returns Defered Outcome of applying `f` to `field` */
  @inline def checkF(f: Field[P] => Rule[F, V, E])(implicit F: Effect[F]): Rule[F, V, E] =
    Rule.defer(f(field))

  /** Alias for [[equalTo]] */
  def ===[C](compared: => C)(implicit
      F: Effect[F],
      V: Validated[V],
      FW: FailWithCompare[E, P],
      C: FieldCompare[P, C],
  ): Rule[F, V, E] =
    equalTo[C](compared)

  /** Validates that [[jap.fields.Field]]#value is equal to `compared` */
  def equalTo[C](compared: => C)(implicit
      F: Effect[F],
      V: Validated[V],
      FW: FailWithCompare[E, P],
      C: FieldCompare[P, C],
  ): Rule[F, V, E] =
    assert(_ == C.value(compared), FW.equal[P, C](compared))

  /** Alias for [[notEqualTo]] */
  def !==[C](compared: => C)(implicit
      F: Effect[F],
      V: Validated[V],
      FW: FailWithCompare[E, P],
      C: FieldCompare[P, C],
  ): Rule[F, V, E] =
    notEqualTo[C](compared)

  /** Validates that [[jap.fields.Field]]#value is not equal to `compared` */
  def notEqualTo[C](compared: => C)(implicit
      F: Effect[F],
      V: Validated[V],
      FW: FailWithCompare[E, P],
      C: FieldCompare[P, C],
  ): Rule[F, V, E] =
    assert(_ != C.value(compared), FW.notEqual[P, C](compared))

  /** Validates that [[jap.fields.Field]]#value is contained by `seq` */
  def in(seq: => Seq[P])(implicit F: Effect[F], V: Validated[V], FW: FailWithOneOf[E, P]): Rule[F, V, E] =
    assert(seq.contains, FW.oneOf(seq))

  /** Combines all validations using AND */
  def all(f: Field[P] => Rule[F, V, E]*)(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.andAll(f.map(_.apply(field)).toList)

  /** Combines all validations using OR */
  def any(f: Field[P] => Rule[F, V, E]*)(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.orAll(f.map(_.apply(field)).toList)

  /** Validates [[jap.fields.Field]] using implicit [[ValidationPolicy]] */
  def validate(implicit P: ValidationPolicy[P, F, V, E]): Rule[F, V, E] = P.validate(field)

  /** Validates [[jap.fields.Field]] using implicit [[ValidationPolicy]] */
  def validateEither(implicit
      F: Effect[F],
      V: Validated[V],
      P: ValidationPolicy[P, F, V, E],
      E: HasErrors[V],
  ): F[Either[List[E], P]] =
    P.validateEither(field)
}
