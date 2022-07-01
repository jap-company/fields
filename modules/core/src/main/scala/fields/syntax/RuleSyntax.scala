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

object RuleSyntax extends RuleSyntax
trait RuleSyntax {
  implicit final def toRuleValidationOps[F[_], V[_], E](rule: Rule[F, V, E]): RuleValidationOps[F, V, E] =
    new RuleValidationOps[F, V, E](rule)

  implicit final def toRuleSequenceOps[F[_], V[_], E](rules: Iterable[Rule[F, V, E]]): RuleSequenceOps[F, V, E] =
    new RuleSequenceOps(rules)
}

final class RuleSequenceOps[F[_], V[_], E](private val rules: Iterable[Rule[F, V, E]]) extends AnyVal {

  /** See [[Rule.andAll]] */
  def combineAll(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.andAll(rules.toList)

  /** See [[Rule.andAll]] */
  def andAll(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.andAll(rules.toList)

  /** See [[Rule.orAll]] */
  def orAll(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.orAll(rules.toList)
}

final class RuleValidationOps[F[_], V[_], E](private val rule: Rule[F, V, E]) extends AnyVal {

  /** Same as [[jap.fields.typeclass.Validated.isInvalid]] but effectful */
  def isInvalid(implicit F: Effect[F], V: Validated[V]): F[Boolean] =
    F.map(rule.effect)(V.isInvalid)

  /** Same as [[jap.fields.typeclass.Validated.isValid]] but effectful */
  def isValid(implicit F: Effect[F], V: Validated[V]): F[Boolean] =
    F.map(rule.effect)(V.isValid)

  /** Same as [[jap.fields.typeclass.HasErrors.errors]] but effectful */
  def errors(implicit F: Effect[F], E: HasErrors[V]): F[List[E]] =
    F.map(rule.effect)(E.errors[E])

  /** Same as [[jap.fields.typeclass.Validated.whenValid]] but effectful */
  def whenValid(b: => Rule[F, V, E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.modifyM(rule)(vr => if (V.isValid(vr)) b else rule)

  /** Same as [[jap.fields.typeclass.Validated.whenValid]] but effectful */
  def whenInvalid(f: V[E] => V[E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.modify(rule)(vr => if (V.isInvalid(vr)) f(vr) else vr)

  /** Same as [[jap.fields.typeclass.Validated.asError]] but effectful */
  def asError(error: => E)(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.modify(rule)(V.asError(_)(error))

  /** Same as [[jap.fields.typeclass.Validated.asError]] but effectful */
  def asInvalid(invalid: => V[E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.modify(rule)(V.asInvalid(_)(invalid))

  /** Same as [[jap.fields.typeclass.Validated.when]] but effectful */
  def when(cond: => Boolean)(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.defer(if (cond) rule else Rule.valid)

  /** Same as [[jap.fields.typeclass.Validated.unless]] but effectful */
  def unless(cond: => Boolean)(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.defer(if (cond) Rule.valid else rule)

  /** See [[Rule.or]] */
  def or(b: Rule[F, V, E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.or(rule, b)

  /** See [[Rule.or]] */
  def ||(b: Rule[F, V, E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.or(rule, b)

  /** See [[Rule.and]] */
  def and(b: Rule[F, V, E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.and(rule, b)

  /** See [[Rule.and]] */
  def &&(b: Rule[F, V, E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.and(rule, b)
}
