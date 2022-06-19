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

import scala.annotation.implicitNotFound

/** Typeclass that is used to allow using compare syntax with both Field[P] and P itself. Not needed in case Scala 2
  * support will be dropped in future.
  */
@implicitNotFound("Cannot compare ${P} with ${C}")
trait FieldCompare[P, C] {
  def value(c: C): P
  def show(compared: C): String
}

object FieldCompare extends FieldCompareInstances0 with FieldCompareInstances1 {
  def apply[P, C](implicit C: FieldCompare[P, C]): FieldCompare[P, C] = C
}
trait FieldCompareInstances1 {
  implicit def valueCompare[P, C <: P]: FieldCompare[P, C] = ValueCompared.asInstanceOf[FieldCompare[P, C]]

  object ValueCompared extends FieldCompare[Any, Any] {
    def value(c: Any): Any        = c
    def show(c: Any): String      = c.toString
    override val toString: String = "VALUE"
  }
}

trait FieldCompareInstances0 {
  implicit def fieldCompare[P, C <: P]: FieldCompare[P, Field[C]] =
    FieldCompared.asInstanceOf[FieldCompare[P, Field[C]]]

  object FieldCompared extends FieldCompare[Any, Field[Any]] {
    def value(c: Field[Any]): Any   = c.value
    def show(c: Field[Any]): String = c.path.full
    override val toString: String   = "FIELD"
  }
}
