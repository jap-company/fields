package fields
package value

import error.ValidationError.*
import value.defaultDsl.{Field => _, _}

class IterableSyntaxSuite extends munit.FunSuite {
  test("Map iterable") {
    val field = Field(Map("a" -> "b"))
    assertEquals(field.minSize(2).errors, List(MinSize(FieldPath.Root, 2)))
  }
}
