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
import scala.collection.generic.IsIterable

import typeclass._
import fail._

trait ModuleIterableSyntax[F[_], V[_], E] { self: ValidationModule[F, V, E] =>
  extension [P](field: Field[P])(using II: IsIterable[P], F: Effect[F], V: Validated[V]) {

    /** Checks that collection is empty */
    def isEmpty(using FW: FailWithNonEmpty[E, P]): Rule[F, V, E] =
      Rule.ensure(field.failNonEmpty)(II(field.value).isEmpty)

    /** Checks that collection is not empty */
    def nonEmpty(using FW: FailWithEmpty[E, P]): Rule[F, V, E] =
      Rule.ensure(field.failEmpty)(II(field.value).nonEmpty)

    /** Checks that collection minimum size is `min` */
    def minSize(min: Int)(using FW: FailWithMinSize[E, P]): Rule[F, V, E] =
      Rule.ensure(field.failMinSize(min))(II(field.value).size >= min)

    /** Checks that collection maximum size is `max` */
    def maxSize(max: Int)(using FW: FailWithMaxSize[E, P]): Rule[F, V, E] =
      Rule.ensure(field.failMaxSize(max))(II(field.value).size <= max)

    /** Applies `check` to each collection element, each should succeed */
    def each(check: Field[II.A] => Rule[F, V, E]): Rule[F, V, E] = eachWithIndex((f, _) => check(f))

    /** Applies `check` to each collection element, each should succeed */
    @nowarn
    def eachWithIndex(check: (Field[II.A], Int) => Rule[F, V, E]): Rule[F, V, E] =
      Rule.andAll(II(field.value).zipWithIndex.map { case (p: P, i) =>
        check(field.provideSub(i.toString, p), i)
      }.toList)

    /** Applies `check` to each collection element, any should succeed */
    def any(check: Field[II.A] => Rule[F, V, E]): Rule[F, V, E] = anyWithIndex((f, _) => check(f))

    /** Applies `check` to each collection element, any should succeed */
    @nowarn
    def anyWithIndex(check: (Field[II.A], Int) => Rule[F, V, E]): Rule[F, V, E] =
      Rule.orAll(II(field.value).zipWithIndex.map { case (p: P, i) =>
        check(field.provideSub(i.toString, p), i)
      }.toList)
  }
}
