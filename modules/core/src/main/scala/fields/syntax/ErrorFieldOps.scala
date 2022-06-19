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

import typeclass._
import fail._
import error.FieldError

final class ErrorFieldOps[P, E](private val field: Field[P]) extends AnyVal {

  /** Just pathrought error */
  def error(error: E): E = error

  /** Useful when your error is wrapped in FieldError */
  def fieldError[EE](error: EE)(implicit ev: FieldError[EE] =:= E): E =
    ev(FieldError(field.path, error))

  /** Returns InvalidError using [[jap.fields.fail.FailWithInvalid]] typeclass */
  def invalidError(implicit FW: FailWithInvalid[E, P]): E = FW.invalid(field)

  /** Returns EmptyError using [[jap.fields.fail.FailWithEmpty]] typeclass */
  def emptyError(implicit FW: FailWithEmpty[E, P]): E = FW.empty(field)

  /** Returns NonEmptyError using [[jap.fields.fail.FailWithNonEmpty]] typeclass */
  def nonEmptyError(implicit FW: FailWithNonEmpty[E, P]): E = FW.nonEmpty(field)

  /** Returns CompareError using [[jap.fields.fail.FailWithCompare]] typeclass */
  def greaterError[C](c: C)(implicit FW: FailWithCompare[E, P], C: FieldCompare[P, C]): E = FW.greater(c)(field)

  /** Returns CompareError using [[jap.fields.fail.FailWithCompare]] typeclass */
  def greaterEqualError[C](c: C)(implicit FW: FailWithCompare[E, P], C: FieldCompare[P, C]): E =
    FW.greaterEqual(c)(field)

  /** Returns CompareError using [[jap.fields.fail.FailWithCompare]] typeclass */
  def lessError[C](c: C)(implicit FW: FailWithCompare[E, P], C: FieldCompare[P, C]): E = FW.less(c)(field)

  /** Returns CompareError using [[jap.fields.fail.FailWithCompare]] typeclass */
  def lessEqualError[C](c: C)(implicit FW: FailWithCompare[E, P], C: FieldCompare[P, C]): E =
    FW.lessEqual(c)(field)

  /** Returns CompareError using [[jap.fields.fail.FailWithCompare]] typeclass */
  def equalError[C](c: C)(implicit FW: FailWithCompare[E, P], C: FieldCompare[P, C]): E = FW.equal(c)(field)

  /** Returns CompareError using [[jap.fields.fail.FailWithCompare]] typeclass */
  def notEqualError[C](c: C)(implicit FW: FailWithCompare[E, P], C: FieldCompare[P, C]): E = FW.notEqual(c)(field)

  /** Returns MinSizeError using [[jap.fields.fail.FailWithMinSize]] typeclass */
  def minSizeError(size: Int)(implicit FW: FailWithMinSize[E, P]): E = FW.minSize(size)(field)

  /** Returns MaxSizeError using [[jap.fields.fail.FailWithMaxSize]] typeclass */
  def maxSizeError(size: Int)(implicit FW: FailWithMaxSize[E, P]): E = FW.maxSize(size)(field)

  /** Returns OneOfError using [[jap.fields.fail.FailWithOneOf]] typeclass */
  def oneOfError[PP >: P](variants: Seq[PP])(implicit FW: FailWithOneOf[E, P]): E = FW.oneOf(variants)(field)

  /** Returns MessageError using [[jap.fields.fail.FailWithMessage]] typeclass */
  def messageError(error: String, description: Option[String] = None)(implicit FW: FailWithMessage[E, P]): E =
    FW.message(error, description)(field)

  /** Returns MessageError using [[jap.fields.fail.FailWithMessage]] typeclass */
  def messageError(error: String, description: String)(implicit FW: FailWithMessage[E, P]): E =
    FW.message(error, Some(description))(field)
}
