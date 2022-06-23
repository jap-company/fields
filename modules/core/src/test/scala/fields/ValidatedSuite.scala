package jap.fields

import error.ValidationError._
import FieldPathConversions._

class ValidatedSuite extends munit.FunSuite {
  test("V.traverse") {
    import DefaultAccumulateVM._

    val data  = TestData()
    val intF  = Field.sub(data.int)
    val longF = Field.sub(data.long)
    val byteF = Field.sub(data.byte)

    assertEquals(
      V.traverse(List(intF, byteF, longF))(_.failEmpty).errors,
      List(Empty(intF), Empty(byteF), Empty(longF)),
    )

    assertEquals(
      V.traverse(intF, byteF, longF)(_.failEmpty).errors,
      List(Empty(intF), Empty(byteF), Empty(longF)),
    )
  }
  test("Ops") {
    import jap.fields.syntax.ValidatedSyntax._
    import jap.fields.data._

    val vr1 = Accumulate.valid
    val vr2 = Accumulate.invalid("ERR01")
    val vr3 = Accumulate.invalid("ERR02")

    vr1.isValid
    vr2.isInvalid
    vr2.errors
    vr1 && vr2
    vr2.and(vr3)
    vr1 || vr2
    vr2.or(vr3)
    List(vr1, vr2, vr3).sequence
  }
}
