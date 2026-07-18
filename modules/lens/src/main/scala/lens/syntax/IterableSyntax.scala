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
import typeclass.PrependPath

import scala.annotation.nowarn

trait IterableDsl[F[_], V[_], E] {
  implicit final def toIterableFieldOps[P, S, C[X] <: Iterable[X]](
      field: FieldLens[P, C[S]]
  ): IterableFieldOps[P, S, C, F, V, E] =
    new IterableFieldOps(field)
}

object IterableSyntax extends IterableSyntax
trait IterableSyntax {
  implicit final def toIterableFieldOps[P, S, C[X] <: Iterable[X], F[_], V[_], E](
      field: FieldLens[P, C[S]]
  ): IterableFieldOps[P, S, C, F, V, E] = new IterableFieldOps(field)
}

final class IterableFieldOps[P, S, C[X] <: Iterable[X], F[_], V[_], E](private val lens: FieldLens[P, C[S]])
    extends AnyVal {

  /** Checks that collection is empty */
  def isEmpty(implicit R: RuleOps[F, V, E], FW: FailWithEmpty[E, C[S]]): PolicyK[P, F, V, E] =
    lens.assert(_.isEmpty, FW.empty)

  /** Checks that collection is not empty */
  def nonEmpty(implicit R: RuleOps[F, V, E], FW: FailWithNonEmpty[E, C[S]]): PolicyK[P, F, V, E] =
    lens.assert(_.nonEmpty, FW.nonEmpty)

  /** Checks that collection minimum size is `min` */
  def minSize(min: => Int)(implicit R: RuleOps[F, V, E], FW: FailWithMinSize[E, C[S]]): PolicyK[P, F, V, E] =
    lens.assert(_.size >= min, FW.minSize(min))

  /** Checks that collection maximum size is `max` */
  def maxSize(max: => Int)(implicit R: RuleOps[F, V, E], FW: FailWithMaxSize[E, C[S]]): PolicyK[P, F, V, E] =
    lens.assert(_.size <= max, FW.maxSize(max))

  /** Applies `check` to each collection element */
  def each(check: PolicyK[S, F, V, E])(implicit R: RuleOps[F, V, E], PP: PrependPath[E]): PolicyK[P, F, V, E] =
    eachWithIndex((v, _) => check(v))

  /** Applies `check` to each collection element */
  def eachWithIndex(
      check: PolicyK2[S, Int, F, V, E]
  )(implicit R: RuleOps[F, V, E], PP: PrependPath[E]): PolicyK[P, F, V, E] =
    v =>
      lens.rule(v)(v =>
        R.andAll(v.zipWithIndex.map { case (p, i) => check(p, i).prependPath(lens.path.down(i)) }.toList)
      )

  /** Applies `check` to each collection element, any should succeed */
  def any(check: PolicyK[S, F, V, E])(implicit R: RuleOps[F, V, E], PP: PrependPath[E]): PolicyK[P, F, V, E] =
    anyWithIndex((v, _) => check(v))

  /** Applies `check` to each collection element, any should succeed */
  def anyWithIndex(
      check: PolicyK2[S, Int, F, V, E]
  )(implicit R: RuleOps[F, V, E], PP: PrependPath[E]): PolicyK[P, F, V, E] =
    v =>
      lens.rule(v)(v =>
        R.orAll(v.zipWithIndex.map { case (p, i) => check(p, i).prependPath(lens.path.down(i)) }.toList)
      )

  /** Verifies that collection has distinct elements using `by` property and fails duplicated items using `fail` */
  @nowarn
  def isDistinctBy[K](by: S => K, fail: => E)(implicit
      R: RuleOps[F, V, E],
      PP: PrependPath[E],
  ): PolicyK[P, F, V, E] = { (v: P) =>
    lens.rule(v) { i =>
      val distinct = i.groupBy(by).mapValues(_.size == 1)
      each(item => R.assert(fail)(distinct.getOrElse(by(item), false))).apply(v)
    }
  }

  /** Verifies that collection has distinct elements and fails duplicated items using `fail` */
  def isDistinct(fail: E)(implicit R: RuleOps[F, V, E], PP: PrependPath[E]): PolicyK[P, F, V, E] =
    isDistinctBy(identity, fail)
}
