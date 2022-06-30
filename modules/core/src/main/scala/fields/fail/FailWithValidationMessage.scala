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

import FieldPathConversions._
import error.ValidationMessage
import error.{ValidationTypes => Types}
import error.{ValidationMessages => Messages}

trait CanFailWithValidationMessage {
  implicit def failWith: FailWith.Base[ValidationMessage] = FailWithValidationMessage
}
object FailWithValidationMessage extends FailWith.Base[ValidationMessage] {
  def invalid[A](field: Field[A]): ValidationMessage  = ValidationMessage(field, Types.Invalid, Messages.Invalid)
  def empty[A](field: Field[A]): ValidationMessage    = ValidationMessage(field, Types.Empty, Messages.Empty)
  def nonEmpty[A](field: Field[A]): ValidationMessage = ValidationMessage(field, Types.NonEmpty, Messages.NonEmpty)
  def minSize[A](size: Int)(field: Field[A]): ValidationMessage                                     =
    ValidationMessage(field, Types.MinSize, Messages.MinSize(size))
  def maxSize[A](size: Int)(field: Field[A]): ValidationMessage                                     =
    ValidationMessage(field, Types.MaxSize, Messages.MaxSize(size))
  def compare[A](operation: CompareOperation, compared: String)(field: Field[A]): ValidationMessage =
    operation match {
      case CompareOperation.Equal        =>
        ValidationMessage(field, Types.Equal, Messages.Equal(compared))
      case CompareOperation.NotEqual     =>
        ValidationMessage(field, Types.NotEqual, Messages.NotEqual(compared))
      case CompareOperation.Greater      =>
        ValidationMessage(field, Types.Greater, Messages.Greater(compared))
      case CompareOperation.GreaterEqual =>
        ValidationMessage(field, Types.GreaterEqual, Messages.GreaterEqual(compared))
      case CompareOperation.Less         =>
        ValidationMessage(field, Types.Less, Messages.Less(compared))
      case CompareOperation.LessEqual    =>
        ValidationMessage(field, Types.LessEqual, Messages.LessEqual(compared))
    }
  def message[A](error: String, message: Option[String])(field: Field[A]): ValidationMessage        =
    ValidationMessage(field, error, message)
  def oneOf[A](variants: Seq[A])(field: Field[A]): ValidationMessage                                =
    ValidationMessage(field, Types.OneOf, Some(Messages.OneOf(variants.map(_.toString))))
}
