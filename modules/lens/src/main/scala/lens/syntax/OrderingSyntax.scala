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

import PolicyKSyntax.*
import fail.*
import lens.FieldLens.GetValue
import lens.syntax.GenericSyntax.*

trait OrderingDsl[F[_], V[_], E] {
  implicit final def toOrderingFieldOps[P, S](field: FieldLens[P, S]): OrderingFieldOps[P, S, F, V, E] =
    new OrderingFieldOps(field)
}
object OrderingSyntax extends OrderingSyntax
trait OrderingSyntax             {
  implicit final def toOrderingFieldOps[P, S, F[_], V[_], E](field: FieldLens[P, S]): OrderingFieldOps[P, S, F, V, E] =
    new OrderingFieldOps(field)
}
final class OrderingFieldOps[P, S, F[_], V[_], E](private val field: FieldLens[P, S]) extends AnyVal {

  /** Validates that [[fields.Field]]#value is greater or equal to `compared` */
  def >=[C](compared: C)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[S],
      FW: FailWithCompare[E, S],
      C: CompareShow[S, C],
      G: GetValue[P, S, C],
  ): PolicyK[P, F, V, E] = gte[C](compared)

  /** Validates that [[fields.Field]]#value is greater or equal to `compared` */
  def gte[C](compared: C)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[S],
      FW: FailWithCompare[E, S],
      C: CompareShow[S, C],
      G: GetValue[P, S, C],
  ): PolicyK[P, F, V, E] =
    field.assertParent((p, v) => G.value(p, compared)(O.compare(v, _) >= 0, false), FW.greaterEqual(compared))

  /** Validates that [[fields.Field]]#value is greater than `compared` */
  def >[C](compared: C)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[S],
      FW: FailWithCompare[E, S],
      C: CompareShow[S, C],
      G: GetValue[P, S, C],
  ): PolicyK[P, F, V, E] = gt[C](compared)

  /** Validates that [[fields.Field]]#value is greater than `compared` */
  def gt[C](compared: C)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[S],
      FW: FailWithCompare[E, S],
      C: CompareShow[S, C],
      G: GetValue[P, S, C],
  ): PolicyK[P, F, V, E] =
    field.assertParent((p, v) => G.value[Boolean](p, compared)(O.compare(v, _) > 0, false), FW.greater(compared))

  /** Validates that [[fields.Field]]#value is less or equal to `compared` */
  def <=[C](compared: C)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[S],
      FW: FailWithCompare[E, S],
      C: CompareShow[S, C],
      G: GetValue[P, S, C],
  ): PolicyK[P, F, V, E] = lte(compared)

  /** Validates that [[fields.Field]]#value is less or equal to `compared` */
  def lte[C](compared: C)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[S],
      FW: FailWithCompare[E, S],
      C: CompareShow[S, C],
      G: GetValue[P, S, C],
  ): PolicyK[P, F, V, E] =
    field.assertParent((p, v) => G.value[Boolean](p, compared)(O.compare(v, _) <= 0, false), FW.lessEqual(compared))

  /** Validates that [[fields.Field]]#value is less than `compared` */
  def <[C](compared: C)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[S],
      FW: FailWithCompare[E, S],
      C: CompareShow[S, C],
      G: GetValue[P, S, C],
  ): PolicyK[P, F, V, E] = lt[C](compared)

  /** Validates that [[fields.Field]]#value is less than `compared` */
  def lt[C](compared: C)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[S],
      FW: FailWithCompare[E, S],
      C: CompareShow[S, C],
      G: GetValue[P, S, C],
  ): PolicyK[P, F, V, E] =
    field.assertParent((p, v) => G.value[Boolean](p, compared)(O.compare(v, _) < 0, false), FW.less(compared))

  /** Validates that [[fields.Field]]#value is  greaterEqual than `from` and lessEqual `to` */
  def isBetween[FROM, TO](from: FROM, to: TO)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[S],
      FW: FailWithCompare[E, S],
      CT: CompareShow[S, TO],
      GT: GetValue[P, S, TO],
      CF: CompareShow[S, FROM],
      GF: GetValue[P, S, FROM],
  ): PolicyK[P, F, V, E] =
    gte[FROM](from) and lte[TO](to)
}
