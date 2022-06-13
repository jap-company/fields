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

trait FailSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toFailFieldOps[P](field: Field[P]): FailFieldOps[P, F, VR, E] =
    new FailFieldOps(field)
}

final class FailFieldOps[P, F[_], VR[_], E](private val field: Field[P]) extends AnyVal {

  /** Just pathrought error */
  def fail(error: E)(implicit VR: ValidationResult[VR]): VR[E] = VR.invalid(error)

  /** Useful when your error is wrapped in FieldError */
  def failField[EE](error: EE)(implicit ev: FieldError[EE] =:= E, VR: ValidationResult[VR]): VR[E] =
    VR.invalid(ev(FieldError(field.path, error)))

  /** Returns InvalidError using [[FailWithInvalid]] typeclass */
  def failInvalid(implicit FW: FailWithInvalid[E], VR: ValidationResult[VR]): VR[E] = VR.invalid(FW.invalid(field))

  /** Returns EmptyError using [[FailWithEmpty]] typeclass */
  def failEmpty(implicit FW: FailWithEmpty[E], VR: ValidationResult[VR]): VR[E] = VR.invalid(FW.empty(field))

  /** Returns NonEmptyError using [[FailWithNonEmpty]] typeclass */
  def failNonEmpty(implicit FW: FailWithNonEmpty[E], VR: ValidationResult[VR]): VR[E] = VR.invalid(FW.nonEmpty(field))

  /** Returns CompareError using [[FailWithCompare]] typeclass */
  def failGreater[C](c: C)(implicit FW: FailWithCompare[E], C: FieldCompare[P, C], VR: ValidationResult[VR]): VR[E] =
    VR.invalid(FW.greater(c)(field))

  /** Returns CompareError using [[FailWithCompare]] typeclass */
  def failGreaterEqual[C](
      c: C
  )(implicit FW: FailWithCompare[E], C: FieldCompare[P, C], VR: ValidationResult[VR]): VR[E] =
    VR.invalid(FW.greaterEqual(c)(field))

  /** Returns CompareError using [[FailWithCompare]] typeclass */
  def failLess[C](c: C)(implicit FW: FailWithCompare[E], C: FieldCompare[P, C], VR: ValidationResult[VR]): VR[E] =
    VR.invalid(FW.less(c)(field))

  /** Returns CompareError using [[FailWithCompare]] typeclass */
  def failLessEqual[C](c: C)(implicit FW: FailWithCompare[E], C: FieldCompare[P, C], VR: ValidationResult[VR]): VR[E] =
    VR.invalid(FW.lessEqual(c)(field))

  /** Returns CompareError using [[FailWithCompare]] typeclass */
  def failEqual[C](c: C)(implicit FW: FailWithCompare[E], C: FieldCompare[P, C], VR: ValidationResult[VR]): VR[E] =
    VR.invalid(FW.equal(c)(field))

  /** Returns CompareError using [[FailWithCompare]] typeclass */
  def failNotEqual[C](c: C)(implicit FW: FailWithCompare[E], C: FieldCompare[P, C], VR: ValidationResult[VR]): VR[E] =
    VR.invalid(FW.notEqual(c)(field))

  /** Returns MinSizeError using [[FailWithMinSize]] typeclass */
  def failMinSize(size: Int)(implicit FW: FailWithMinSize[E], VR: ValidationResult[VR]): VR[E] =
    VR.invalid(FW.minSize(size)(field))

  /** Returns MaxSizeError using [[FailWithMaxSize]] typeclass */
  def failMaxSize(size: Int)(implicit FW: FailWithMaxSize[E], VR: ValidationResult[VR]): VR[E] =
    VR.invalid(FW.maxSize(size)(field))

  /** Returns OneOfError using [[FailWithOneOf]] typeclass */
  def failOneOf[PP >: P](variants: Seq[PP])(implicit FW: FailWithOneOf[E], VR: ValidationResult[VR]): VR[E] =
    VR.invalid(FW.oneOf(variants)(field))

  /** Returns MessageError using [[FailWithMessage]] typeclass */
  def failMessage(error: String, description: Option[String] = None)(implicit
      FW: FailWithMessage[E],
      VR: ValidationResult[VR],
  ): VR[E] =
    VR.invalid(FW.message(error, description)(field))

  /** Returns MessageError using [[FailWithMessage]] typeclass */
  def failMessage(error: String, description: String)(implicit
      FW: FailWithMessage[E],
      VR: ValidationResult[VR],
  ): VR[E] =
    VR.invalid(FW.message(error, Some(description))(field))
}
