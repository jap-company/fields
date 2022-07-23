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

import GenericSyntax._
import typeclass._
import fail._

trait ModuleOptionSyntax[F[_], V[_], E] {
  implicit final def toOptionFieldOps[P](field: Field[Option[P]]): OptionFieldOps[P, F, V, E] =
    new OptionFieldOps(field)

  /** Unpacks `rule` from `Option` if `None` returns valid */
  def someOrValid(rule: => Option[Rule[F, V, E]])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    OptionSyntax.someOrValid(rule)
}

object OptionSyntax extends OptionSyntax
trait OptionSyntax {
  implicit final def toOptionFieldOps[F[_], V[_], E, P](field: Field[Option[P]]): OptionFieldOps[P, F, V, E] =
    new OptionFieldOps(field)

  /** Unpacks `rule` from `Option` if `None` returns valid */
  def someOrValid[F[_]: Effect, V[_]: Validated, E](rule: => Option[Rule[F, V, E]]): Rule[F, V, E] =
    Rule.defer(rule.getOrElse(Rule.valid))
}

final class OptionFieldOps[P, F[_], V[_], E](private val field: Field[Option[P]]) extends AnyVal {

  /** Alias for [[isDefined]] */
  def isSome(implicit F: Effect[F], V: Validated[V], FW: FailWithEmpty[E, Option[P]]): Rule[F, V, E] =
    isDefined

  /** Validates that [[jap.fields.Field]]#value is [[scala.Some]] */
  def isDefined(implicit F: Effect[F], V: Validated[V], FW: FailWithEmpty[E, Option[P]]): Rule[F, V, E] =
    field.ensure(_.isDefined, _.failEmpty)

  /** Alias for [[isEmpty]] */
  def isNone(implicit F: Effect[F], V: Validated[V], FW: FailWithNonEmpty[E, Option[P]]): Rule[F, V, E] =
    isEmpty

  /** Validates that [[jap.fields.Field]]#value is [[scala.None]] */
  def isEmpty(implicit F: Effect[F], V: Validated[V], FW: FailWithNonEmpty[E, Option[P]]): Rule[F, V, E] =
    field.ensure(_.isEmpty, _.failNonEmpty)

  /** Appies `check` to [[jap.fields.Field]]#value if it is [[scala.Some]] or returns valid */
  def some(check: Field[P] => Rule[F, V, E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.defer(field.option.fold[Rule[F, V, E]](Rule.valid)(check))
}
