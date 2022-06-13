package jap.fields

import cats._

import scala.collection.mutable.ListBuffer

import FieldPathConversions._
import ValidationError._
import ValidationResult._
import CatsInterop._

object EvalValidationModule extends FailFastVM[Eval, ValidationError]
import EvalValidationModule._

class EvalSuite extends munit.FunSuite {
  val inited                            = new ListBuffer[FieldPath]
  def init[A](path: FieldPath)(a: A): A = {
    inited.append(path)
    a
  }

  override def beforeEach(context: BeforeEach): Unit = inited.clear()

  test("FailFast.one") {
    val field = Field(FieldPath.Root, 12)
    val vr    = field.ensure(init(field)(_) > 10, _.failMinSize(10))
    assertEquals(inited.toList.length, 0)
    assertEquals(vr.value, VR.valid)
    assertEquals(inited.toList.length, 1)
  }

  test("FailFast.sequence") {
    val vr =
      (0 to 100)
        .map { i =>
          val field = Field(i.toString, 10)
          field.ensure(init(field)(_) > i, _.failMinSize(10))
        }
        .combineAll
        .memoize

    val expectedToInit = (0 to 10).map(v => FieldPath(v.toString)).toList
    assertEquals(inited.toList.length, 0)
    assertEquals(vr.value.errors, MinSize("10", 10) :: Nil)
    assertEquals(inited.toList, expectedToInit)
    assertEquals(vr.value.isInvalid, true)
    assertEquals(inited.toList, expectedToInit)
  }
}
