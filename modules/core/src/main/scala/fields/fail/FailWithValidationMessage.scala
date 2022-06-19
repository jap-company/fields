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
