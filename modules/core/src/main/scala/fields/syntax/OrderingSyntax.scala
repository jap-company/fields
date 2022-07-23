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

trait ModuleOrderingSyntax[F[_], V[_], E] {
  implicit final def toOrderingFieldOps[P](field: Field[P]): OrderingFieldOps[P, F, V, E] =
    new OrderingFieldOps(field)
}
object OrderingSyntax extends OrderingSyntax
trait OrderingSyntax                      {
  implicit final def toOrderingFieldOps[F[_], V[_], E, P](field: Field[P]): OrderingFieldOps[P, F, V, E] =
    new OrderingFieldOps(field)
}
final class OrderingFieldOps[P, F[_], V[_], E](private val field: Field[P]) extends AnyVal {

  /** Validates that [[jap.fields.Field]]#value is greater or equal to `compared` */
  def >=[C](compared: => C)(implicit
      F: Effect[F],
      V: Validated[V],
      O: Ordering[P],
      FW: FailWithCompare[E, P],
      C: FieldCompare[P, C],
  ): Rule[F, V, E] = gte[C](compared)

  /** Validates that [[jap.fields.Field]]#value is greater or equal to `compared` */
  def gte[C](compared: => C)(implicit
      F: Effect[F],
      V: Validated[V],
      O: Ordering[P],
      FW: FailWithCompare[E, P],
      C: FieldCompare[P, C],
  ): Rule[F, V, E] =
    field.ensure(O.gteq(_, C.value(compared)), _.failGreaterEqual(compared))

  /** Validates that [[jap.fields.Field]]#value is greater than `compared` */
  def >[C](compared: => C)(implicit
      F: Effect[F],
      V: Validated[V],
      O: Ordering[P],
      FW: FailWithCompare[E, P],
      C: FieldCompare[P, C],
  ): Rule[F, V, E] = gt[C](compared)

  /** Validates that [[jap.fields.Field]]#value is greater than `compared` */
  def gt[C](compared: => C)(implicit
      F: Effect[F],
      V: Validated[V],
      O: Ordering[P],
      FW: FailWithCompare[E, P],
      C: FieldCompare[P, C],
  ): Rule[F, V, E] =
    field.ensure(O.gt(_, C.value(compared)), _.failGreater(compared))

  /** Validates that [[jap.fields.Field]]#value is less or equal to `compared` */
  def <=[C](compared: => C)(implicit
      F: Effect[F],
      V: Validated[V],
      O: Ordering[P],
      FW: FailWithCompare[E, P],
      C: FieldCompare[P, C],
  ): Rule[F, V, E] = lte(compared)

  /** Validates that [[jap.fields.Field]]#value is less or equal to `compared` */
  def lte[C](compared: => C)(implicit
      F: Effect[F],
      V: Validated[V],
      O: Ordering[P],
      FW: FailWithCompare[E, P],
      C: FieldCompare[P, C],
  ): Rule[F, V, E] =
    field.ensure(O.lteq(_, C.value(compared)), _.failLessEqual(compared))

  /** Validates that [[jap.fields.Field]]#value is less than `compared` */
  def <[C](compared: => C)(implicit
      F: Effect[F],
      V: Validated[V],
      O: Ordering[P],
      FW: FailWithCompare[E, P],
      C: FieldCompare[P, C],
  ): Rule[F, V, E] = lt[C](compared)

  /** Validates that [[jap.fields.Field]]#value is less than `compared` */
  def lt[C](compared: => C)(implicit
      F: Effect[F],
      V: Validated[V],
      O: Ordering[P],
      FW: FailWithCompare[E, P],
      C: FieldCompare[P, C],
  ): Rule[F, V, E] =
    field.ensure(O.lt(_, C.value(compared)), _.failLess(compared))

  /** Validates that [[jap.fields.Field]]#value is  greaterEqual than `from` and lessEqual `to` */
  def isBetween[FROM, TO](from: => FROM, to: => TO)(implicit
      F: Effect[F],
      V: Validated[V],
      O: Ordering[P],
      FW: FailWithCompare[E, P],
      CF: FieldCompare[P, FROM],
      CT: FieldCompare[P, TO],
  ): Rule[F, V, E] =
    Rule.and(gte[FROM](from), lte[TO](to))
}
