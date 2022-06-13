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

trait ErrorSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toErrorFieldOps[P](field: Field[P]): ErrorFieldOps[P, F, VR, E] =
    new ErrorFieldOps(field)
}

final class ErrorFieldOps[P, F[_], VR[_], E](private val field: Field[P]) extends AnyVal {

  /** Just pathrought error */
  def error(error: E): E = error

  /** Useful when your error is wrapped in FieldError */
  def fieldError[EE](error: EE)(implicit ev: FieldError[EE] =:= E): E =
    ev(FieldError(field.path, error))

  /** Returns InvalidError using [[FailWithInvalid]] typeclass */
  def invalidError(implicit FW: FailWithInvalid[E]): E = FW.invalid(field)

  /** Returns EmptyError using [[FailWithEmpty]] typeclass */
  def emptyError(implicit FW: FailWithEmpty[E]): E = FW.empty(field)

  /** Returns NonEmptyError using [[FailWithNonEmpty]] typeclass */
  def nonEmptyError(implicit FW: FailWithNonEmpty[E]): E = FW.nonEmpty(field)

  /** Returns CompareError using [[FailWithCompare]] typeclass */
  def greaterError[C](c: C)(implicit FW: FailWithCompare[E], C: FieldCompare[P, C]): E = FW.greater(c)(field)

  /** Returns CompareError using [[FailWithCompare]] typeclass */
  def greaterEqualError[C](c: C)(implicit FW: FailWithCompare[E], C: FieldCompare[P, C]): E =
    FW.greaterEqual(c)(field)

  /** Returns CompareError using [[FailWithCompare]] typeclass */
  def lessError[C](c: C)(implicit FW: FailWithCompare[E], C: FieldCompare[P, C]): E = FW.less(c)(field)

  /** Returns CompareError using [[FailWithCompare]] typeclass */
  def lessEqualError[C](c: C)(implicit FW: FailWithCompare[E], C: FieldCompare[P, C]): E =
    FW.lessEqual(c)(field)

  /** Returns CompareError using [[FailWithCompare]] typeclass */
  def equalError[C](c: C)(implicit FW: FailWithCompare[E], C: FieldCompare[P, C]): E = FW.equal(c)(field)

  /** Returns CompareError using [[FailWithCompare]] typeclass */
  def notEqualError[C](c: C)(implicit FW: FailWithCompare[E], C: FieldCompare[P, C]): E = FW.notEqual(c)(field)

  /** Returns MinSizeError using [[FailWithMinSize]] typeclass */
  def minSizeError(size: Int)(implicit FW: FailWithMinSize[E]): E = FW.minSize(size)(field)

  /** Returns MaxSizeError using [[FailWithMaxSize]] typeclass */
  def maxSizeError(size: Int)(implicit FW: FailWithMaxSize[E]): E = FW.maxSize(size)(field)

  /** Returns OneOfError using [[FailWithOneOf]] typeclass */
  def oneOfError[PP >: P](variants: Seq[PP])(implicit FW: FailWithOneOf[E]): E = FW.oneOf(variants)(field)

  /** Returns MessageError using [[FailWithMessage]] typeclass */
  def messageError(error: String, description: Option[String] = None)(implicit FW: FailWithMessage[E]): E =
    FW.message(error, description)(field)

  /** Returns MessageError using [[FailWithMessage]] typeclass */
  def messageError(error: String, description: String)(implicit FW: FailWithMessage[E]): E =
    FW.message(error, Some(description))(field)
}
