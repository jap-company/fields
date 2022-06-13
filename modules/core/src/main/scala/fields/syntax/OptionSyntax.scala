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

trait OptionSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toOptionFieldOps[P](field: Field[Option[P]]): OptionFieldOps[P, F, VR, E] =
    new OptionFieldOps(field)
}

final class OptionFieldOps[P, F[_], VR[_], E](private val field: Field[Option[P]]) extends AnyVal {

  /** Alias for [[isDefined]] */
  def isSome(implicit M: ValidationModule[F, VR, E], FW: FailWithEmpty[E]): F[VR[E]] = isDefined

  /** Validates that [[jap.fields.Field]]#value is [[scala.Some]] */
  def isDefined(implicit M: ValidationModule[F, VR, E], FW: FailWithEmpty[E]): F[VR[E]] =
    M.fieldAssert[Option[P]](field, _.isDefined, FW.empty)

  /** Alias for [[isEmpty]] */
  def isNone(implicit M: ValidationModule[F, VR, E], FW: FailWithNonEmpty[E]): F[VR[E]] = isEmpty

  /** Validates that [[jap.fields.Field]]#value is [[scala.None]] */
  def isEmpty(implicit M: ValidationModule[F, VR, E], FW: FailWithNonEmpty[E]): F[VR[E]] =
    M.fieldAssert[Option[P]](field, _.isEmpty, FW.nonEmpty)

  /** Appies `check` to [[jap.fields.Field]]#value if it is [[scala.Some]] or returns valid */
  def some(check: Field[P] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.F.defer(
      field.value match {
        case Some(value) => check(Field(field.path, value))
        case None        => M.validF
      }
    )
}
