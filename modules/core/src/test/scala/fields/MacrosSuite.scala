package jap.fields

import DefaultAccumulateVM._

class MacrosSuite extends munit.FunSuite {
  test("Field.from") {
    val data = TestData()
    assertEquals(Field.from(data), Field(FieldPath("data"), data))
    assertEquals(Field.from(data.nested), Field(FieldPath("data", "nested"), data.nested))
    assertEquals(
      Field.from(data.nested.deep),
      Field(FieldPath("data", "nested", "deep"), data.nested.deep),
    )
  }

  test("Field.sub") {
    val data = TestData()
    assertEquals(Field.sub(data), Field(FieldPath.Root, data))
    assertEquals(Field.sub(data.nested), Field(FieldPath("nested"), data.nested))
    assertEquals(
      Field.sub(data.nested.deep),
      Field(FieldPath("nested", "deep"), data.nested.deep),
    )
  }

  test("Field#sub") {
    val data     = TestData()
    val dataF    = Field(data)
    val nestedF  = dataF.sub(_.nested)
    val deepF    = dataF.sub(_.nested.deep)
    val deepIntF = dataF.sub(_.nested.deep.int)

    assertEquals(nestedF, Field(FieldPath("nested"), data.nested))

    assertEquals(deepF, Field(FieldPath("nested", "deep"), data.nested.deep))
    assertEquals(deepF, nestedF.sub(_.deep))

    assertEquals(deepIntF, Field(FieldPath("nested", "deep", "int"), data.nested.deep.int))
    assertEquals(deepIntF, nestedF.sub(_.deep.int))
    assertEquals(deepIntF, deepF.sub(_.int))
  }

  test("Policy.subRule") {
    implicit val policy: Policy[TestData] =
      Policy
        .builder[TestData]
        .subRule(_.string)(_.nonEmpty)
        .build

    val data    = TestData()
    val dataF   = Field(data)
    val stringF = dataF.sub(_.string)

    assertEquals(
      policy.validate(dataF).errors,
      stringF.nonEmptyError :: Nil,
    )
  }
  test("Policy.subRule2") {
    implicit val policy: Policy[TestData] =
      Policy
        .builder[TestData]
        .subRule(_.int, _.nested.deep.int)(_ !== _)
        .build

    val data  = TestData(int = 1, nested = NestedTestData(DeepTestData(1)))
    val dataF = Field.from(data)

    assertEquals(
      dataF.validate.errors,
      dataF.sub(_.int).notEqualError(dataF.sub(_.nested.deep.int)) :: Nil,
    )
  }
}
