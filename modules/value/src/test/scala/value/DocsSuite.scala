package fields
package value

import typeclass.Effect

/** Suite that contains examples from docs */
class DocsSuite extends munit.FunSuite {

  test("Code teaser") {
    import error.ValidationError.*

    import defaultDsl.*

    case class User(username: String, password: String)
    case class Request(user: User)
    object Request {

      implicit val policy: Policy[Request] =
        Policy[Request]
          .subRule(_.user.username)(_.nonBlank, _.minSize(4))
          .subRule(_.user.password)(_.nonBlank, _.minSize(8), _.maxSize(30))
    }

    val request  = Request(User("ke", "k"))
    val requestF = Field(request)

    assertEquals(
      requestF.validate.errors,
      List(MinSize(FieldPath.parse("user.username"), 4), MinSize(FieldPath.parse("user.password"), 8)),
    )
  }

  test("FailWith.override") {
    import fields.error.*
    import fields.fail.*
    object Validation extends FieldsDsl.BaseAccumulate[Effect.Sync, ValidationError] {
      implicit object IntFailWith
          extends FailWithInvalid[ValidationError, Int]
          with FailWithEmpty[ValidationError, Int] {
        def invalid[P >: Int](path: FieldPath, value: P): ValidationError = ValidationError.Message(path, "Invalid int")
        def empty[P >: Int](path: FieldPath, value: P): ValidationError   = ValidationError.Message(path, "Empty int")
      }
    }
    import Validation.*

    val intF    = Field(1)
    val stringF = Field("1")

    assertEquals(intF.failInvalid, ValidationError.Message(intF.path, "Invalid int"))
    assertEquals(intF.failEmpty, ValidationError.Message(intF.path, "Empty int"))
    assertEquals(stringF.failInvalid, ValidationError.Invalid(stringF.path))
    assertEquals(stringF.failEmpty, ValidationError.Empty(stringF.path))
  }
}
