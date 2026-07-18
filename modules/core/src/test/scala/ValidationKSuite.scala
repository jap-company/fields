package fields

import PolicyKSyntax.*
import typeclass.Effect.Sync
import value.Field

class PolicyKSuite extends munit.FunSuite {
  test("PolicyK") {
    implicit val Rule: RuleOps[Sync, Accumulate, String] = RuleOps.instance[Sync, Accumulate, String]

    case class User(name: String, age: Int, password: String, passwordRepeat: String)

    val validation: PolicyK[Field[User], Sync, Accumulate, String] =
      PolicyK[Field[User], Sync, Accumulate, String]
        .mappedRule(_.downS("age", _.age))(
          ageF => Rule.assert(s"${ageF.path}<18")(ageF.value > 18),
          ageF => Rule.assert(s"${ageF.path}>200")(ageF.value < 200),
        )
        .mappedRule(_.downS("name", _.name))(
          nameF => Rule.assert(s"${nameF.path}<5")(nameF.value.length > 5),
          nameF => Rule.assert(s"${nameF.path}>10")(nameF.value.length < 10),
        )
        .mappedRule(
          _.downS("password", _.password),
          _.downS("passwordRepeat", _.passwordRepeat),
        )((passwordF, passwordRepeatF) =>
          Rule.assert(s"${passwordF.path}!=${passwordRepeatF.path}")(passwordF.value == passwordRepeatF.value)
        )

    assertEquals(
      validation(Field(User("a", 0, "", ""))).effect,
      List(".age<18", ".name<5"),
    )
    assertEquals(
      validation(Field(User("0123456789", 201, "2", "1"))).effect,
      List(".age>200", ".name>10", ".password!=.passwordRepeat"),
    )
  }
}
