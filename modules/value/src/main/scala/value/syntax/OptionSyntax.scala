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

import fail.*
import value.syntax.GenericSyntax.*

trait OptionDsl[F[_], V[_], E] {
  implicit final def toOptionFieldOps[P](field: Field[Option[P]]): OptionFieldOps[P, F, V, E] =
    new OptionFieldOps(field)

  /** Unpacks `rule` from `Option` if `None` returns valid */
  def someOrValid(rule: => Option[RuleK[F, V, E]])(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    OptionSyntax.someOrValid(rule)
}

object OptionSyntax extends OptionSyntax
trait OptionSyntax {
  implicit final def toOptionFieldOps[F[_], V[_], E, P](field: Field[Option[P]]): OptionFieldOps[P, F, V, E] =
    new OptionFieldOps(field)

  /** Unpacks `rule` from `Option` if `None` returns valid */
  def someOrValid[F[_], V[_], E](rule: => Option[RuleK[F, V, E]])(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.defer(rule.getOrElse(R.valid))
}

final class OptionFieldOps[P, F[_], V[_], E](private val field: Field[Option[P]]) extends AnyVal {

  /** Alias for [[isDefined]] */
  def isSome(implicit R: RuleOps[F, V, E], FW: FailWithEmpty[E, Option[P]]): RuleK[F, V, E] =
    isDefined

  /** Validates that [[fields.Field]]#value is [[scala.Some]] */
  def isDefined(implicit R: RuleOps[F, V, E], FW: FailWithEmpty[E, Option[P]]): RuleK[F, V, E] =
    field.assert(_.isDefined, FW.empty)

  /** Alias for [[isEmpty]] */
  def isNone(implicit R: RuleOps[F, V, E], FW: FailWithNonEmpty[E, Option[P]]): RuleK[F, V, E] =
    isEmpty

  /** Validates that [[fields.Field]]#value is [[scala.None]] */
  def isEmpty(implicit R: RuleOps[F, V, E], FW: FailWithNonEmpty[E, Option[P]]): RuleK[F, V, E] =
    field.assert(_.isEmpty, FW.nonEmpty)

  /** Appies `check` to [[fields.Field]]#value if it is [[scala.Some]] or returns valid */
  def some(check: Field[P] => RuleK[F, V, E])(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.defer(field.option.fold[RuleK[F, V, E]](R.valid)(check))
}
