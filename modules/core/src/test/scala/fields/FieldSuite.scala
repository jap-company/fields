package jap.fields

import FieldPathConversions._

class FieldSuite extends munit.FunSuite {
  test("Field._1") {
    assertEquals(Field(("1", 2)).first, Field("1"))
  }
  test("Field._2") {
    assertEquals(Field(("1", 2)).second, Field(2))
  }
  test("Field.provideSub") {
    val field = Field("a", "a")
    assertEquals(field.provideSub("b", "b"), Field("a.b", "b"))
  }
  test("Field.selectSub") {
    val field = Field("a", "a")
    assertEquals(field.selectSub("A", _.toUpperCase), Field("a.A", "A"))
  }
  test("Field.option") {
    assertEquals(Field(Some(2)).option, Some(Field(2)))
    assertEquals(Field[Option[Int]](None).option, None)
  }
}
