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
package circe

import io.circe._
import jap.fields.CatsInterop.DefaultValidatedNelVM._
import jap.fields.CirceInterop._
import jap.fields._

case class Form(nested: FormNested)
case class FormNested(list: List[Int], map: Map[String, String])

object FormNested {
  implicit val decoder: Decoder[FormNested] = Decoder.forProduct2("list", "map")(FormNested.apply)
}

object Form {
  implicit val policy: Policy[Form] =
    Policy
      .builder[Form]
      .subRule(_.nested.list)(
        _.minSize(2),
        _.each(_.all(_ > 1, _.ensure(_ != -1, _.failMessage("Cannot be -1")))),
      )
      .subRule(_.nested.map)(
        _.minSize(2),
        _.eachValue(_ !== "value"),
      )
      .build

  implicit val decoder: Decoder[Form] = Decoder.forProduct1("nested")(Form.apply).usePolicy(policy)
}

object CirceExample {
  showBuildInfo()

  final def main(args: Array[String]): Unit = {
    val json = Json.obj(
      "nested" -> Json.obj(
        "list" -> Json.arr(Json.fromInt(-1)),
        "map"  -> Json.obj("key" -> Json.fromString("value")),
      )
    )

    println(Form.decoder.decodeAccumulating(json.hcursor))
  }
}
