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
package lens
package syntax

import error.FieldError
import fail.*

object FailFieldSyntax extends FailFieldSyntax
trait FailFieldSyntax {

  /** Just pathrought error */
  def fail[V, E](error: E): PathedFn[V, E] = (_, _) => error

  /** Useful when your error is wrapped in FieldError */
  def failFieldError[V, E, EE](error: EE)(implicit ev: FieldError[EE] =:= E): PathedFn[V, E] =
    (path, _) => ev(FieldError(path, error))

  /** Returns InvalidError using [[fields.fail.FailWithInvalid]] typeclass */
  def failInvalid[E, P](implicit FW: FailWithInvalid[E, P]): PathedFn[P, E] = FW.invalid

  /** Returns EmptyError using [[fields.fail.FailWithEmpty]] typeclass */
  def failEmpty[E, P](implicit FW: FailWithEmpty[E, P]): PathedFn[P, E] = FW.empty

  /** Returns NonEmptyError using [[fields.fail.FailWithNonEmpty]] typeclass */
  def failNonEmpty[E, P](implicit FW: FailWithNonEmpty[E, P]): PathedFn[P, E] = FW.nonEmpty

  /** Returns CompareError using [[fields.fail.FailWithCompare]] typeclass */
  def failGreater[E, P, C](c: C)(implicit FW: FailWithCompare[E, P], C: CompareShow[P, C]): PathedFn[P, E] =
    FW.greater(c)

  /** Returns CompareError using [[fields.fail.FailWithCompare]] typeclass */
  def failGreaterEqual[E, P, C](
      c: C
  )(implicit FW: FailWithCompare[E, P], C: CompareShow[P, C]): PathedFn[P, E] =
    FW.greaterEqual(c)

  /** Returns CompareError using [[fields.fail.FailWithCompare]] typeclass */
  def failLess[E, P, C](c: C)(implicit FW: FailWithCompare[E, P], C: CompareShow[P, C]): PathedFn[P, E] =
    FW.less(c)

  /** Returns CompareError using [[fields.fail.FailWithCompare]] typeclass */
  def failLessEqual[E, P, C](c: C)(implicit FW: FailWithCompare[E, P], C: CompareShow[P, C]): PathedFn[P, E] =
    FW.lessEqual(c)

  /** Returns CompareError using [[fields.fail.FailWithCompare]] typeclass */
  def failEqual[E, P, C](c: C)(implicit FW: FailWithCompare[E, P], C: CompareShow[P, C]): PathedFn[P, E] =
    FW.equal(c)

  /** Returns CompareError using [[fields.fail.FailWithCompare]] typeclass */
  def failNotEqual[E, P, C](c: C)(implicit FW: FailWithCompare[E, P], C: CompareShow[P, C]): PathedFn[P, E] =
    FW.notEqual(c)

  /** Returns MinSizeError using [[fields.fail.FailWithMinSize]] typeclass */
  def failMinSize[E, P](size: Int)(implicit FW: FailWithMinSize[E, P]): PathedFn[P, E] = FW.minSize(size)

  /** Returns MaxSizeError using [[fields.fail.FailWithMaxSize]] typeclass */
  def failMaxSize[E, P](size: Int)(implicit FW: FailWithMaxSize[E, P]): PathedFn[P, E] = FW.maxSize(size)

  /** Returns OneOfError using [[fields.fail.FailWithOneOf]] typeclass */
  def failOneOf[E, P, PP >: P](variants: Seq[PP])(implicit FW: FailWithOneOf[E, P]): PathedFn[P, E] =
    FW.oneOf(variants)

  /** Returns MessageError using [[fields.fail.FailWithMessage]] typeclass */
  def failMessage[E, P](error: String, description: Option[String] = None)(implicit
      FW: FailWithMessage[E, P]
  ): PathedFn[P, E] =
    FW.message(error, description)

  /** Returns MessageError using [[fields.fail.FailWithMessage]] typeclass */
  def failMessage[E, P](error: String, description: String)(implicit
      FW: FailWithMessage[E, P]
  ): PathedFn[P, E] =
    FW.message(error, Some(description))
}
