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

import jap.fields.FieldPathConversions._
import jap.fields._
import jap.fields.fail._

case class MediumErrorCode(path: FieldPath, code: Int)
object MediumErrorCode {
  def apply(code: Int)(path: FieldPath): MediumErrorCode = MediumErrorCode(path, code)
}

trait CanFailMediumErrorCode {
  implicit val FailWith: FailWith[MediumErrorCode, Nothing] = FailWithMediumErrorCode
}

object FailWithMediumErrorCode extends FailWith.Base[MediumErrorCode] {
  def empty[P](field: Field[P]): MediumErrorCode                                                  =
    MediumErrorCode(field, 1)
  def minSize[P](size: Int)(field: Field[P]): MediumErrorCode                                     =
    MediumErrorCode(field, 2)
  def invalid[P](field: Field[P]): MediumErrorCode                                                =
    MediumErrorCode(field, 3)
  def nonEmpty[P](field: Field[P]): MediumErrorCode                                               =
    MediumErrorCode(field, 4)
  def maxSize[P](size: Int)(field: Field[P]): MediumErrorCode                                     =
    MediumErrorCode(field, 5)
  def oneOf[P](variants: Seq[P])(field: Field[P]): MediumErrorCode                                =
    MediumErrorCode(field, 7)
  def message[P](error: String, message: Option[String])(field: Field[P]): MediumErrorCode        =
    MediumErrorCode(field, 1)
  def compare[P](operation: CompareOperation, compared: String)(field: Field[P]): MediumErrorCode =
    MediumErrorCode(field, 8)
}
