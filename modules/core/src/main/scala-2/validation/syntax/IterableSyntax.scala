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

trait IterableSyntax[F[_], VR[_], E] { self: ValidationModule[F, VR, E] =>
  implicit final def toIterableFieldOps[I[_] <: Iterable[?], P](field: Field[I[P]]): IterableFieldOps[I, P, F, VR, E] =
    new IterableFieldOps(field)
}

final class IterableFieldOps[I[_] <: Iterable[?], P, F[_], VR[_], E](private val field: Field[I[P]]) extends AnyVal {

  /** Checks that collection is empty */
  def isEmpty(implicit M: ValidationModule[F, VR, E], FW: FailWithNonEmpty[E]): F[VR[E]] =
    M.fieldAssert[I[P]](field, _.isEmpty, FW.nonEmpty[I[P]])

  /** Checks that collection is not empty */
  def nonEmpty(implicit M: ValidationModule[F, VR, E], FW: FailWithEmpty[E]): F[VR[E]] =
    M.fieldAssert[I[P]](field, _.nonEmpty, FW.empty[I[P]])

  /** Checks that collection minimum size is `min` */
  def minSize(min: Int)(implicit M: ValidationModule[F, VR, E], FW: FailWithMinSize[E]): F[VR[E]] =
    M.fieldAssert[I[P]](field, _.size >= min, FW.minSize[I[P]](min))

  /** Checks that collection maximum size is `max` */
  def maxSize(max: Int)(implicit M: ValidationModule[F, VR, E], FW: FailWithMaxSize[E]): F[VR[E]] =
    M.fieldAssert[I[P]](field, _.size <= max, FW.maxSize[I[P]](max))

  /** Applies `check` to each collection element */
  def each(check: Field[P] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    eachWithIndex((f, _) => check(f))

  /** Applies `check` to each collection element */
  @nowarn
  def eachWithIndex(check: (Field[P], Int) => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.andAll(field.value.zipWithIndex.map { case (p: P, i) => check(field.provideSub(i.toString, p), i) }.toList)

  /** Applies `check` to each collection element, any should succeed */
  def any(check: Field[P] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    anyWithIndex((f, _) => check(f))

  /** Applies `check` to each collection element, any should succeed */
  @nowarn
  def anyWithIndex(check: (Field[P], Int) => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.orAll(field.value.zipWithIndex.map { case (p: P, i) => check(field.provideSub(i.toString, p), i) }.toList)
}
