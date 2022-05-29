package jap.fields

import ValidationError._
import ValidationResult._
import CatsInterop._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import cats._

object EvalValidationModule extends FailFastValidationModule[Eval, FieldError[ValidationError]]
import EvalValidationModule._

class EvalSuite extends munit.FunSuite {
  val inited                            = new ListBuffer[FieldPath]
  def init[A](path: FieldPath)(a: A): A = {
    inited.append(path)
    a
  }

  override def beforeEach(context: BeforeEach): Unit = inited.clear

  test("FailFast.one") {
    val field = Field(FieldPath.root, 12)
    val vr    = field.assert(init(field)(_) > 10, _.minSize(10))
    assertEquals(inited.toList.length, 0)
    assertEquals(vr.value, FailFast.valid)
    assertEquals(inited.toList.length, 1)
  }

  test("FailFast.sequence") {
    val vr =
      (0 to 100)
        .map { i =>
          val field = Field(i.toString, 10)
          field.assert(init(field)(_) > i, _.minSize(10))
        }
        .combineAll
        .memoize

    val expectedToInit = (0 to 10).map(v => FieldPath(v.toString)).toList
    assertEquals(inited.toList.length, 0)
    assertEquals(vr.value.errors, FieldError[ValidationError]("10", MinSize(10)) :: Nil)
    assertEquals(inited.toList, expectedToInit)
    assertEquals(vr.value.isInvalid, true)
    assertEquals(inited.toList, expectedToInit)
  }
}
