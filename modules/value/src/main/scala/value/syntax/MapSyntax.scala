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
import typeclass.*
import value.syntax.GenericSyntax.*

import scala.annotation.nowarn

trait MapDsl[F[_], V[_], E] {
  implicit final def toMapFieldOps[K, P](field: Field[Map[K, P]]): MapFieldOps[K, P, F, V, E] =
    new MapFieldOps(field)
}

object MapSyntax extends MapSyntax
trait MapSyntax {
  implicit final def toMapFieldOps[F[_], V[_], E, K, P](field: Field[Map[K, P]]): MapFieldOps[K, P, F, V, E] =
    new MapFieldOps(field)
}

final class MapFieldOps[K, P, F[_], V[_], E](private val field: Field[Map[K, P]]) extends AnyVal {

  /** Checks that collection is empty */
  def isEmpty(implicit R: RuleOps[F, V, E], FW: FailWithEmpty[E, Map[K, P]]): RuleK[F, V, E] =
    field.assert(_.isEmpty, FW.empty)

  /** Checks that collection is not empty */
  def nonEmpty(implicit R: RuleOps[F, V, E], FW: FailWithNonEmpty[E, Map[K, P]]): RuleK[F, V, E] =
    field.assert(_.nonEmpty, FW.nonEmpty)

  /** Checks that collection minimum size is `min` */
  def minSize(min: => Int)(implicit R: RuleOps[F, V, E], FW: FailWithMinSize[E, Map[K, P]]): RuleK[F, V, E] =
    field.assert(_.size >= min, FW.minSize(min))

  /** Checks that collection maximum size is `max` */
  def maxSize(max: => Int)(implicit R: RuleOps[F, V, E], FW: FailWithMaxSize[E, Map[K, P]]): RuleK[F, V, E] =
    field.assert(_.size <= max, FW.maxSize(max))

  /** Applies `check` to each Map element, each should succeed */
  def each(f: Field[(K, P)] => RuleK[F, V, E])(implicit
      R: RuleOps[F, V, E],
      TP: MapKeyToPart[K],
  ): RuleK[F, V, E] =
    R.andAll(field.value.zipWithIndex.map { case (entry @ (key, _), index) =>
      f(field.down(TP.toPart(key, index), entry))
    }.toList)

  /** Applies `check` to each Map key, each should succeed */
  def eachKey(check: Field[K] => RuleK[F, V, E])(implicit
      R: RuleOps[F, V, E],
      TP: MapKeyToPart[K],
  ): RuleK[F, V, E] =
    each(e => check(e.first))

  /** Applies `check` to each Map value, each should succeed */
  def eachValue(check: Field[P] => RuleK[F, V, E])(implicit
      R: RuleOps[F, V, E],
      TP: MapKeyToPart[K],
  ): RuleK[F, V, E] =
    each(e => check(e.second))

  /** Applies `check` to each Map element, any should succeed */
  def any(check: Field[(K, P)] => RuleK[F, V, E])(implicit
      R: RuleOps[F, V, E],
      TP: MapKeyToPart[K],
  ): RuleK[F, V, E] =
    R.orAll(field.value.zipWithIndex.map { case (entry @ (key, _), index) =>
      check(field.down(TP.toPart(key, index), entry))
    }.toList)

  /** Applies `check` to each Map key, any should succeed */
  def anyKey(check: Field[K] => RuleK[F, V, E])(implicit
      R: RuleOps[F, V, E],
      TP: MapKeyToPart[K],
  ): RuleK[F, V, E] =
    any(e => check(e.first))

  /** Applies `check` to each Map value, any should succeed */
  def anyValue(check: Field[P] => RuleK[F, V, E])(implicit
      R: RuleOps[F, V, E],
      TP: MapKeyToPart[K],
  ): RuleK[F, V, E] =
    any(e => check(e.second))

  /** Verifies that collection has distinct elements using `by` property and fails duplicated items using `fail` */
  @nowarn
  def isDistinctBy[S](by: ((K, P)) => S, fail: Field[(K, P)] => V[E])(implicit R: RuleOps[F, V, E]) = {
    val distinct = field.value.groupBy(by).mapValues(_.size == 1)
    each(item => R.ensure(fail(item))(distinct.getOrElse(by(item.value), false)))
  }

  /** Verifies that collection has distinct elements and fails duplicated items using `fail` */
  def isDistinct(fail: Field[(K, P)] => V[E])(implicit R: RuleOps[F, V, E]) = isDistinctBy(identity, fail)
}
