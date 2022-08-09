package jap.fields

import FieldPart._

class FieldPathSuite extends munit.FunSuite {
  test("FieldPath.name") {
    assertEquals(FieldPath.Root.name, ".")
    assertEquals(FieldPath.parse("1.2.3").name, "3")
  }

  test("FieldPath.parse") {
    assertEquals(FieldPath.parse("a.b.c"), FieldPath(List(Path("a"), Path("b"), Path("c"))))
    assertEquals(
      FieldPath.parse("a[1][22].c[333]"),
      FieldPath(List(Path("a"), Index(1), Index(22), Path("c"), Index(333))),
    )
  }

  List(
    FieldPath.Root           -> ".",
    FieldPath.parse("1.2.3") -> ".1.2.3",
  ).foreach { case (field, expected) =>
    test(s"FieldPath.full/toString - $expected") {
      assertEquals(field.full, expected)
      assertEquals(field.toString, expected)
    }
  }

  List(
    (FieldPath.parse("user.username"), "name", FieldPath.parse("user.name")),
    (FieldPath.Root, "name", FieldPath.fromPath("name")),
  ).foreach { case (path, name, expected) =>
    test(s"FieldPath.named: ${path.full} named ${name}") {
      assertEquals(path.named("name"), expected)
    }
  }
}
