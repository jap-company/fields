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

trait EffectValidationResultSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toEffectValidationResultOps(a: F[VR[E]]): EffectValidationResultOps[F, VR, E] =
    new EffectValidationResultOps[F, VR, E](a)

  implicit final def toEffectValidationResultSequenceOps(
      iterable: Iterable[F[VR[E]]]
  ): EffectValidationResultSequenceOps[F, VR, E] =
    new EffectValidationResultSequenceOps(iterable)
}

final class EffectValidationResultSequenceOps[F[_], VR[_], E](
    private val iterable: Iterable[F[VR[E]]]
) extends AnyVal {

  /** See [[ValidationModule.combineAll]] */
  def combineAll(implicit M: ValidationModule[F, VR, E]): F[VR[E]] = M.combineAll(iterable.toList)
}

final class EffectValidationResultOps[F[_], VR[_], E](private val a: F[VR[E]]) extends AnyVal {

  /** Same as [[ValidationResult.isInvalid]] but effectful */
  def isInvalid(implicit M: ValidationModule[F, VR, E]): F[Boolean] = M.F.map(a)(M.VR.isInvalid)

  /** Same as [[ValidationResult.isValid]] but effectful */
  def isValid(implicit M: ValidationModule[F, VR, E]): F[Boolean] = M.F.map(a)(M.VR.isValid)

  /** Same as [[ValidationResult.errors]] but effectful */
  def errors(implicit M: ValidationModule[F, VR, E]): F[List[E]] = M.F.map(a)(M.VR.errors)

  /** Same as [[ValidationResult.whenValid]] but effectful */
  def whenValid(b: => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.F.flatMap(a)(vr => if (M.VR.isValid(vr)) b else a)

  /** Same as [[ValidationResult.whenValid]] but effectful */
  def whenInvalid(f: VR[E] => VR[E])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.F.map(a)(vr => if (M.VR.isInvalid(vr)) f(vr) else vr)

  /** Same as [[ValidationResult.asError]] but effectful */
  def asError(error: E)(implicit M: ValidationModule[F, VR, E]) = M.F.map(a)(M.VR.asError(_)(error))

  /** Same as [[ValidationResult.asError]] but effectful */
  def asInvalid(invalid: VR[E])(implicit M: ValidationModule[F, VR, E]) = M.F.map(a)(M.VR.asInvalid(_)(invalid))

  /** Same as [[ValidationResult.when]] but effectful */
  def when(cond: Boolean)(implicit M: ValidationModule[F, VR, E]): F[VR[E]] = if (cond) a else M.validF

  /** Same as [[ValidationResult.unless]] but effectful */
  def unless(cond: Boolean)(implicit M: ValidationModule[F, VR, E]): F[VR[E]] = if (cond) M.validF else a

  /** See [[ValidationModule.or]] */
  def or(b: F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] = M.or(a, b)

  /** See [[ValidationModule.or]] */
  def ||(b: F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] = M.or(a, b)

  /** See [[ValidationModule.and]] */
  def and(b: F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] = M.and(a, b)

  /** See [[ValidationModule.and]] */
  def &&(b: F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] = M.and(a, b)
}
