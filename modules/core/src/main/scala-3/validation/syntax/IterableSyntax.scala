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

trait IterableSyntax[F[_], VR[_], E] { self: ValidationModule[F, VR, E] =>
  extension [P](field: Field[P])(using II: IsIterable[P], M: ValidationModule[F, VR, E]) {

    /** Checks that collection is empty */
    def isEmpty(using FW: FailWithNonEmpty[E]): F[VR[E]] = M.fieldAssert(field, II(_).isEmpty, FW.nonEmpty)

    /** Checks that collection is not empty */
    def nonEmpty(using FW: FailWithEmpty[E]): F[VR[E]] = M.fieldAssert(field, II(_).nonEmpty, FW.empty)

    /** Checks that collection minimum size is `min` */
    def minSize(min: Int)(using FW: FailWithMinSize[E]): F[VR[E]] =
      M.fieldAssert(field, II(_).size >= min, FW.minSize(min))

    /** Checks that collection maximum size is `max` */
    def maxSize(max: Int)(using FW: FailWithMaxSize[E]): F[VR[E]] =
      M.fieldAssert(field, II(_).size <= max, FW.maxSize(max))

    /** Applies `check` to each collection element, each should succeed */
    def each(check: Field[II.A] => F[VR[E]]): F[VR[E]] = eachWithIndex((f, _) => check(f))

    /** Applies `check` to each collection element, each should succeed */
    @nowarn
    def eachWithIndex(check: (Field[II.A], Int) => F[VR[E]]): F[VR[E]] =
      M.andAll(II(field.value).zipWithIndex.map { case (p: P, i) =>
        check(field.provideSub(i.toString, p), i)
      }.toList)

    /** Applies `check` to each collection element, any should succeed */
    def any(check: Field[II.A] => F[VR[E]]): F[VR[E]] = anyWithIndex((f, _) => check(f))

    /** Applies `check` to each collection element, any should succeed */
    @nowarn
    def anyWithIndex(check: (Field[II.A], Int) => F[VR[E]]): F[VR[E]] =
      M.orAll(II(field.value).zipWithIndex.map { case (p: P, i) =>
        check(field.provideSub(i.toString, p), i)
      }.toList)
  }
}
