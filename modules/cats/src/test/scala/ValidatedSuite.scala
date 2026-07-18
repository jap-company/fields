package fields

import cats.data.*
import value.FieldsDsl
import CatsInterop.*
import error.ValidationError

import typeclass.Effect

class ValidatedSuite extends munit.FunSuite {
  test("ValidatedNec") {
    val dsl = new FieldsDsl[Effect.Sync, ValidatedAccumulateNec, ValidationError] {}
    import dsl._

    val field = Field(FieldPath.Root, 12)
    val vr    = field > 13 && field > 14 && (field > 15 || field === 12)
    assertEquals(
      vr.effect,
      Validated.invalid(
        NonEmptyChain(
          field.failGreater(13),
          field.failGreater(14),
        )
      ),
    )
  }

  test("ValidatedNel") {
    val dsl = new FieldsDsl[Effect.Sync, ValidatedAccumulateNel, ValidationError] {}
    import dsl._

    val field = Field(FieldPath.Root, 12)
    val vr    = field > 13 && field > 14 && (field > 15 || field === 12)
    assertEquals(
      vr.effect,
      Validated.invalid(
        NonEmptyList(
          field.failGreater(13),
          field.failGreater(14) :: Nil,
        )
      ),
    )
  }
}
