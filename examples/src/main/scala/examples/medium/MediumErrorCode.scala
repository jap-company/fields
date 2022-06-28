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
