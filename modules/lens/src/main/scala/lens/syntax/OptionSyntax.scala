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
package lens
package syntax

import fail.*
import lens.syntax.GenericSyntax.*

trait OptionDsl[F[_], V[_], E] {
  implicit final def toOptionFieldOps[P, S](field: FieldLens[P, Option[S]]): OptionFieldOps[P, S, F, V, E] =
    new OptionFieldOps(field)
}

object OptionSyntax extends OptionSyntax
trait OptionSyntax {
  implicit final def toOptionFieldOps[P, S, F[_], V[_], E](
      field: FieldLens[P, Option[S]]
  ): OptionFieldOps[P, S, F, V, E] =
    new OptionFieldOps(field)
}

final class OptionFieldOps[P, S, F[_], V[_], E](private val field: FieldLens[P, Option[S]]) extends AnyVal {

  /** Alias for [[isDefined]] */
  def isSome(implicit R: RuleOps[F, V, E], FW: FailWithEmpty[E, Option[S]]): PolicyK[P, F, V, E] =
    isDefined

  /** Validates that [[fields.Field]]#value is [[scala.Some]] */
  def isDefined(implicit R: RuleOps[F, V, E], FW: FailWithEmpty[E, Option[S]]): PolicyK[P, F, V, E] =
    field.assert(_.isDefined, FW.empty)

  /** Alias for [[isEmpty]] */
  def isNone(implicit R: RuleOps[F, V, E], FW: FailWithNonEmpty[E, Option[S]]): PolicyK[P, F, V, E] =
    isEmpty

  /** Validates that [[fields.Field]]#value is [[scala.None]] */
  def isEmpty(implicit R: RuleOps[F, V, E], FW: FailWithNonEmpty[E, Option[S]]): PolicyK[P, F, V, E] =
    field.assert(_.isEmpty, FW.nonEmpty)

  /** Appies `check` to [[fields.Field]]#value if it is [[scala.Some]] or returns valid */
  def some(check: FieldLens[P, S] => PolicyK[P, F, V, E]): PolicyK[P, F, V, E] =
    check(field.toPrism(identity))
}
