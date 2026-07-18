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
import value.Field.GetValue
import value.syntax.GenericSyntax.*

trait OrderingDsl[F[_], V[_], E] {
  implicit final def toOrderingFieldOps[P](field: Field[P]): OrderingFieldOps[P, F, V, E] =
    new OrderingFieldOps(field)
}
object OrderingSyntax extends OrderingSyntax
trait OrderingSyntax             {
  implicit final def toOrderingFieldOps[F[_], V[_], E, P](field: Field[P]): OrderingFieldOps[P, F, V, E] =
    new OrderingFieldOps(field)
}
final class OrderingFieldOps[P, F[_], V[_], E](private val field: Field[P]) extends AnyVal {

  /** Validates that [[fields.Field]]#value is greater or equal to `compared` */
  def >=[C](compared: => C)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[P],
      FW: FailWithCompare[E, P],
      C: CompareShow[P, C],
      G: GetValue[P, C],
  ): RuleK[F, V, E] = gte[C](compared)

  /** Validates that [[fields.Field]]#value is greater or equal to `compared` */
  def gte[C](compared: => C)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[P],
      FW: FailWithCompare[E, P],
      C: CompareShow[P, C],
      G: GetValue[P, C],
  ): RuleK[F, V, E] =
    field.assert(O.gteq(_, G.value(compared)), FW.greaterEqual(compared))

  /** Validates that [[fields.Field]]#value is greater than `compared` */
  def >[C](compared: => C)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[P],
      FW: FailWithCompare[E, P],
      C: CompareShow[P, C],
      G: GetValue[P, C],
  ): RuleK[F, V, E] = gt[C](compared)

  /** Validates that [[fields.Field]]#value is greater than `compared` */
  def gt[C](compared: => C)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[P],
      FW: FailWithCompare[E, P],
      C: CompareShow[P, C],
      G: GetValue[P, C],
  ): RuleK[F, V, E] =
    field.assert(O.gt(_, G.value(compared)), FW.greater(compared))

  /** Validates that [[fields.Field]]#value is less or equal to `compared` */
  def <=[C](compared: => C)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[P],
      FW: FailWithCompare[E, P],
      C: CompareShow[P, C],
      G: GetValue[P, C],
  ): RuleK[F, V, E] = lte(compared)

  /** Validates that [[fields.Field]]#value is less or equal to `compared` */
  def lte[C](compared: => C)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[P],
      FW: FailWithCompare[E, P],
      C: CompareShow[P, C],
      G: GetValue[P, C],
  ): RuleK[F, V, E] =
    field.assert(O.lteq(_, G.value(compared)), FW.lessEqual(compared))

  /** Validates that [[fields.Field]]#value is less than `compared` */
  def <[C](compared: => C)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[P],
      FW: FailWithCompare[E, P],
      C: CompareShow[P, C],
      G: GetValue[P, C],
  ): RuleK[F, V, E] = lt[C](compared)

  /** Validates that [[fields.Field]]#value is less than `compared` */
  def lt[C](compared: => C)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[P],
      FW: FailWithCompare[E, P],
      C: CompareShow[P, C],
      G: GetValue[P, C],
  ): RuleK[F, V, E] =
    field.assert(O.lt(_, G.value(compared)), FW.less(compared))

  /** Validates that [[fields.Field]]#value is  greaterEqual than `from` and lessEqual `to` */
  def isBetween[FROM, TO](from: => FROM, to: => TO)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[P],
      FW: FailWithCompare[E, P],
      CF: CompareShow[P, FROM],
      GVF: GetValue[P, FROM],
      CT: CompareShow[P, TO],
      GVT: GetValue[P, TO],
  ): RuleK[F, V, E] =
    R.and(gte[FROM](from), lte[TO](to))
}
