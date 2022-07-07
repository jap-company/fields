package jap.fields

/** Suite that contains examples from docs */
class DocsSuite extends munit.FunSuite {
  test("Code teaser") {
    import DefaultAccumulateVM._
    import error.ValidationError._
    case class User(username: String, password: String)
    case class Request(user: User)
    object Request {
      implicit val policy: Policy[Request] =
        Policy
          .builder[Request]
          .subRule(_.user.username)(_.nonBlank, _.minSize(4))
          .subRule(_.user.password)(_.nonBlank, _.minSize(8), _.maxSize(30))
          .build
    }

    val request  = Request(User("ke", "k"))
    val requestF = Field(request)

    assertEquals(
      requestF.validate.errors,
      List(MinSize(FieldPath("user", "username"), 4), MinSize(FieldPath("user", "password"), 8)),
    )
  }

  test("FailWith.override") {
    import jap.fields._
    import jap.fields.fail._
    import jap.fields.error._
    object Validation extends DefaultAccumulateVM {
      implicit object IntFailWith
          extends FailWithInvalid[ValidationError, Int]
          with FailWithEmpty[ValidationError, Int] {
        def invalid[P >: Int](field: Field[P]): ValidationError = ValidationError.Message(field.path, "Invalid int")
        def empty[P >: Int](field: Field[P]): ValidationError   = ValidationError.Message(field.path, "Empty int")
      }
    }
    import Validation._

    val intF    = Field(1)
    val stringF = Field("1")

    assertEquals(intF.failInvalid, V.invalid(ValidationError.Message(intF.path, "Invalid int")))
    assertEquals(intF.failEmpty, V.invalid(ValidationError.Message(intF.path, "Empty int")))
    assertEquals(stringF.failInvalid, V.invalid(ValidationError.Invalid(stringF.path)))
    assertEquals(stringF.failEmpty, V.invalid(ValidationError.Empty(stringF.path)))
  }
}
