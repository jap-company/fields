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
package lens.syntax

import fields.lens.FieldLens
import fields.lens.FieldsDsl.default.*
import scala.compiletime.*
import scala.deriving.*

object PolicyDeriveSyntax {
  inline def summonOrDefault[T](default: => T): T =
    summonFrom {
      case t: T => t
      case _    => default
    }

  /** Summon a tuple of `LensPolicy[_]` matching the tuple of field types `Elems`. */
  inline def summonPolicies[Elems <: Tuple]: Tuple =
    inline erasedValue[Elems] match
      case _: (h *: t)   => summonOrDefault[LensPolicy[h]](LensPolicy.none[h]) *: summonPolicies[t]
      case _: EmptyTuple => EmptyTuple

  /** Build a field accessor by index using Product.productElement. */

  /** Fold over the tuple of field policies, chaining `subRule`s. */
  inline def chainSubRules[T, Elems <: Tuple](p0: LensPolicy[T], policies: Tuple, inline idx: Int = 0): LensPolicy[T] =
    policies match
      case EmptyTuple   =>
        p0
      case head *: tail =>
        // Defer to the step that recovers the head element's static type from `Elems`
        chainSubRules_step[T, Elems](p0, head.asInstanceOf[LensPolicy[Any]], tail, idx)

  inline def chainSubRules_step[T, Elems <: Tuple](
      p0: LensPolicy[T],
      head: LensPolicy[Any],
      tail: Tuple,
      inline idx: Int,
  ): LensPolicy[T] =
    inline erasedValue[Elems] match
      case _: (a *: rest) =>
        // subRule for the current field
        val fieldsPolicy: LensPolicy[T] = LensPolicy[T] { implicit base =>
          val sl         = FieldLens[T](inferBasePath).sub[a](GetByIndexMacro.getterByIndex[T, a](idx))
          val headPolicy = head.asInstanceOf[LensPolicy[a]].apply(sl)
          _ and headPolicy
        }
        val p1: LensPolicy[T]           = p0.and(fieldsPolicy)
        // continue with the rest
        chainSubRules[T, rest](p1, tail, idx + 1)
      case _: EmptyTuple  =>
        // unreachable for well-formed calls
        p0

  /** The derived given instance. */
  inline given derived[T](using m: Mirror.ProductOf[T]): LensPolicy[T] =
    // summon per-field policies in tuple order (m.MirroredElemTypes)
    val fieldPolicies = summonPolicies[m.MirroredElemTypes]
    // start from an empty/root LensPolicy[T] and attach sub-rules for each field
    chainSubRules[T, m.MirroredElemTypes](LensPolicy.root(_ => Policy[T]), fieldPolicies)
}
