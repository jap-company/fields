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

import scala.annotation.nowarn

import typeclass._
import fail._

trait ModuleIterableSyntax[F[_], V[_], E] { self: ValidationModule[F, V, E] =>
  implicit final def toIterableFieldOps[I[_] <: Iterable[?], P](field: Field[I[P]]): IterableFieldOps[I, P, F, V, E] =
    new IterableFieldOps(field)
}

object IterableSyntax extends IterableSyntax
trait IterableSyntax {
  implicit final def toIterableFieldOps[F[_], V[_], E, I[_] <: Iterable[?], P](
      field: Field[I[P]]
  ): IterableFieldOps[I, P, F, V, E] = new IterableFieldOps(field)
}

final class IterableFieldOps[I[_] <: Iterable[?], P, F[_], V[_], E](private val field: Field[I[P]]) extends AnyVal {

  /** Checks that collection is empty */
  def isEmpty(implicit F: Effect[F], V: Validated[V], FW: FailWithEmpty[E, I[P]]): Rule[F, V, E] =
    Rule.ensure(field.failEmpty)(field.value.isEmpty)

  /** Checks that collection is not empty */
  def nonEmpty(implicit F: Effect[F], V: Validated[V], FW: FailWithNonEmpty[E, I[P]]): Rule[F, V, E] =
    Rule.ensure(field.failNonEmpty)(field.value.isEmpty)

  /** Checks that collection minimum size is `min` */
  def minSize(min: => Int)(implicit F: Effect[F], V: Validated[V], FW: FailWithMinSize[E, I[P]]): Rule[F, V, E] =
    Rule.ensure(field.failMinSize(min))(field.value.size >= min)

  /** Checks that collection maximum size is `max` */
  def maxSize(max: => Int)(implicit F: Effect[F], V: Validated[V], FW: FailWithMaxSize[E, I[P]]): Rule[F, V, E] =
    Rule.ensure(field.failMaxSize(max))(field.value.size <= max)

  /** Applies `check` to each collection element */
  def each(check: Field[P] => Rule[F, V, E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    eachWithIndex((f, _) => check(f))

  /** Applies `check` to each collection element */
  @nowarn
  def eachWithIndex(check: (Field[P], Int) => Rule[F, V, E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.andAll(field.value.zipWithIndex.map { case (p: P, i) => check(field.provideSub(i.toString, p), i) }.toList)

  /** Applies `check` to each collection element, any should succeed */
  def any(check: Field[P] => Rule[F, V, E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    anyWithIndex((f, _) => check(f))

  /** Applies `check` to each collection element, any should succeed */
  @nowarn
  def anyWithIndex(check: (Field[P], Int) => Rule[F, V, E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.orAll(field.value.zipWithIndex.map { case (p: P, i) => check(field.provideSub(i.toString, p), i) }.toList)
}