package jap.fields

import DefaultAccumulateVM._

class RuleSuite extends munit.FunSuite {
  test("Should be same after wrap/unwrap") {
    val expectedRule = Rule.pure(V.invalid(error.ValidationError.Invalid(FieldPath.Root)))
    val actualRule   = (0 to 1000).foldLeft(expectedRule)((r, _) => Rule.wrap(r.unwrap))
    assertEquals(actualRule, expectedRule)
  }
}
