package fields

import cats._
import fields.error.ValidationError
import fields.error.ValidationError._
import value.FieldsDsl

import FieldPathConversions._
import CatsInterop._

object EvalFailFastDsl   extends FieldsDsl.BaseFailFast[Eval, ValidationError]
object EvalAccumulateDsl extends FieldsDsl.BaseAccumulate[Eval, ValidationError]

class EvalSuite extends munit.FunSuite {
  val touch = new TouchCheckUtil[FieldPath]

  override def beforeEach(context: BeforeEach): Unit = touch.clear()

  test("FailFast.one") {
    import EvalFailFastDsl._
    val field = Field(FieldPath.Root, 12)
    val vr    = field.assert(touch(field)(_) > 10, _.failMinSize(10))
    assert(!touch.isTouched(field))
    assertEquals(vr.unwrap.value, V.valid)
    assert(touch.isTouched(field))
  }

  test("FailFast.sequence") {
    import EvalFailFastDsl._
    val rule =
      Rule {
        (0 to 100)
          .map { i =>
            val field = Field(i.toString, 10)
            field.assert(touch(field)(_) > i, _.failMinSize(10))
          }
          .combineAll
          .unwrap
          .memoize
      }

    val expectedToInit = (0 to 10).map(v => FieldPath.fromPath(v.toString)).toList
    assertEquals(touch.touched.length, 0)
    assertEquals(rule.errors(EvalFailFastDsl.F, typeclass.HasErrors.OptionHasErrors).value, MinSize("10", 10) :: Nil)
    assertEquals(touch.touched, expectedToInit)
    assertEquals(rule.isInvalid.value, true)
    assertEquals(touch.touched, expectedToInit)
  }

  test("Rule.syntax") {
    import EvalAccumulateDsl._
    val field        = Field("")
    val failRule     = (msg: String) => Rule.pure(field.failMessage(msg).invalid[Accumulate])
    val result: Rule =
      List[Rule](
        Rule.valid,
        Rule.effect(Eval.later(field.failMessage("effect").invalid[Accumulate])),
        Rule.defer(failRule("defer")),
        Rule.pure(field.failMessage("pure").invalid[Accumulate]),
        Rule.invalid(field.failMessage("invalid")),
        Rule.when(true)(failRule("when")),
        Rule.whenF(Eval.True)(failRule("whenF")),
        Rule.assert(field.failMessage("ensure"))(false),
        Rule.assertF(field.failMessage("ensureF"))(Eval.False),
        Rule.modify(Rule.valid)(_ => field.failMessage("modify").invalid[Accumulate]),
        Rule.modifyM(Rule.valid)(_ => failRule("modifyM")),
      ).combineAll

    val expectedErrors =
      List("effect", "defer", "pure", "invalid", "when", "whenF", "ensure", "ensureF", "modify", "modifyM")
        .map(field.failMessage(_))

    assertEquals(result.errors(EvalAccumulateDsl.F, typeclass.HasErrors.ListHasErrors).value, expectedErrors)
  }

  test("Rule combinators evaluate each source at most once and preserve left-to-right order") {
    import EvalAccumulateDsl._

    val evaluated                                = scala.collection.mutable.ListBuffer.empty[String]
    val field                                    = Field(0)
    def rule(name: String, valid: Boolean): Rule = Rule.effect(Eval.later {
      evaluated += name
      if (valid) V.valid else V.invalid(field.failMessage(name))
    })

    assertEquals(
      (rule("left", valid = false) && rule("right", valid = false)).unwrap.value,
      List(field.failMessage("left"), field.failMessage("right")),
    )
    assertEquals(evaluated.toList, List("left", "right"))

    evaluated.clear()
    assertEquals((rule("left", valid = true) || rule("right", valid = false)).unwrap.value, V.valid)
    assertEquals(evaluated.toList, List("left"))

    evaluated.clear()
    val mapped = rule("source", valid = true).map(identity)
    assertEquals(mapped.unwrap.value, V.valid)
    assertEquals(evaluated.toList, List("source"))

    evaluated.clear()
    val flatMapped = rule("source", valid = true).flatMap(_ => rule("next", valid = true))
    assertEquals(flatMapped.unwrap.value, V.valid)
    assertEquals(evaluated.toList, List("source", "next"))
  }

  test("Rule collections preserve left-to-right fail-fast semantics with right association") {
    import EvalFailFastDsl._

    val evaluated                                = scala.collection.mutable.ListBuffer.empty[String]
    val field                                    = Field(0)
    def rule(name: String, valid: Boolean): Rule = Rule.effect(Eval.later {
      evaluated += name
      if (valid) V.valid else V.invalid(field.failMessage(name))
    })

    val andResult = Rule.andAll(
      List(rule("first", valid = true), rule("second", valid = false), rule("third", valid = false))
    )
    assertEquals(andResult.unwrap.value, V.invalid(field.failMessage("second")))
    assertEquals(evaluated.toList, List("first", "second"))

    evaluated.clear()
    val orResult = Rule.orAll(
      List(rule("first", valid = false), rule("second", valid = true), rule("third", valid = true))
    )
    assertEquals(orResult.unwrap.value, V.valid)
    assertEquals(evaluated.toList, List("first", "second"))

    assertEquals(Rule.andAll(Nil).unwrap.value, V.valid)
    assertEquals(Rule.orAll(Nil).unwrap.value, V.valid)

    val manyRules = List.fill(10000)(Rule.effect(Eval.now(V.valid)))
    assertEquals(Rule.andAll(manyRules).unwrap.value, V.valid)
  }

  test("Policy collections preserve lazy left-to-right fail-fast semantics") {
    import EvalFailFastDsl._

    val evaluated = scala.collection.mutable.ListBuffer.empty[String]
    val field     = Field(0)

    def policy(name: String, valid: Boolean): PolicyK[Int, Eval, FailFast, ValidationError] = _ =>
      Rule.effect(Eval.later {
        evaluated += name
        if (valid) V.valid else V.invalid(field.failMessage(name))
      })

    val andPolicy = PolicyK.combine(
      policy("first", valid = true),
      policy("second", valid = false),
      policy("third", valid = false),
    )
    assertEquals(andPolicy(0).unwrap.value, V.invalid(field.failMessage("second")))
    assertEquals(evaluated.toList, List("first", "second"))

    evaluated.clear()
    val orPolicy = PolicyK.orAll(
      policy("first", valid = false),
      policy("second", valid = true),
      policy("third", valid = true),
    )
    assertEquals(orPolicy(0).unwrap.value, V.valid)
    assertEquals(evaluated.toList, List("first", "second"))
  }
}
