package jap.fields
package examples
package medium

import jap.fields.ZioInterop._
import jap.fields._
import jap.fields.fail._
import zio.Task

object MediumCustomValidation
    extends ValidationModule[Task, MediumErrors, MediumErrorCode]
    with CanFailMediumErrorCode {
  def failCode[P](code: Int)(field: Field[P]) =
    V.invalid(MediumErrorCode(field.path, code))

  implicit object FailPostDateWithErrorCode extends FailWithCompare[MediumErrorCode, PostDate] {
    override def compare[P](operation: CompareOperation, compared: String)(field: Field[P]): MediumErrorCode =
      MediumErrorCode(field.path, 123)
  }
}
