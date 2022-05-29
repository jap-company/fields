package jap.fields

import DefaultAccumulateValidationModule._
import ValidationError._
import ValidationResult._

case class NumericPair(numeric: Int, string: String)

case class NumericForm(pair: NumericPair)

class Dsl3Suite extends munit.FunSuite {
  test("conditional subRule") {
    val form    = NumericForm(NumericPair(0, "zero"))
    val formF   = Field.from(form)
    val stringF = formF.sub(_.pair.string)

    given Policy[NumericForm] =
      Policy
        .builder[NumericForm]
        .subRule(_.pair.string) {
          validated.value.pair.numeric match {
            case n if n < 0 => _.startsWith("-")
            case 0          => _ === "0"
            case n if n > 0 => _.nonEmpty
          }
        }
        .build

    val vr = implicitly[Policy[NumericForm]].validate(formF)

    assertEquals(
      vr.errors,
      List(FieldError(stringF, Equal("0"): ValidationError)),
    )
  }

}
