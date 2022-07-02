package jap.fields

import DefaultAccumulateVM._
import java.util.UUID

class RuleSuite extends munit.FunSuite {
  test("Should be same after wrap/unwrap") {
    val expectedRule = Rule.pure(V.invalid(error.ValidationError.Invalid(FieldPath.Root)))
    val actualRule   = (0 to 1000).foldLeft(expectedRule)((r, _) => Rule.wrap(r.unwrap))
    assertEquals(actualRule, expectedRule)
  }

  test("Rule.whenType") {
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
        field.whenType[Tofu.type](_.check(_.failMessage(Tofu.toString))) &&
        field.whenType[Cats.type](_.check(_.failMessage(Cats.toString))) &&
        field.whenType[IzumiBio.type](_.check(_.failMessage(IzumiBio.toString)))
      }

      assertEquals(
        rule.errors,
        List(field.messageError(field.value.toString)),
      )
    }
  }

  test("is Distinct By") {
    case class Good(id: UUID)
    case class Cart(goods: List[Good])
    object Cart {
      implicit val policy: Policy[Cart] =
        Policy
          .builder[Cart]
          .subRule(_.goods)(
            _.minSize(1),
            _.isDistinctBy(_.id, _.failInvalid),
          )
          .build
    }

    val dupID = UUID.randomUUID

    val cart  = Cart(List(Good(dupID), Good(UUID.randomUUID), Good(dupID)))
    val cartF = Field.from(cart)

    assertEquals(
      cartF.validate.errors,
      List(
        cartF.sub(_.goods(0)).invalidError,
        cartF.sub(_.goods(2)).invalidError,
      ),
    )
  }
}
