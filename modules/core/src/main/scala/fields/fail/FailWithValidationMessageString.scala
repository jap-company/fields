package jap.fields
package fail

import error.ValidationMessages

trait CanFailWithValidationMessageString {
  implicit def failWith: FailWith.Base[String] = FailWithValidationMessageString
}

/** String [[FailWith]] instance where errors are human-readable error messages */
object FailWithValidationMessageString extends FailWith.Base[String] {
  def invalid[P](field: Field[P]): String                                         = ValidationMessages.Invalid
  def empty[P](field: Field[P]): String                                           = ValidationMessages.Empty
  def nonEmpty[P](field: Field[P]): String                                        = ValidationMessages.NonEmpty
  def minSize[P](size: Int)(field: Field[P]): String                              = ValidationMessages.MinSize(size)
  def maxSize[P](size: Int)(field: Field[P]): String                              = ValidationMessages.MaxSize(size)
  def oneOf[P](variants: Seq[P])(field: Field[P]): String                         =
    ValidationMessages.OneOf(variants.map(_.toString))
  def message[P](error: String, message: Option[String])(field: Field[P]): String = message.getOrElse(error)
  def compare[P](operation: CompareOperation, compared: String)(field: Field[P]): String =
    operation match {
      case CompareOperation.Equal        => ValidationMessages.Equal(compared)
      case CompareOperation.NotEqual     => ValidationMessages.NotEqual(compared)
      case CompareOperation.Greater      => ValidationMessages.Greater(compared)
      case CompareOperation.GreaterEqual => ValidationMessages.GreaterEqual(compared)
      case CompareOperation.Less         => ValidationMessages.Less(compared)
      case CompareOperation.LessEqual    => ValidationMessages.LessEqual(compared)
    }
}
