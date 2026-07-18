package fields
package value

import error.ValidationError

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.*
import scala.concurrent.Future

class RuleSuite extends munit.FunSuite {
  test("Should be same after wrap/unwrap") {
    import defaultDsl.*

    val expectedRule = Rule.pure(V.invalid(ValidationError.Invalid(FieldPath.Root)))
    val actualRule   = (0 to 1000).foldLeft(expectedRule)((r, _) => Rule.wrap(r.unwrap))
    assertEquals(actualRule, expectedRule)
  }

  test("Rule.whenType") {
    import defaultDsl.*
    sealed trait TF
    case object IzumiBio extends TF
    case object Tofu     extends TF
    case object Cats     extends TF

    List(
      Field[TF](Tofu),
      Field[TF](Cats),
      Field[TF](IzumiBio),
    ).foreach { field =>
      val rule = {
        field.whenType[Tofu.type](_.check(_.failMessage(Tofu.toString).invalid)) &&
        field.whenType[Cats.type](_.check(_.failMessage(Cats.toString).invalid)) &&
        field.whenType[IzumiBio.type](_.check(_.failMessage(IzumiBio.toString).invalid))
      }

      assertEquals(
        rule.errors,
        List(field.failMessage(field.value.toString)),
      )
    }
  }

  test("is Distinct By") {
    import defaultDsl.*
    case class Good(id: UUID)
    case class Cart(goods: List[Good])
    object Cart {
      implicit val policy: Policy[Cart] =
        Policy[Cart]
          .subRule(_.goods)(
            _.minSize(1),
            _.isDistinctBy(_.id, _.failInvalid),
          )
    }

    val dupID = UUID.randomUUID

    val cart  = Cart(List(Good(dupID), Good(UUID.randomUUID), Good(dupID)))
    val cartF = Field.from(cart)

    assertEquals(
      cartF.validate.errors,
      List(
        cartF.sub(_.goods(0)).failInvalid,
        cartF.sub(_.goods(2)).failInvalid,
      ),
    )
  }

  test("Rule.flatten") {
    import typeclass.Effect.future.*
    object Validation extends FieldsDsl.BaseAccumulate[Future, ValidationError]
    import Validation.*

    val field = Field(2)
    def rule  = Rule.pure(field.failMessage("flatten").invalid)

    for {
      actual   <- Rule.flatten(Future.successful(rule)).effect
      expected <- rule.effect
    } yield assertEquals(actual, expected)
  }
}
