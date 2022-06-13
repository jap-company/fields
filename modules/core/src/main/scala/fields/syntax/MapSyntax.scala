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

trait MapSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toMapFieldOps[K, V](field: Field[Map[K, V]]): MapFieldOps[K, V, F, VR, E] =
    new MapFieldOps(field)
}

final class MapFieldOps[K, V, F[_], VR[_], E](private val field: Field[Map[K, V]]) extends AnyVal {

  /** Applies `check` to each Map element, each should succeed */
  def each(f: Field[(K, V)] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.andAll(field.value.zipWithIndex.map { case (t, i) => f(field.provideSub(i.toString, t)) }.toList)

  /** Applies `check` to each Map key, each should succeed */
  def eachKey(check: Field[K] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    each(e => check(e.first))

  /** Applies `check` to each Map value, each should succeed */
  def eachValue(check: Field[V] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    each(e => check(e.second))

  /** Applies `check` to each Map element, any should succeed */
  def any(check: Field[(K, V)] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.orAll(field.value.zipWithIndex.map { case (t, i) => check(field.provideSub(i.toString, t)) }.toList)

  /** Applies `check` to each Map key, any should succeed */
  def anyKey(check: Field[K] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    any(e => check(e.first))

  /** Applies `check` to each Map value, any should succeed */
  def anyValue(check: Field[V] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    any(e => check(e.second))
}
