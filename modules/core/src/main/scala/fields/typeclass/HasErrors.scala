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
package typeclass

import jap.fields.data._

/** Type class that helps extracting errors as list */
trait HasErrors[V[_]] {

  /** Returns `v` errors as [[scala.collection.immutable.List]] */
  def errors[E](v: V[E]): List[E]
}

object HasErrors {
  implicit object AccumulateHasErrors extends HasErrors[Accumulate] {
    def errors[E](v: Accumulate[E]): List[E] = v match {
      case Accumulate.Valid           => Nil
      case Accumulate.Invalid(errors) => errors
    }
  }

  implicit object FailFastHasErrors extends HasErrors[FailFast] {
    def errors[E](v: FailFast[E]): List[E] = v.option.toList
  }

  implicit object ListHasErrors extends HasErrors[List] {
    def errors[E](v: List[E]): List[E] = v
  }

  implicit object OptionHasErrors extends HasErrors[Option] {
    def errors[E](v: Option[E]): List[E] = v.toList
  }

  implicit object EitherHasErrors extends HasErrors[Either[_, Nothing]] {
    def errors[E](v: Either[E, Nothing]): List[E] = v.left.toSeq.toList
  }
}
