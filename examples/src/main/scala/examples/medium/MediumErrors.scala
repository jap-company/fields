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
package examples
package medium

import jap.fields.typeclass._

import scala.util.control.NoStackTrace

case class MediumErrors[E](errors: List[E]) extends Throwable with NoStackTrace {
  override def toString = errors.mkString("ERRORS:\n\t", "\n\t", "\n")
}
object MediumErrors {
  implicit object MediumErrorsValidated extends AccumulateLike[MediumErrors] {
    def valid[E]: MediumErrors[E]                                         = MediumErrors(Nil)
    def invalid[E](e: E): MediumErrors[E]                                 = MediumErrors(e :: Nil)
    def and[E](va: MediumErrors[E], vb: MediumErrors[E]): MediumErrors[E] = MediumErrors(va.errors ++ vb.errors)
    def isValid[E](v: MediumErrors[E]): Boolean                           = v.errors.isEmpty
  }
}
