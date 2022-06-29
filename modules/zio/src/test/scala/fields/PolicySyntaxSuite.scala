package jap.fields

import jap.fields.data.Accumulate
import jap.fields.fail._
import zio._
import zio.test.Assertion._
import zio.test._

import error._
import ZioInterop._

object UIOValidation
    extends ValidationModule[UIO, Accumulate, ValidationError]
    with CanFailWithValidationError
    with ZioPolicySyntax
import UIOValidation._

object PolicySyntaxSuite extends DefaultRunnableSpec {
  def spec =
    suite("PolicySyntax")(
      testM("validateIO") {
        implicit val policy: Policy[Int] = _ === 0

        val field = Field(12)

        field.validateIO.either
          .map { either =>
            assert(either)(equalTo(Left(List(field.equalError(0)))))
          }
      }
    )
}
