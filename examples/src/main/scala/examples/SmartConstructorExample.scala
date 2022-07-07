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
package smart

import cats.data.NonEmptyList
import cats.data.Validated
import jap.fields.CatsInterop.DefaultValidatedNelVM._
import jap.fields._
import jap.fields.error.ValidationError

sealed abstract case class NyanCat private (
    name: String,
    colors: List[String],
)
object NyanCat                 {
  val RainbowColors = List("red", "orange", "yellow", "green", "blue", "indigo", "violet")

  implicit val policy: Policy[NyanCat] =
    Policy
      .builder[NyanCat]
      .subRule(_.name)(_.minSize(2))
      .subRule(_.colors)(
        _.minSize(1),
        _.each(_.in(RainbowColors)),
        _.isDistinct(_.failMessage("Nyan cat must have unique colors")),
      )
      .build

  def apply(
      name: String,
      colors: List[String],
  ): Validated[NonEmptyList[ValidationError], NyanCat] = {
    val nyanCat = new NyanCat(name, colors) {}
    Field(nyanCat).validate.effect.value.map(_ => nyanCat)
  }
}

/** Also Fields is Validation library you may use it in pair with smart-constructors */
object SmartConstructorExample {
  showBuildInfo()

  final def main(args: Array[String]): Unit = {
    val nyanCat = NyanCat("", List("blue", "blue"))
    println(nyanCat)
  }
}
