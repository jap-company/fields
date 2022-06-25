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

final class FailFieldOps[P, V[_], E](private val field: Field[P]) extends AnyVal {

  /** Use field path to construct error */
  def failPath(error: FieldPath => E)(implicit V: Validated[V]): V[E] = V.invalid(error(field.path))

  /** Just pathrought error */
  def fail(error: E)(implicit V: Validated[V]): V[E] = V.invalid(error)

  /** Useful when your error is wrapped in FieldError */
  def failField[EE](error: EE)(implicit ev: FieldError[EE] =:= E, V: Validated[V]): V[E] =
    V.invalid(ev(FieldError(field.path, error)))

  /** Returns InvalidError using [[jap.fields.fail.FailWithInvalid]] typeclass */
  def failInvalid(implicit FW: FailWithInvalid[E, P], V: Validated[V]): V[E] = V.invalid(FW.invalid(field))

  /** Returns EmptyError using [[jap.fields.fail.FailWithEmpty]] typeclass */
  def failEmpty(implicit FW: FailWithEmpty[E, P], V: Validated[V]): V[E] = V.invalid(FW.empty(field))

  /** Returns NonEmptyError using [[jap.fields.fail.FailWithNonEmpty]] typeclass */
  def failNonEmpty(implicit FW: FailWithNonEmpty[E, P], V: Validated[V]): V[E] = V.invalid(FW.nonEmpty(field))

  /** Returns CompareError using [[jap.fields.fail.FailWithCompare]] typeclass */
  def failGreater[C](c: C)(implicit FW: FailWithCompare[E, P], C: FieldCompare[P, C], V: Validated[V]): V[E] =
    V.invalid(FW.greater(c)(field))

  /** Returns CompareError using [[jap.fields.fail.FailWithCompare]] typeclass */
  def failGreaterEqual[C](
      c: C
  )(implicit FW: FailWithCompare[E, P], C: FieldCompare[P, C], V: Validated[V]): V[E] =
    V.invalid(FW.greaterEqual(c)(field))

  /** Returns CompareError using [[jap.fields.fail.FailWithCompare]] typeclass */
  def failLess[C](c: C)(implicit FW: FailWithCompare[E, P], C: FieldCompare[P, C], V: Validated[V]): V[E] =
    V.invalid(FW.less(c)(field))

  /** Returns CompareError using [[jap.fields.fail.FailWithCompare]] typeclass */
  def failLessEqual[C](c: C)(implicit FW: FailWithCompare[E, P], C: FieldCompare[P, C], V: Validated[V]): V[E] =
    V.invalid(FW.lessEqual(c)(field))

  /** Returns CompareError using [[jap.fields.fail.FailWithCompare]] typeclass */
  def failEqual[C](c: C)(implicit FW: FailWithCompare[E, P], C: FieldCompare[P, C], V: Validated[V]): V[E] =
    V.invalid(FW.equal(c)(field))

  /** Returns CompareError using [[jap.fields.fail.FailWithCompare]] typeclass */
  def failNotEqual[C](c: C)(implicit FW: FailWithCompare[E, P], C: FieldCompare[P, C], V: Validated[V]): V[E] =
    V.invalid(FW.notEqual(c)(field))

  /** Returns MinSizeError using [[jap.fields.fail.FailWithMinSize]] typeclass */
  def failMinSize(size: Int)(implicit FW: FailWithMinSize[E, P], V: Validated[V]): V[E] =
    V.invalid(FW.minSize(size)(field))

  /** Returns MaxSizeError using [[jap.fields.fail.FailWithMaxSize]] typeclass */
  def failMaxSize(size: Int)(implicit FW: FailWithMaxSize[E, P], V: Validated[V]): V[E] =
    V.invalid(FW.maxSize(size)(field))

  /** Returns OneOfError using [[jap.fields.fail.FailWithOneOf]] typeclass */
  def failOneOf[PP >: P](variants: Seq[PP])(implicit FW: FailWithOneOf[E, P], V: Validated[V]): V[E] =
    V.invalid(FW.oneOf(variants)(field))

  /** Returns MessageError using [[jap.fields.fail.FailWithMessage]] typeclass */
  def failMessage(error: String, description: Option[String] = None)(implicit
      FW: FailWithMessage[E, P],
      V: Validated[V],
  ): V[E] =
    V.invalid(FW.message(error, description)(field))

  /** Returns MessageError using [[jap.fields.fail.FailWithMessage]] typeclass */
  def failMessage(error: String, description: String)(implicit
      FW: FailWithMessage[E, P],
      V: Validated[V],
  ): V[E] =
    V.invalid(FW.message(error, Some(description))(field))
}
