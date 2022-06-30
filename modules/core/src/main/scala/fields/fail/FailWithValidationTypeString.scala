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
package fail

import error.ValidationTypes

trait CanFailWithValidationTypeString {
  implicit def failWith: FailWith.Base[String] = FailWithValidationTypeString
}

/** String [[FailWith]] instance where errors are just error types that occured */
object FailWithValidationTypeString extends FailWith.Base[String] {
  def invalid[P](field: Field[P]): String                                                = ValidationTypes.Invalid
  def empty[P](field: Field[P]): String                                                  = ValidationTypes.Empty
  def nonEmpty[P](field: Field[P]): String                                               = ValidationTypes.NonEmpty
  def minSize[P](size: Int)(field: Field[P]): String                                     = ValidationTypes.MinSize
  def maxSize[P](size: Int)(field: Field[P]): String                                     = ValidationTypes.MaxSize
  def oneOf[P](variants: Seq[P])(field: Field[P]): String                                = ValidationTypes.OneOf
  def message[P](error: String, message: Option[String])(field: Field[P]): String        = error
  def compare[P](operation: CompareOperation, compared: String)(field: Field[P]): String = operation.constraint
}
