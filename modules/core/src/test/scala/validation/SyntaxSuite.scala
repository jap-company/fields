package jap.fields

import FieldPathConversions._
import DefaultAccumulateVM._
import ValidationError._
import scala.util.Properties

class SyntaxSuite extends munit.FunSuite {
  test("compare Field[P] with P") {
    val field = Field(4)
    assertEquals((field > 8).errors.head, field.greaterError(8))
    assertEquals((field >= 8).errors.head, field.greaterEqualError(8))
    assertEquals((field < 2).errors.head, field.lessError(2))
    assertEquals((field <= 2).errors.head, field.lessEqualError(2))
    assertEquals((field === 2).errors.head, field.equalError(2))
    assertEquals((field !== 4).errors.head, field.notEqualError(4))
  }

  test("compare Field[P] with Field[P]") {
    val field = Field(4)
    val f8    = Field(8)
    val f2    = Field(2)
    assertEquals((field > Field(8)).errors.head, field.greaterError(f8))
    assertEquals((field >= Field(8)).errors.head, field.greaterEqualError(f8))
    assertEquals((field < Field(2)).errors.head, field.lessError(f2))
    assertEquals((field <= Field(2)).errors.head, field.lessEqualError(f2))
    assertEquals((field === Field(2)).errors.head, field.equalError(f2))
    assertEquals((field !== field).errors.head, field.notEqualError(field))
  }

  test("Scala 2: No FieldCompare") {
    assume(Properties.versionNumberString.startsWith("3"))
    assertNoDiff(
      compileErrors("Field(2) === true"),
      """|error: Cannot compare Int with Boolean
         |Field(2) === true
         |         ^
         |""".stripMargin,
    )
  }

  test("Scala 3: No FieldCompare") {
    assume(Properties.versionNumberString.startsWith("3"))
    assertNoDiff(
      compileErrors("Field(2) === true"),
      """|error:
         |Cannot compare Int with Boolean.
         |I found:
         |
         |    jap.fields.FieldCompare.defaultFieldCompare[P]
         |
         |But method defaultFieldCompare in object FieldCompare does not match type jap.fields.FieldCompare[Int, Boolean].
         |Field(2) === true
         |         ^       
         |""".stripMargin,
    )
  }

  test("Field.when") {
    val field = Field(2)
    assertEquals(field.when(true)(_ === 3).errors, field.equalError(3) :: Nil)
    assertEquals(field.when(false)(_ === 3).errors, Nil)
  }
  test("Field.unless") {
    val field = Field(2)
    assertEquals(field.unless(false)(_ === 3).errors, field.equalError(3) :: Nil)
    assertEquals(field.unless(true)(_ === 3).errors, Nil)
  }
  test("List.each") {
    case class UserList(users: List[String])
    val userList  = UserList(List("ann", ""))
    val userListF = Field.from(userList)
    val usersF    = userListF.sub(_.users)
    val emptyF    = userListF.sub(_.users(1))

    assertEquals(emptyF, usersF.sub(_.apply(1)))
    assertEquals(
      usersF.each(_.nonEmpty).errors,
      emptyF.emptyError :: Nil,
    )
  }
  test("Map.each") {
    case class BalanceCache(userBalances: Map[String, Int])
    val ageCache      = BalanceCache(Map("ann" -> 0, "oleg" -> 0, "mars" -> Int.MaxValue))
    val ageCacheF     = Field.from(ageCache)
    val userBalancesF = ageCacheF.sub(_.userBalances)

    assertEquals(
      userBalancesF.each(_.second !== 0).errors,
      List[ValidationError](
        NotEqual(FieldPath.raw("ageCache.userBalances.0"), "0"),
        NotEqual(FieldPath.raw("ageCache.userBalances.1"), "0"),
      ),
    )
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
          _.minSize(4),
          _.maxSize(8),
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
          _.minSize(4),
          _.maxSize(8),
          _.each(_.first.nonBlank),
          _.eachKey(_.nonBlank),
          _.eachValue(_.blank),
        )
        .subRule(_.listInt)(_.each(_ > 4), _.minSize(4), _.maxSize(8))
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
        .subRule(_.name)(name => name.minSize(4) && name.maxSize(48)) // runs all validations combining using and
        .subRule(_.email)(email => email.validate)                    // same but field creating is manual
        .subRule(_.age, _.hasParrot)((age, hasParrot) => age > 48 || (age > 22 && hasParrot.isTrue)) // 2 fields rule
        .build

    val request: Request = Request("", Email(""), 23, true)
    val requestF         = Field.from(request)
    assertEquals(
      requestF.validate.errors,
      requestF.sub(_.name).minSizeError(4) :: requestF.sub(_.email).emptyError :: Nil,
    )
  }
}

case class Email(value: String) extends AnyVal
object Email {
  implicit val policy: Policy[Email] = _.map(_.value).all(_.nonEmpty, _.maxSize(40))
}

object RGB extends scala.Enumeration {
  val Red   = Value
  val Green = Value
  val Blue  = Value
}
