package jap.fields

import cats._
import jap.fields.error.ValidationError
import jap.fields.error.ValidationError._
import jap.fields.fail._

import FieldPathConversions._

object ValidationModules {
  import CatsInterop._
  object EvalFailFastVM   extends FailFastVM[Eval, ValidationError] with CanFailWithValidationError
  object EvalAccumulateVM extends AccumulateVM[Eval, ValidationError] with CanFailWithValidationError
}
import ValidationModules._

class EvalSuite extends munit.FunSuite {
  val touch = new TouchCheckUtil[FieldPath]

  override def beforeEach(context: BeforeEach): Unit = touch.clear()

  test("FailFast.one") {
    import EvalFailFastVM._
    val field = Field(FieldPath.Root, 12)
    val vr    = field.ensure(touch(field)(_) > 10, _.failMinSize(10))
    assert(!touch.isTouched(field))
    assertEquals(vr.unwrap.value, V.valid)
    assert(touch.isTouched(field))
  }

  test("FailFast.sequence") {
    import EvalFailFastVM._
    val rule =
      Rule {
        (0 to 100)
          .map { i =>
            val field = Field(i.toString, 10)
            field.ensure(touch(field)(_) > i, _.failMinSize(10))
          }
          .combineAll
          .unwrap
          .memoize
      }

    val expectedToInit = (0 to 10).map(v => FieldPath(v.toString)).toList
    assertEquals(touch.touched.length, 0)
    assertEquals(rule.errors.value, MinSize("10", 10) :: Nil)
    assertEquals(touch.touched, expectedToInit)
    assertEquals(rule.isInvalid.value, true)
    assertEquals(touch.touched, expectedToInit)
  }

  test("Rule.syntax") {
    import EvalAccumulateVM._
    val field         = Field("")
    val failRule      = (msg: String) => Rule.pure(field.failMessage(msg))
    val result: MRule =
      List[MRule](
        Rule.valid,
        Rule.effect(Eval.later(field.failMessage("effect"))),
        Rule.defer(failRule("defer")),
        Rule.pure(field.failMessage("pure")),
        Rule.invalid(field.messageError("invalid")),
        Rule.when(true)(failRule("when")),
        Rule.whenF(Eval.True)(failRule("whenF")),
        Rule.ensure(field.failMessage("ensure"))(false),
        Rule.ensureF(field.failMessage("ensureF"))(Eval.False),
        Rule.modify(MRule.valid)(_ => field.failMessage("modify")),
        Rule.modifyM(MRule.valid)(_ => failRule("modifyM")),
      ).combineAll

    val expectedErrors =
      List("effect", "defer", "pure", "invalid", "when", "whenF", "ensure", "ensureF", "modify", "modifyM")
        .map(field.messageError(_))

    assertEquals(result.errors.value, expectedErrors)
  }
}
