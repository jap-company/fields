package jap.fields

import cats.data._

import CatsInterop.{DefaultValidatedNecVM, DefaultValidatedNelVM}

class ValidatedSuite extends munit.FunSuite {
  test("ValidatedNec") {
    import DefaultValidatedNecVM._

    val field = Field(FieldPath.Root, 12)
    val vr    = field > 13 && field > 14 && (field > 15 || field === 12)
    assertEquals(
      vr.effect.value,
      Validated.invalid(
        NonEmptyChain(
          field.greaterError(13),
          field.greaterError(14),
        )
      ),
    )
  }

  test("ValidatedNel") {
    import DefaultValidatedNelVM._
    val field = Field(FieldPath.Root, 12)
    val vr    = field > 13 && field > 14 && (field > 15 || field === 12)
    assertEquals(
      vr.effect.value,
      Validated.invalid(
        NonEmptyList(
          field.greaterError(13),
          field.greaterError(14) :: Nil,
        )
      ),
    )
  }
}
