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
package value
package syntax

import error.FieldError
import fail.*

object FailFieldSyntax extends FailFieldSyntax
trait FailFieldSyntax {
  implicit final def toErrorFieldOps[P, E](field: Field[P]): FailFieldOps[P, E] = new FailFieldOps(field)
}

trait FailFieldDsl[F[_], V[_], E] {
  implicit final def toErrorFieldOps[P](field: Field[P]): FailFieldOps[P, E] = new FailFieldOps(field)
}

final class FailFieldOps[P, E](private val f: Field[P]) extends AnyVal {

  /** Just pathrought error */
  def fail(error: E): E = error

  /** Useful when your error is wrapped in FieldError */
  def failFieldError[EE](error: EE)(implicit ev: FieldError[EE] =:= E): E =
    ev(FieldError(f.path, error))

  /** Returns InvalidError using [[fields.fail.FailWithInvalid]] typeclass */
  def failInvalid(implicit FW: FailWithInvalid[E, P]): E = FW.invalid(f.path, f.value)

  /** Returns EmptyError using [[fields.fail.FailWithEmpty]] typeclass */
  def failEmpty(implicit FW: FailWithEmpty[E, P]): E = FW.empty(f.path, f.value)

  /** Returns NonEmptyError using [[fields.fail.FailWithNonEmpty]] typeclass */
  def failNonEmpty(implicit FW: FailWithNonEmpty[E, P]): E = FW.nonEmpty(f.path, f.value)

  /** Returns CompareError using [[fields.fail.FailWithCompare]] typeclass */
  def failGreater[C](c: C)(implicit FW: FailWithCompare[E, P], C: CompareShow[P, C]): E =
    FW.greater(c)(f.path, f.value)

  /** Returns CompareError using [[fields.fail.FailWithCompare]] typeclass */
  def failGreaterEqual[C](c: C)(implicit FW: FailWithCompare[E, P], C: CompareShow[P, C]): E =
    FW.greaterEqual(c)(f.path, f.value)

  /** Returns CompareError using [[fields.fail.FailWithCompare]] typeclass */
  def failLess[C](c: C)(implicit FW: FailWithCompare[E, P], C: CompareShow[P, C]): E =
    FW.less(c)(f.path, f.value)

  /** Returns CompareError using [[fields.fail.FailWithCompare]] typeclass */
  def failLessEqual[C](c: C)(implicit FW: FailWithCompare[E, P], C: CompareShow[P, C]): E =
    FW.lessEqual(c)(f.path, f.value)

  /** Returns CompareError using [[fields.fail.FailWithCompare]] typeclass */
  def failEqual[C](c: C)(implicit FW: FailWithCompare[E, P], C: CompareShow[P, C]): E =
    FW.equal(c)(f.path, f.value)

  /** Returns CompareError using [[fields.fail.FailWithCompare]] typeclass */
  def failNotEqual[C](c: C)(implicit FW: FailWithCompare[E, P], C: CompareShow[P, C]): E =
    FW.notEqual(c)(f.path, f.value)

  /** Returns MinSizeError using [[fields.fail.FailWithMinSize]] typeclass */
  def failMinSize(size: Int)(implicit FW: FailWithMinSize[E, P]): E = FW.minSize(size)(f.path, f.value)

  /** Returns MaxSizeError using [[fields.fail.FailWithMaxSize]] typeclass */
  def failMaxSize(size: Int)(implicit FW: FailWithMaxSize[E, P]): E = FW.maxSize(size)(f.path, f.value)

  /** Returns OneOfError using [[fields.fail.FailWithOneOf]] typeclass */
  def oneOfError[PP >: P](variants: Seq[PP])(implicit FW: FailWithOneOf[E, P]): E =
    FW.oneOf(variants)(f.path, f.value)

  /** Returns MessageError using [[fields.fail.FailWithMessage]] typeclass */
  def failMessage(error: String, description: Option[String] = None)(implicit FW: FailWithMessage[E, P]): E =
    FW.message(error, description)(f.path, f.value)

  /** Returns MessageError using [[fields.fail.FailWithMessage]] typeclass */
  def failMessage(error: String, description: String)(implicit FW: FailWithMessage[E, P]): E =
    FW.message(error, Some(description))(f.path, f.value)
}
