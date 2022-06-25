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
import typeclass._
import data._
import fail._
import error._

/** FailFast [[jap.fields.ValidationModule]] */
abstract class FailFastVM[F[_]: Effect, E] extends ValidationModule[F, FailFast, E]

/** Accumulating [[jap.fields.ValidationModule]] */
abstract class AccumulateVM[F[_]: Effect, E] extends ValidationModule[F, Accumulate, E]

/** Default ValidationModule where:
  *   - Effect is [[jap.fields.typeclass.Effect.Sync]]
  *   - Validated is Accumulate
  *   - Error is ValidationError
  */
trait DefaultAccumulateVM  extends AccumulateVM[Effect.Sync, ValidationError] with CanFailWithValidationError
object DefaultAccumulateVM extends DefaultAccumulateVM

/** Default ValidationModule where:
  *   - Effect is [[jap.fields.typeclass.Effect.Sync]]
  *   - Validated is FailFast
  *   - Error is ValidationError
  */
trait DefaultFailFastVM  extends FailFastVM[Effect.Sync, ValidationError] with CanFailWithValidationError
object DefaultFailFastVM extends DefaultFailFastVM

/** God object that provides all validation syntax for choosen Effect - F[_], Validated - V[_] and Error - E Requires
  * user to provide implicit instances of Effect and Validated typeclasses for choosen F[_] and V[_].
  */
@implicitNotFound("ValidationModule[${F}, ${V}, ${E}] not found")
abstract class ValidationModule[F[_], V[_], E](implicit
    val F: Effect[F],
    val V: Validated[V],
) extends ModuleGenericSyntax[F, V, E]
    with ModuleBooleanSyntax[F, V, E]
    with ModuleRuleSyntax[F, V, E]
    with ModuleOrderingSyntax[F, V, E]
    with ModuleOptionSyntax[F, V, E]
    with ModuleStringSyntax[F, V, E]
    with ModuleMapSyntax[F, V, E]
    with ModuleIterableSyntax[F, V, E]
    with PolicySyntax
    with FieldSyntax
    with ValidatedSyntax {

  /** [[jap.fields.ValidationPolicy]] alias. Infers F, V, E */
  type Policy[P] = ValidationPolicy[P, F, V, E]

  /** [[jap.fields.ValidationPolicy]] alias. Infers F, V, E */
  type MPolicy[P] = Policy[P]
  val MPolicy = Policy

  /** [[jap.fields.ValidationPolicyBuilder]] alias. Infers F, V, E */
  type PolicyBuilder[P] = ValidationPolicyBuilder[P, F, V, E]

  /** [[jap.fields.ValidationPolicyBuilder]] alias. Infers F, V, E */
  type MPolicyBuilder[P] = PolicyBuilder[P]

  object Policy {

    /** See [[ValidationPolicy.builder]] */
    def builder[P]: PolicyBuilder[P] = ValidationPolicy.builder
  }

  /** [[jap.fields.Rule]] alias. F, V, E infered from [[jap.fields.ValidationModule]] Very useful for type inference
    * when building custom validations
    */
  final type MRule = Rule[F, V, E]

  /** Convenient accessors makes type inference better */
  object MRule {

    /** See [[Rule.valid]] */
    val valid: MRule = Rule.valid

    /** See [[Rule.invalid]] */
    def invalid(error: => E): MRule = Rule.invalid(error)

    /** See [[Rule.pure]] */
    def pure(validated: => V[E]): MRule = Rule.pure(validated)

    /** See [[Rule.effect]] */
    def effect(effect: => F[V[E]]): MRule = Rule.effect(effect)

    /** See [[Rule.defer]] */
    def defer(rule: => MRule): MRule = Rule.defer(rule)

    /** See [[Rule.when]] */
    def when(test: => Boolean)(rule: => MRule) = Rule.when(test)(rule)

    /** See [[Rule.whenF]] */
    def whenF(test: => F[Boolean])(rule: => MRule) = Rule.whenF(test)(rule)

    /** See [[Rule.ensure]] */
    def ensure(v: => V[E])(test: => Boolean) = Rule.ensure(v)(test)

    /** See [[Rule.ensureF]] */
    def ensureF(v: => V[E])(test: => F[Boolean]) = Rule.ensureF(v)(test)

    /** See [[Rule.and]] */
    def and(ra: MRule, rb: MRule): MRule = Rule.and(ra, rb)

    /** See [[Rule.or]] */
    def or(ra: MRule, rb: MRule): MRule = Rule.or(ra, rb)

    /** See [[Rule.andAll]] */
    def andAll(rules: List[MRule]): MRule = Rule.andAll(rules)

    /** See [[Rule.orAll]] */
    def orAll(rules: List[MRule]): MRule = Rule.orAll(rules)

    /** See [[Rule.fold]] */
    def fold[B](rule: MRule)(onInvalid: V[E] => B, onValid: => B) = Rule.fold(rule)(onInvalid, onValid)

    /** See [[Rule.modify]] */
    def modify(rule: MRule)(f: V[E] => V[E]) = Rule.modify(rule)(f)

    /** See [[Rule.modifyM]] */
    def modifyM(rule: MRule)(f: V[E] => MRule) = Rule.modifyM(rule)(f)
  }
}
