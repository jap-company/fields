package jap.fields

import DefaultAccumulateValidationModule._
import ValidationError._
import ValidationResult._

class DslSuite extends munit.FunSuite {
  test("List.each") {
    case class UserList(users: List[String])
    val userList  = UserList(List("ann", ""))
    val userListF = Field.from(userList)
    val usersF    = userListF.sub(_.users)
    val emptyF    = userListF.sub(_.users(1))

    assertEquals(emptyF, usersF.sub(_.apply(1)))
    assertEquals(
      usersF.each(_.nonEmpty).errors,
      emptyF.error[ValidationError](Empty) :: Nil,
    )
  }
  test("Map.each") {
    case class BalanceCache(userBalances: Map[String, Int])
    val ageCache      = BalanceCache(Map("ann" -> 0, "oleg" -> 0, "mars" -> Int.MaxValue))
    val ageCacheF     = Field.from(ageCache)
    val userBalancesF = ageCacheF.sub(_.userBalances)

    assertEquals(
      userBalancesF.each(_.second !== 0).errors,
      List[FieldError[ValidationError]](
        FieldError(List("ageCache", "userBalances", "0"), NotEqual("0")),
        FieldError(List("ageCache", "userBalances", "1"), NotEqual("0")),
      ),
    )
  }
  test("Some.some") {
    val someF = Field(Some(2))
    assertEquals(
      someF.some(_ > 10).errors,
      FieldError[ValidationError](someF, Greater("10")) :: Nil,
    )
  }
  test("None.some") {
    val noneF: Field[Option[Int]] = Field(None)
    assertEquals(noneF.some(_ > 10).errors, Nil)
  }
  test("String.isEnum") {
    assert(Field("Red").isEnum(RGB).isValid)
    assert(Field("Purple").isEnum(RGB).isInvalid)
  }
}

object RGB extends scala.Enumeration {
  val Red   = Value
  val Green = Value
  val Blue  = Value
}
