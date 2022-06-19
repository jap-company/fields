package jap.fields
package fail

import FieldPathConversions.fromField
import error.ValidationError
import error.ValidationError._

trait CanFailWithValidationError {
  implicit def failWith: FailWith.Base[ValidationError] = FailWithValidationError
}
object FailWithValidationError extends FailWith.Base[ValidationError] {
  def invalid[A](field: Field[A]): ValidationError                 = Invalid(field)
  def empty[A](field: Field[A]): ValidationError                   = Empty(field)
  def nonEmpty[A](field: Field[A]): ValidationError                = NonEmpty(field)
  def minSize[A](size: Int)(field: Field[A]): ValidationError      = MinSize(field, size)
  def maxSize[A](size: Int)(field: Field[A]): ValidationError      = MaxSize(field, size)
  def oneOf[A](variants: Seq[A])(field: Field[A]): ValidationError = OneOf(field, variants.map(_.toString))
  def message[A](error: String, message: Option[String])(field: Field[A]): ValidationError        =
    Message(field, error, message)
  def compare[A](operation: CompareOperation, compared: String)(field: Field[A]): ValidationError = {
    operation match {
      case CompareOperation.Equal        => Equal(field, compared)
      case CompareOperation.NotEqual     => NotEqual(field, compared)
      case CompareOperation.Greater      => Greater(field, compared)
      case CompareOperation.GreaterEqual => GreaterEqual(field, compared)
      case CompareOperation.Less         => Less(field, compared)
      case CompareOperation.LessEqual    => LessEqual(field, compared)
    }
  }
}
