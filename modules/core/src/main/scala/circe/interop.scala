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
package circe

import typeclass.*

import cats.data.NonEmptyList
import io.circe.*
import io.circe.Decoder.*

class FieldsDecoderOps[T](private val decoder: Decoder[T]) extends AnyVal {
  def usePolicy[F[_]: RunSync, V[_]: Validated: HasErrors, E: HasFieldPath](policy: PolicyK[T, F, V, E]): Decoder[T] =
    interop.policyDecoder(policy, decoder)
}

class FieldsCodecOps[T](private val codec: Codec[T]) extends AnyVal {
  def usePolicy[F[_]: RunSync, V[_]: Validated: HasErrors, E: HasFieldPath](policy: PolicyK[T, F, V, E]): Codec[T] =
    Codec.from[T](
      interop.policyDecoder(policy, codec),
      codec,
    )
}

trait FieldsCirceInstances0 extends FieldsCirceInstances1 {
  implicit def toFieldsDecoderOps[T](decoder: Decoder[T]): FieldsDecoderOps[T] = new FieldsDecoderOps(decoder)
}

trait FieldsCirceInstances1 {
  implicit def toFieldsCodecOps[T](codec: Codec[T]): FieldsCodecOps[T] = new FieldsCodecOps(codec)
}

object interop extends FieldsCirceInstances0 {
  def policyDecoder[T, F[_]: RunSync, V[_]: Validated: HasErrors, E: HasFieldPath](
      policy: PolicyK[T, F, V, E],
      decoder: Decoder[T],
  ): Decoder[T] =
    decoder.flatMap { entity =>
      val result = RunSync[F].run(policy(entity).effect)

      if (implicitly[Validated[V]].isValid(result)) Decoder.const(entity)
      else
        new Decoder[T] {
          private val errors: List[E] = implicitly[HasErrors[V]].errors(result)
          private val failures        =
            errors.map(e =>
              DecodingFailure(
                message = e.toString,
                ops = implicitly[HasFieldPath[E]].getPath(e).parts.reverse.map {
                  case FieldPart.Key(value)   => CursorOp.DownField(value)
                  case FieldPart.Path(value)  => CursorOp.DownField(value)
                  case FieldPart.Index(value) => CursorOp.DownN(value)
                },
              )
            )

          final def apply(c: HCursor): Result[T] = Left(failures.head)

          final override def decodeAccumulating(c: HCursor): AccumulatingResult[T] =
            cats.data.Validated.invalid(NonEmptyList.fromListUnsafe(failures))
        }
    }
}
