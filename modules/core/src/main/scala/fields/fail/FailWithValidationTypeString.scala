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
