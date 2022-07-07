package jap.fields
package fail

import jap.fields.error.{ValidationMessages => M, ValidationTypes => T}

trait FailWithBuilder[E, +P] extends FailWith[E, P] {
  def build[PP >: P](field: Field[PP], errorType: => String, errorMessage: => Option[String]): E
  def invalid[PP >: P](field: Field[PP]): E            = build[PP](field, T.Invalid, Some(M.Invalid))
  def empty[PP >: P](field: Field[PP]): E              = build[PP](field, T.Empty, Some(M.Empty))
  def nonEmpty[PP >: P](field: Field[PP]): E           = build[PP](field, T.NonEmpty, Some(M.NonEmpty))
  def minSize[PP >: P](size: Int)(field: Field[PP]): E = build[PP](field, T.MinSize, Some(M.MinSize(size)))
  def maxSize[PP >: P](size: Int)(field: Field[PP]): E = build[PP](field, T.MaxSize, Some(M.MaxSize(size)))
  def message[PP >: P](error: String, message: Option[String])(field: Field[PP]): E = build[PP](field, error, message)
  def oneOf[PP >: P](variants: Seq[PP])(field: Field[PP]): E                        =
    build[PP](field, T.OneOf, Some(M.OneOf(variants.map(_.toString))))
  def compare[PP >: P](operation: CompareOperation, compared: String)(field: Field[PP]): E =
    build[PP](field, operation.constraint, Some(operation.message(compared)))
}
