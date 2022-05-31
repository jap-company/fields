package jap.fields

import DefaultAccumulateVM._
import ValidationError._

class ValidationResultSuite extends munit.FunSuite {
  test("FieldPath.name") {
    assertEquals(FieldPath.root.name, "root")
    assertEquals(FieldPath("1", "2", "3").name, "3")
  }
}
