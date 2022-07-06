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

import typeclass._

trait ModuleMapSyntax[F[_], V[_], E] {
  implicit final def toMapFieldOps[K, P](field: Field[Map[K, P]]): MapFieldOps[K, P, F, V, E] =
    new MapFieldOps(field)
}

object MapSyntax extends MapSyntax
trait MapSyntax {
  implicit final def toMapFieldOps[F[_], V[_], E, K, P](field: Field[Map[K, P]]): MapFieldOps[K, P, F, V, E] =
    new MapFieldOps(field)
}

final class MapFieldOps[K, P, F[_], V[_], E](private val field: Field[Map[K, P]]) extends AnyVal {

  /** Applies `check` to each Map element, each should succeed */
  def each(f: Field[(K, P)] => Rule[F, V, E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.andAll(field.value.map { case entry @ (key, _) => f(field.provideSub(key.toString, entry)) }.toList)

  /** Applies `check` to each Map key, each should succeed */
  def eachKey(check: Field[K] => Rule[F, V, E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    each(e => check(e.first))

  /** Applies `check` to each Map value, each should succeed */
  def eachValue(check: Field[P] => Rule[F, V, E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    each(e => check(e.second))

  /** Applies `check` to each Map element, any should succeed */
  def any(check: Field[(K, P)] => Rule[F, V, E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    Rule.orAll(field.value.map { case entry @ (key, _) => check(field.provideSub(key.toString, entry)) }.toList)

  /** Applies `check` to each Map key, any should succeed */
  def anyKey(check: Field[K] => Rule[F, V, E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    any(e => check(e.first))

  /** Applies `check` to each Map value, any should succeed */
  def anyValue(check: Field[P] => Rule[F, V, E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
    any(e => check(e.second))
}
