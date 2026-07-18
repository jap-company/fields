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

import scala.annotation.nowarn

trait IterableDsl[F[_], V[_], E] {
  implicit final def toIterableFieldOps[P, C[X] <: Iterable[X]](
      field: Field[C[P]]
  ): IterableFieldOps[P, C, F, V, E] =
    new IterableFieldOps(field)
}

object IterableSyntax extends IterableSyntax
trait IterableSyntax {
  implicit final def toIterableFieldOps[F[_], V[_], E, P, C[X] <: Iterable[X]](
      field: Field[C[P]]
  ): IterableFieldOps[P, C, F, V, E] = new IterableFieldOps(field)
}

trait BaseFieldOps[F[_], V[_], E] extends Any {
  type Rule            = RuleK[F, V, E]
  type Ops             = RuleOps[F, V, E]
  type Policy[-P]      = PolicyK[Field[P], F, V, E]
  type Policy2[-A, -B] = PolicyK2[Field[A], B, F, V, E]
}

final class IterableFieldOps[P, C[X] <: Iterable[X], F[_], V[_], E](private val field: Field[C[P]])
    extends AnyVal
    with BaseFieldOps[F, V, E] {

  /** Checks that collection is empty */
  def isEmpty(implicit R: Ops, FW: FailWithEmpty[E, C[P]]): Rule =
    field.assert(_.isEmpty, FW.empty)

  /** Checks that collection is not empty */
  def nonEmpty(implicit R: Ops, FW: FailWithNonEmpty[E, C[P]]): Rule =
    field.assert(_.nonEmpty, FW.nonEmpty)

  /** Checks that collection minimum size is `min` */
  def minSize(min: => Int)(implicit R: Ops, FW: FailWithMinSize[E, C[P]]): Rule =
    field.assert(_.size >= min, FW.minSize(min))

  /** Checks that collection maximum size is `max` */
  def maxSize(max: => Int)(implicit R: Ops, FW: FailWithMaxSize[E, C[P]]): Rule =
    field.assert(_.size <= max, FW.maxSize(max))

  /** Applies `check` to each collection element */
  def each(check: Policy[P])(implicit R: Ops): Rule =
    eachWithIndex((f, _) => check(f))

  /** Applies `check` to each collection element */
  def eachWithIndex(check: Policy2[P, Int])(implicit R: Ops): Rule =
    R.andAll(field.value.zipWithIndex.map { case (p, i) => check(field.down(i, p), i) }.toList)

  /** Applies `check` to each collection element, any should succeed */
  def any(check: Policy[P])(implicit R: Ops): Rule =
    anyWithIndex((f, _) => check(f))

  /** Applies `check` to each collection element, any should succeed */
  def anyWithIndex(check: Policy2[P, Int])(implicit R: Ops): Rule =
    R.orAll(field.value.zipWithIndex.map { case (p, i) => check(field.down(i, p), i) }.toList)

  /** Verifies that collection has distinct elements using `by` property and fails duplicated items using `fail` */
  @nowarn
  def isDistinctBy[K](by: P => K, fail: Field[P] => E)(implicit R: Ops): Rule = {
    val distinct = field.value.groupBy(by).mapValues(_.size == 1)
    each(item => R.assert(fail(item))(distinct.getOrElse(by(item.value), false)))
  }

  /** Verifies that collection has distinct elements and fails duplicated items using `fail` */
  def isDistinct(fail: Field[P] => E)(implicit R: Ops): Rule =
    isDistinctBy(identity, fail)
}
