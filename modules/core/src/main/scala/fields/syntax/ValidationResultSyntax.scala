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

object ValidationResultSyntax extends ValidationResultSyntax
trait ValidationResultSyntax {
  implicit final def toVROps[VR[_], E](vr: VR[E]): VROps[VR, E]                                 = new VROps(vr)
  implicit final def toVRIdOps[E](error: E): VRIdOps[E]                                         = new VRIdOps(error)
  implicit final def toVRSequenceOps[VR[_], E](iterable: Iterable[VR[E]]): VRSequenceOps[VR, E] =
    new VRSequenceOps(iterable)
}

final class VRSequenceOps[VR[_], E](private val iterable: Iterable[VR[E]]) extends AnyVal {

  /** See [[ValidationResult.sequence]] */
  def sequence(implicit VR: ValidationResult[VR]): VR[E] = VR.sequence(iterable.toList)

  /** See [[ValidationResult.andAll]] */
  def andAll(implicit VR: ValidationResult[VR]): VR[E] = VR.andAll(iterable.toList)

  /** See [[ValidationResult.orAll]] */
  def orAll(implicit VR: ValidationResult[VR]): VR[E] = VR.orAll(iterable.toList)
}

final class VRIdOps[E](private val error: E) extends AnyVal {
  def invalid[VR[_]](implicit VR: ValidationResult[VR]): VR[E] = VR.invalid(error)
}

final class VROps[VR[_], E](private val vr: VR[E]) extends AnyVal {

  /** See [[ValidationResult.isInvalid]] */
  def isInvalid(implicit VR: ValidationResult[VR]): Boolean = VR.isInvalid(vr)

  /** See [[ValidationResult.isValid]] */
  def isValid(implicit VR: ValidationResult[VR]): Boolean = VR.isValid(vr)

  /** See [[ValidationResult.and]] */
  def and(that: VR[E])(implicit VR: ValidationResult[VR]): VR[E] = VR.and(vr, that)

  /** See [[ValidationResult.and]] */
  def &&(that: VR[E])(implicit VR: ValidationResult[VR]): VR[E] = VR.and(vr, that)

  /** See [[ValidationResult.or]] */
  def or(that: VR[E])(implicit VR: ValidationResult[VR]): VR[E] = VR.or(vr, that)

  /** See [[ValidationResult.or]] */
  def ||(that: VR[E])(implicit VR: ValidationResult[VR]): VR[E] = VR.or(vr, that)

  /** See [[ValidationResult.errors]] */
  def errors(implicit VR: ValidationResult[VR]): List[E] = VR.errors(vr)

  /** See [[ValidationResult.when]] */
  def when(cond: Boolean)(implicit VR: ValidationResult[VR]): VR[E] = VR.when(cond)(vr)

  /** See [[ValidationResult.whenValid]] */
  def whenValid(b: => VR[E])(implicit VR: ValidationResult[VR]): VR[E] = VR.whenValid(vr)(b)

  /** See [[ValidationResult.whenValid]] */
  def whenInvalid(f: VR[E] => VR[E])(implicit VR: ValidationResult[VR]): VR[E] = VR.whenInvalid(vr)(f)

  /** See [[ValidationResult.unless]] */
  def unless(cond: Boolean)(implicit VR: ValidationResult[VR]): VR[E] = VR.unless(cond)(vr)

  /** See [[ValidationResult.asError]] */
  def asError(error: E)(implicit VR: ValidationResult[VR]) = VR.asError(vr)(error)

  /** See [[ValidationResult.asError]] */
  def asInvalid(invalid: VR[E])(implicit VR: ValidationResult[VR]) = VR.asInvalid(vr)(invalid)

}
