package examples
package medium

import jap.fields.ZioInterop._
import jap.fields._
import jap.fields.data.Accumulate
import jap.fields.error.ValidationError
import jap.fields.fail.CanFailWithValidationError
import zio.Task

object MediumSimpleValidation
    extends ValidationModule[Task, Accumulate, ValidationError]
    with CanFailWithValidationError {
  def failCode[P](code: Int)(field: Field[P]) =
    V.invalid(ValidationError.Message(field.path, code.toString))
}
