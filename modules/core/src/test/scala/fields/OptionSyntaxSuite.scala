package jap.fields

import FieldPathConversions._
import DefaultAccumulateVM._
import ValidationError._
import scala.util.Properties

class OptionSyntaxSuite extends munit.FunSuite {
  test("Option.some") {
    assertEquals(Field(Some(2)).some(_ > 10).errors, List(Greater(FieldPath.Root, "10")))
    assertEquals(Field[Option[Int]](None).some(_ > 10).errors, Nil)
  }
  test("Option.isSome/isNone") {
    val someF = Field(Some(2))
    val noneF = Field[Option[Int]](None)
    assertEquals(someF.isSome.errors, Nil)
    assertEquals(noneF.isSome.errors, List(Empty(someF)))
    assertEquals(someF.isNone.errors, List(NonEmpty(someF)))
    assertEquals(noneF.isNone.errors, Nil)
  }
  test("Option.someOrValid") {
    val fo1        = Field(Option(3))
    val fo2        = Field(Option(4))
    val rule: Rule =
      someOrValid(
        for {
          f1 <- fo1.option
          f2 <- fo2.option
        } yield (f1 > f2)
      )
    assertEquals(rule.errors, List(Greater(fo1, fo2.fullPath)))
  }
}
