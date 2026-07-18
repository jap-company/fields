package fields

import error.ValidationError.*

import typeclass.Validated.ListValidated

class ValidatedSuite extends munit.FunSuite {
  test("V.traverse") {
    import syntax.ValidatedSyntax.*

    val V = ListValidated

    val intP  = FieldPath.parse("data.int")
    val longP = FieldPath.parse("data.long")
    val byteP = FieldPath.parse("data.byte")

    assertEquals(
      V.traverse(List(intP, byteP, longP))(Empty(_).invalid[Accumulate]).errors,
      List(Empty(intP), Empty(byteP), Empty(longP)),
    )

    assertEquals(
      V.traverse(intP, byteP, longP)(Empty(_).invalid[Accumulate]).errors,
      List(Empty(intP), Empty(byteP), Empty(longP)),
    )
  }
  test("Ops") {
    import syntax.ValidatedSyntax.*

    val vr1 = List.empty[String]
    val vr2 = List("ERR01")
    val vr3 = List("ERR02")

    assertEquals(vr1.isValid, true)
    assertEquals(vr2.isInvalid, true)
    assertEquals(vr2.errors, List("ERR01"))
    assertEquals(vr1 && vr2, vr2)
    assertEquals(vr2.and(vr3), ListValidated.invalidAll("ERR01", "ERR02"))
    assertEquals(vr1 || vr2, ListValidated.valid)
    assertEquals(vr2.or(vr3), ListValidated.invalidAll("ERR01", "ERR02"))
    assertEquals(List(vr1, vr2, vr3).sequence, ListValidated.invalidAll("ERR01", "ERR02"))
    assertEquals(ListValidated.andAll[String](Nil), ListValidated.valid)
    assertEquals(ListValidated.andAll(List(vr2)), vr2)
    assertEquals(ListValidated.andAll(List(vr2, vr3)), List("ERR01", "ERR02"))
    assertEquals(ListValidated.orAll[String](Nil), ListValidated.valid)
    assertEquals(ListValidated.orAll(List(vr2)), vr2)
    assertEquals(ListValidated.orAll(List(vr2, vr3)), List("ERR01", "ERR02"))
  }
}
