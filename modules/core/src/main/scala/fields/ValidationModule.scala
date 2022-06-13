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

import scala.annotation.implicitNotFound

import syntax._
import ValidationResult._
import ValidationEffect.Sync

/** FailFast [[jap.fields.ValidationModule]] */
abstract class FailFastVM[F[_]: ValidationEffect, E] extends ValidationModule[F, FailFast, E]

/** Accumulating [[jap.fields.ValidationModule]] */
abstract class AccumulateVM[F[_]: ValidationEffect, E] extends ValidationModule[F, Accumulate, E]

/** Default ValidationModule where:
  *   - ValidationEffect is [[ValidationEffect.Sync]]
  *   - ValidationResult is Accumulate
  *   - Error is ValidationError
  */
object DefaultAccumulateVM extends AccumulateVM[Sync, ValidationError]

/** Default ValidationModule where:
  *   - ValidationEffect is [[ValidationEffect.Sync]]
  *   - ValidationResult is FailFast
  *   - Error is ValidationError
  */
object DefaultFailFastVM extends FailFastVM[Sync, ValidationError]

/** God object that provides all validation syntax for choosen Effect - F[_], ValidationResult - VR[_] and Error - E
  * Requires user to provide implicit instances of ValidationEffect and ValidationResult typeclasses for choosen F[_]
  * and VR[_].
  */
@implicitNotFound("ValidationModule[${F}, ${VR}, ${E}] not found")
abstract class ValidationModule[F[_], VR[_], E](implicit
    val F: ValidationEffect[F],
    val VR: ValidationResult[VR],
) extends GenericSyntax[F, VR, E]
    with FailSyntax[F, VR, E]
    with ErrorSyntax[F, VR, E]
    with BooleanSyntax[F, VR, E]
    with EffectValidationResultSyntax[F, VR, E]
    with OrderingSyntax[F, VR, E]
    with OptionSyntax[F, VR, E]
    with StringSyntax[F, VR, E]
    with IterableSyntax[F, VR, E]
    with MapSyntax[F, VR, E]
    with PolicySyntax[F, VR, E]
    with FieldSyntax
    with ValidationResultSyntax {
  type Rule = F[VR[E]]

  /** Syntax classes requires implicit ValidationModule in scope */
  implicit def Module: ValidationModule[F, VR, E] = this

  /** Valid `VR[E]` instance */
  val valid: VR[E] = VR.valid[E]

  /** Returns invalid `VR[E]` containing provided error */
  def invalid(error: E): VR[E] = VR.invalid(error)

  /** Returns `F[_]` containing invalid `VR[E]` containing provided error */
  def invalidF(error: E): F[VR[E]] = F.suspend(VR.invalid(error))

  /** Valid F[VR[E]] instance */
  val validF: F[VR[E]] = F.pure(valid)

  /** Returns [[ValidationResult.valid]] if `cond` is true else fails with provided `error` */
  @inline def ensure[P](cond: => Boolean, vr: => VR[E]): F[VR[E]] =
    F.suspend(if (cond) valid else vr)

  @inline def ensureF[P](cond: => F[Boolean], vr: => VR[E]): F[VR[E]] =
    F.map(F.defer(cond))(if (_) valid else vr)

  /** Returns [[ValidationResult.valid]] if `cond` is true else fails with provided `error` */
  @inline def fieldEnsure[P](field: Field[P], cond: P => Boolean, vr: Field[P] => VR[E]): F[VR[E]] =
    ensure(cond(field.value), vr(field))

  /** Returns [[ValidationResult.valid]] if `cond` is true else fails with provided `error` */
  @inline def fieldEnsureF[P](field: Field[P], cond: P => F[Boolean], vr: Field[P] => VR[E]): F[VR[E]] =
    ensureF(cond(field.value), vr(field))

  /** Like [[ValidationModule.ensure]] but single error */
  @inline def fieldAssert[P](field: Field[P], cond: P => Boolean, error: Field[P] => E): F[VR[E]] =
    fieldEnsure(field, cond, error.andThen(invalid))

  /** Like [[ValidationModule.ensureF]] but single error */
  @inline def fieldAssertF[P](field: Field[P], cond: P => F[Boolean], error: Field[P] => E): F[VR[E]] =
    fieldEnsureF(field, cond, error.andThen(invalid))

  /** Applies `f` validation to [[jap.fields.Field]]#value */
  @inline def fieldCheck[P](field: Field[P], f: Field[P] => VR[E]): F[VR[E]] = F.suspend(f(field))

  /** Applies `f` effectful validation to [[jap.fields.Field]]#value */
  @inline def fieldCheckF[P](field: Field[P], f: Field[P] => F[VR[E]]): F[VR[E]] = F.defer(f(field))

  /** Combines `a` and `b` using AND. Short-circuits if [[ValidationResult.strategy]] is
    * [[ValidationResult.Strategy.FailFast]].
    */
  def and(a: F[VR[E]], b: F[VR[E]]) =
    VR.strategy match {
      case Strategy.Accumulate => F.map2(a, b)(VR.and)
      case Strategy.FailFast   => F.flatMap(a)(aa => if (VR.isInvalid(aa)) F.pure(aa) else b)
    }

  /** Combines `a` and `b` using OR. Short-circuits if `a` is valid */
  def or(a: F[VR[E]], b: F[VR[E]]) =
    F.flatMap(a)(aa =>
      if (VR.isValid(aa)) F.pure(aa)
      else F.map(b)(bb => VR.or(bb, aa))
    )

  /** Alias for [[and]] */
  def combineAll(list: List[F[VR[E]]]): F[VR[E]] = andAll(list)

  /** Combines all validations using AND */
  def andAll(list: List[F[VR[E]]]): F[VR[E]] = FoldUtil.fold(list, validF, and)

  /** Combines all validations using OR */
  def orAll(list: List[F[VR[E]]]): F[VR[E]] = FoldUtil.fold(list, validF, or)

  /** Shortcut for [[ValidationPolicyBuilder]] */
  type PolicyBuilder[P] = ValidationPolicyBuilder[P, F, VR, E]

  /** Shortcut for [[ValidationPolicy]] */
  type Policy[P] = ValidationPolicy[P, F, VR, E]
  object Policy {

    /** Shortcut for [[ValidationPolicy.builder]] */
    def builder[P]: PolicyBuilder[P] = ValidationPolicy.builder
  }
}
