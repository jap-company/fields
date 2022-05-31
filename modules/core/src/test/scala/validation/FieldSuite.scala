package jap.fields

import DefaultAccumulateVM._
import ValidationError._

class FieldSuite extends munit.FunSuite {
  test("Field._1") {
    assertEquals(Field(("1", 2)).first, Field("1"))
  }
  test("Field._2") {
    assertEquals(Field(("1", 2)).second, Field(2))
  }
  test("Field.provideSub") {
    val field = Field("a", "a")
    assertEquals(field.provideSub("b", "b"), Field(FieldPath.raw("a.b"), "b"))
  }
  test("Field.selectSub") {
    val field = Field("a", "a")
    assertEquals(field.selectSub("A", _.toUpperCase), Field(FieldPath.raw("a.A"), "A"))
  }
}
