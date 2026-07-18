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

package examples
package tagless

import fields.*
import fields.fail.*
import fields.typeclass.Effect.Sync
import fields.value.FieldsDsl

// You like magic codes?)
case class ERR(code: Int)

object FailWithErr extends FailWith[ERR, Nothing] {
  def message[P](error: => String, message: => Option[String])(path: FieldPath, value: P): ERR  = ERR(error.toInt)
  def invalid[P](path: FieldPath, value: P): ERR                                                = ERR(2)
  def empty[P](path: FieldPath, value: P): ERR                                                  = ERR(3)
  def oneOf[P](variants: Seq[P])(path: FieldPath, value: P): ERR                                = ERR(4)
  def minSize[P](size: Int)(path: FieldPath, value: P): ERR                                     = ERR(5)
  def compare[P](operation: CompareOperation, compared: String)(path: FieldPath, value: P): ERR = ERR(6)
  def nonEmpty[P](path: FieldPath, value: P): ERR                                               = ERR(7)
  def maxSize[P](size: Int)(path: FieldPath, value: P): ERR                                     = ERR(8)
}

object Validation extends FieldsDsl.BaseAccumulate[Sync, ERR] with FailWithErr.Mixin {
  implicit object FailWithEmptyString extends FailWithCompare[ERR, String] {
    override def compare[P >: String](operation: CompareOperation, compared: String)(path: FieldPath, value: P): ERR =
      ERR(44)
  }
}

import Validation.*

object FailWithOverrideExample {
  showBuildInfo()

  implicit final def main(args: Array[String]): Unit = {
    val intF    = Field(1)
    val stringF = Field("asd")

    println("intF.equalTo - " + intF.equalTo(2).errors)
    println("intF.gt - " + intF.gt(2).errors)
    println("stringF.equalTo - " + stringF.equalTo("").errors)
    println("stringF.gt - " + stringF.gt("b").errors)
  }
}
