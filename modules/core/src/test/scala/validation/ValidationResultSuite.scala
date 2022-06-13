package jap.fields

import DefaultAccumulateVM._
import ValidationError._
import FieldPathConversions._
class ValidationResultSuite extends munit.FunSuite {
  test("VR.traverse") {
    val data  = TestData()
    val intF  = Field.sub(data.int)
    val longF = Field.sub(data.long)
    val byteF = Field.sub(data.byte)

    assertEquals(
      VR.traverse(List(intF, byteF, longF))(_.failEmpty).errors,
      List(Empty(intF), Empty(byteF), Empty(longF)),
    )

    assertEquals(
      VR.traverse(intF, byteF, longF)(_.failEmpty).errors,
      List(Empty(intF), Empty(byteF), Empty(longF)),
    )
  }
}
