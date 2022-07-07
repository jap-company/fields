package jap.fields

class FieldPathSuite extends munit.FunSuite {
  test("FieldPath.name") {
    assertEquals(FieldPath.Root.name, ".")
    assertEquals(FieldPath("1", "2", "3").name, "3")
  }

  test("FieldPath.fromRaw") {
    assertEquals(FieldPath.fromRaw("a.b.c"), FieldPath(List("a", "b", "c")))
  }

  List(
    FieldPath.Root           -> ".",
    FieldPath("1", "2", "3") -> "1.2.3",
  ).foreach { case (field, expected) =>
    test(s"FieldPath.full/toString - $expected") {
      assertEquals(field.full, expected)
      assertEquals(field.toString, expected.split('.').toList.toString)
    }
  }

  List(
    (FieldPath("user", "username"), "name", FieldPath("user", "name")),
    (FieldPath.Root, "name", FieldPath("name")),
  ).foreach { case (path, name, expected) =>
    test(s"FieldPath.named: ${path.full} named ${name}") {
      assertEquals(path.named("name"), expected)
    }
  }
}
