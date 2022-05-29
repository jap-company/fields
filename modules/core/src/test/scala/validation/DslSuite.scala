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

  test("Policy.TestDataPolicy") {
    implicit val testDataPolicy: Policy[TestData] =
      Policy
        .builder[TestData]
        .subRule(_.int)(_ > 4, _ >= 4, _ < 8, _ <= 8)
        .subRule(_.byte)(_ > 8.toByte)
        .subRule(_.long)(_ > 4L, _ in List(8L, 4L))
        .subRule(_.double)(f => f > 4d && (f < 8d || f >= 4d))
        .subRule(_.float)(_ > 4f)
        .subRule(_.bigDecimal)(_ > BigDecimal(4))
        .subRule(_.bigInt)(_ > BigInt(4))
        .subRule(_.boolean)(_.isTrue, _.isFalse)
        .subRule(_.string)(
          _.min(4),
          _.max(8),
          _.nonBlank,
          _.nonEmpty,
          _.blank,
          _.startsWith("sca"),
          _.endsWith("la"),
          _.matches("scala"),
          _.matches("scala".r),
          _.isEnum(RGB),
          _ in List("scala"),
        )
        .fieldRule(_.sub(_.stringValueClass).map(_.value))(_.nonEmpty)
        .subRule(_.mapStringString)(
          _.min(4),
          _.max(8),
          _.each(_.first.nonBlank),
          _.eachKey(_.nonBlank),
          _.eachValue(_.blank),
        )
        .subRule(_.listInt)(_.each(_ > 4), _.min(4), _.max(8))
        .subRule(_.optionInt)(_.isDefined, _.isEmpty, _.some(_ > 4))
        .subRule(_.nested.deep.int)(_ > 0)
        .build

    val data = TestData()
    val vr   = Field.from(data).validate
    assert(vr.isInvalid)
  }

  test("Policy.README") {
    case class Request(name: String, email: Email, age: Int, hasParrot: Boolean)
    implicit val policy: Policy[Request] =
      Policy
        .builder[Request]
        .subRule(_.name)(_.all(_.min(4), _.max(48))) // runs all validations combining using and
        .subRule(_.email)(_.validate)                // same but field creating is manual
        .subRule2(_.age, _.hasParrot)((age, hasParrot) => age > 48 || (age > 22 && hasParrot.isTrue)) // 2 fields rule
        .build

    val request: Request = Request("", Email(""), 23, true)
    val requestF         = Field.from(request)
    assertEquals(
      requestF.validate.errors,
      requestF
        .sub(_.name)
        .error[ValidationError](MinSize(4)) :: requestF.sub(_.email).error[ValidationError](Empty) :: Nil,
    )
  }
}

case class Email(value: String) extends AnyVal
object Email {
  implicit val policy: Policy[Email] = _.map(_.value).all(_.nonEmpty, _.max(40))
}

object RGB extends scala.Enumeration {
  val Red   = Value
  val Green = Value
  val Blue  = Value
}
