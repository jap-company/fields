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
package data

import typeclass._

/** Accumulate [[jap.fields.typeclass.Validated]] implementation */
sealed trait Accumulate[+E] {
  def mapError[EE >: E, B](f: E => B): Accumulate[B] =
    this match {
      case Accumulate.Valid           => Accumulate.Valid
      case Accumulate.Invalid(errors) => Accumulate.Invalid(errors.map(f))
    }
}
object Accumulate extends AccumulateLike[Accumulate] {
  case object Valid                       extends Accumulate[Nothing]
  case class Invalid[+E](errors: List[E]) extends Accumulate[E]
  def valid[E]: Accumulate[E]                                   = Valid
  def invalid[E](e: E): Accumulate[E]                           = Invalid(List(e))
  def isValid[E](v: Accumulate[E]): Boolean                     = v == Valid
  def and[E](a: Accumulate[E], b: Accumulate[E]): Accumulate[E] =
    (a, b) match {
      case (Valid, Valid)                   => Valid
      case (aa: Invalid[E], bb: Invalid[E]) => Invalid(aa.errors ++ bb.errors)
      case (_: Invalid[E], _)               => a
      case _                                => b
    }

  implicit val accumulateValidated: Validated[Accumulate] = this
}
