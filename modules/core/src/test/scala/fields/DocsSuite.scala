package jap.fields

/** Suite that contains examples from docs */
class DocsSuite extends munit.FunSuite {
  test("Code teaser") {
    import DefaultAccumulateVM._
    import ValidationError._
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
}
