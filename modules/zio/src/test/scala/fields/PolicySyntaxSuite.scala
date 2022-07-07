package jap.fields

import zio._
import zio.test.Assertion._
import zio.test._

object PolicySyntaxSuite extends DefaultRunnableSpec {
  object Validation {
    import jap.fields.ZIOInterop._
    object all extends AccumulateVM[UIO, error.ValidationError] with ZIOSyntaxAll with fail.CanFailWithValidationError
  }
  import Validation.all._
  def spec =
    suite("PolicySyntax")(
      testM("validateIO") {
        implicit val policy: Policy[Int] = _ === 0
        val field                        = Field(12)
        assertM(field.validateIO.either)(equalTo(Left(List(field.equalError(0)))))
      }
    )
}
