package fields

import zio._
import zio.test.Assertion._
import zio.test._
import value.FieldsDsl
import FieldsInteropZIO.*

object PolicySyntaxSuite extends ZIOSpecDefault {
  object dsl extends FieldsDsl.BaseAccumulate[UIO, error.ValidationError]
  import dsl._

  def spec: Spec[Any, Any] =
    suite("PolicySyntax")(
      test("validateIO") {
        implicit val policy: Policy[Int] = _ === 0
        val field                        = Field(12)
        assertZIO(field.validateIO.either)(equalTo(Left(List(field.failEqual(0)))))
      }
    )
}
