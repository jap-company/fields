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
import typeclass.*

import scala.annotation.nowarn

trait MapDsl[F[_], V[_], E] {
  implicit final def toMapFieldOps[P, K, S](field: FieldLens[P, Map[K, S]]): MapFieldOps[P, K, S, F, V, E] =
    new MapFieldOps(field)
}

object MapSyntax extends MapSyntax
trait MapSyntax {
  implicit final def toMapFieldOps[P, K, S, F[_], V[_], E](
      field: FieldLens[P, Map[K, S]]
  ): MapFieldOps[P, K, S, F, V, E] =
    new MapFieldOps(field)
}

final class MapFieldOps[P, K, S, F[_], V[_], E](private val lens: FieldLens[P, Map[K, S]]) extends AnyVal {

  /** Checks that collection is empty */
  def isEmpty(implicit R: RuleOps[F, V, E], FW: FailWithEmpty[E, Map[K, P]]): PolicyK[P, F, V, E] =
    lens.assert(_.isEmpty, FW.empty)

  /** Checks that collection is not empty */
  def nonEmpty(implicit R: RuleOps[F, V, E], FW: FailWithNonEmpty[E, Map[K, P]]): PolicyK[P, F, V, E] =
    lens.assert(_.nonEmpty, FW.nonEmpty)

  /** Checks that collection minimum size is `min` */
  def minSize(
      min: => Int
  )(implicit R: RuleOps[F, V, E], FW: FailWithMinSize[E, Map[K, P]]): PolicyK[P, F, V, E] =
    lens.assert(_.size >= min, FW.minSize(min))

  /** Checks that collection maximum size is `max` */
  def maxSize(
      max: => Int
  )(implicit R: RuleOps[F, V, E], FW: FailWithMaxSize[E, Map[K, P]]): PolicyK[P, F, V, E] =
    lens.assert(_.size <= max, FW.maxSize(max))

  /** Applies `check` to each Map element, each should succeed */
  def each(f: PolicyK2[K, S, F, V, E])(implicit
      R: RuleOps[F, V, E],
      PP: PrependPath[E],
      TP: MapKeyToPart[K],
  ): PolicyK[P, F, V, E] =
    v =>
      lens.rule(v)(i =>
        R.andAll(i.zipWithIndex.map { case ((key, value), i) =>
          f(key, value).prependPath(lens.path.down(TP.toPart(key, i)))
        }.toList)
      )

  /** Applies `check` to each Map key, each should succeed */
  def eachKey(f: PolicyK[K, F, V, E])(implicit
      R: RuleOps[F, V, E],
      TP: MapKeyToPart[K],
      PP: PrependPath[E],
  ): PolicyK[P, F, V, E] =
    each((k, _) => f(k))

  /** Applies `check` to each Map value, each should succeed */
  def eachValue(f: PolicyK[S, F, V, E])(implicit
      R: RuleOps[F, V, E],
      TP: MapKeyToPart[K],
      PP: PrependPath[E],
  ): PolicyK[P, F, V, E] =
    each((_, v) => f(v))

  /** Applies `check` to each Map element, any should succeed */
  def any(f: PolicyK2[K, S, F, V, E])(implicit
      R: RuleOps[F, V, E],
      TP: MapKeyToPart[K],
      PP: PrependPath[E],
  ): PolicyK[P, F, V, E] =
    v =>
      lens.rule(v)(i =>
        R.orAll(i.zipWithIndex.map { case ((key, value), i) =>
          f(key, value).prependPath(lens.path.down(TP.toPart(key, i)))
        }.toList)
      )

  /** Applies `check` to each Map key, any should succeed */
  def anyKey(f: PolicyK[K, F, V, E])(implicit
      R: RuleOps[F, V, E],
      TP: MapKeyToPart[K],
      PP: PrependPath[E],
  ): PolicyK[P, F, V, E] =
    any((k, _) => f(k))

  /** Applies `check` to each Map value, any should succeed */
  def anyValue(f: PolicyK[S, F, V, E])(implicit
      R: RuleOps[F, V, E],
      TP: MapKeyToPart[K],
      PP: PrependPath[E],
  ): PolicyK[P, F, V, E] =
    any((_, v) => f(v))

  /** Verifies that collection has distinct elements using `by` property and fails duplicated items using `fail` */
  @nowarn
  def isDistinctBy[SS](by: (K, S) => SS, fail: E)(implicit
      R: RuleOps[F, V, E],
      PP: PrependPath[E],
  ): PolicyK[P, F, V, E] = { v =>
    lens.rule(v) { i =>
      val distinct = i.groupBy(by.tupled).mapValues(_.size == 1)
      each((k, v) => R.assert(fail)(distinct.getOrElse(by(k, v), false))).apply(v)
    }
  }

  /** Verifies that collection has distinct elements and fails duplicated items using `fail` */
  def isDistinct(fail: E)(implicit R: RuleOps[F, V, E], PP: PrependPath[E]): PolicyK[P, F, V, E] =
    isDistinctBy((k, v) => (k, v), fail)
}
