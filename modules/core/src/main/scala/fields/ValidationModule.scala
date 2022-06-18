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

  /** Combines `a` and `b` using AND. Short-circuits if [[ValidationResult.strategy]] is
    * [[ValidationResult.Strategy.FailFast]].
    */
  def and(a: Rule, b: Rule) =
    VR.strategy match {
      case Strategy.Accumulate => F.map2(a, b)(VR.and)
      case Strategy.FailFast   => F.flatMap(a)(aa => if (VR.isInvalid(aa)) F.pure(aa) else b)
    }

  /** Combines `a` and `b` using OR. Short-circuits if `a` is valid */
  def or(a: Rule, b: Rule) =
    F.flatMap(a)(aa =>
      if (VR.isValid(aa)) F.pure(aa)
      else F.map(b)(bb => VR.or(bb, aa))
    )

  /** Alias for [[and]] */
  def combineAll(list: List[Rule]): Rule = andAll(list)

  /** Combines all validations using AND */
  def andAll(list: List[Rule]): Rule = FoldUtil.fold(list, validF, and)

  /** Combines all validations using OR */
  def orAll(list: List[Rule]): Rule = FoldUtil.fold(list, validF, or)

  /** Shortcut for [[ValidationPolicyBuilder]] */
  type PolicyBuilder[P] = ValidationPolicyBuilder[P, F, VR, E]

  /** Shortcut for [[ValidationPolicy]] */
  type Policy[P] = ValidationPolicy[P, F, VR, E]
  object Policy {

    /** Shortcut for [[ValidationPolicy.builder]] */
    def builder[P]: PolicyBuilder[P] = ValidationPolicy.builder
  }
}
