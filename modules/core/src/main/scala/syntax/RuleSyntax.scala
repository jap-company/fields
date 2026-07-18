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
package syntax

import typeclass.*

object RuleSyntax extends RuleSyntax
trait RuleSyntax {
  implicit final def toRuleValidationOps[F[_], V[_], E](rule: RuleK[F, V, E]): RuleValidationOps[F, V, E] =
    new RuleValidationOps[F, V, E](rule)

  implicit final def toRuleSequenceOps[F[_], V[_], E](rules: Iterable[RuleK[F, V, E]]): RuleSequenceOps[F, V, E] =
    new RuleSequenceOps(rules)
}

final class RuleSequenceOps[F[_], V[_], E](private val rules: Iterable[RuleK[F, V, E]]) extends AnyVal {

  /** See [[RuleK.andAll]] */
  def combineAll(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.andAll(rules.toList)

  /** See [[RuleK.andAll]] */
  def andAll(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.andAll(rules.toList)

  /** See [[RuleK.orAll]] */
  def orAll(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.orAll(rules.toList)
}

final class RuleValidationOps[F[_], V[_], E](private val rule: RuleK[F, V, E]) extends AnyVal {

  /** Same as [[fields.typeclass.Validated.isInvalid]] but effectful */
  def isInvalid(implicit R: RuleOps[F, V, E]): F[Boolean] =
    R.F.map(rule.effect)(R.V.isInvalid)

  /** Same as [[fields.typeclass.Validated.isValid]] but effectful */
  def isValid(implicit R: RuleOps[F, V, E]): F[Boolean] =
    R.F.map(rule.effect)(R.V.isValid)

  /** Same as [[fields.typeclass.HasErrors.errors]] but effectful */
  def errors(implicit F: Effect[F], E: HasErrors[V]): F[List[E]] =
    F.map(rule.effect)(E.errors[E])

  /** Same as [[fields.typeclass.Validated.whenValid]] but effectful */
  def whenValid(b: => RuleK[F, V, E])(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.modifyM(rule)(vr => if (R.V.isValid(vr)) b else rule)

  /** Same as [[fields.typeclass.Validated.whenValid]] but effectful */
  def whenInvalid(f: V[E] => V[E])(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.modify(rule)(vr => if (R.V.isInvalid(vr)) f(vr) else vr)

  /** Same as [[fields.typeclass.Validated.asError]] but effectful */
  def asError(error: => E)(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.modify(rule)(R.V.asError(_)(error))

  /** Same as [[fields.typeclass.Validated.asError]] but effectful */
  def asInvalid(invalid: => V[E])(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.modify(rule)(R.V.asInvalid(_)(invalid))

  /** Same as [[fields.typeclass.Validated.when]] but effectful */
  def when(cond: => Boolean)(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.defer(if (cond) rule else R.valid)

  /** Same as [[fields.typeclass.Validated.unless]] but effectful */
  def unless(cond: => Boolean)(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.defer(if (cond) R.valid else rule)

  /** See [[RuleK.or]] */
  def or(b: RuleK[F, V, E])(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.or(rule, b)

  /** See [[RuleK.or]] */
  def ||(b: RuleK[F, V, E])(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.or(rule, b)

  /** See [[RuleK.and]] */
  def and(b: RuleK[F, V, E])(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.and(rule, b)

  /** See [[RuleK.and]] */
  def &&(b: RuleK[F, V, E])(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.and(rule, b)
}
