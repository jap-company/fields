package fields

import FieldPathConversions.*
import FieldsInteropZIO.*
import error.ValidationError.MinSize
import value.FieldsDsl

import zio.*
import zio.test.*
import zio.test.Assertion.*

import scala.collection.mutable.ListBuffer

object dsl extends FieldsDsl.BaseFailFast[Task, error.ValidationError]
import dsl.{FieldPath => _, _}

object ZioTaskSuite extends ZIOSpecDefault {

  def init[A](path: FieldPath)(a: A)(implicit inited: ListBuffer[FieldPath]): A = {
    inited.append(path)
    a
  }

  def spec: Spec[Any, Any] =
    suite("ZIO.FailFast")(
      test("FailFast.one") {
        implicit val inited: ListBuffer[FieldPath] = new ListBuffer[FieldPath]

        val field = Field(FieldPath.Root, 12)
        val rule  = field.assert(init(field)(_) > 10, _.failMinSize(10))

        val beforeRun = inited.toList.length

        rule.effect.map { result =>
          assert(beforeRun)(equalTo(0)) &&
          assert(result)(equalTo(V.valid)) &&
          assert(inited.toList)(hasSize(equalTo(1)))
        }
      },
      test("FailFast.sequence") {
        implicit val inited: ListBuffer[FieldPath] = new ListBuffer[FieldPath]

        val resultMemo =
          (0 to 100)
            .map { i =>
              val field = Field(i.toString, 10)
              field.assert(init(field)(_) > i, _.failMinSize(10))
            }
            .combineAll
            .effect
            .memoize

        val expectedToInit = (0 to 10).map(v => FieldPath.fromPath(v.toString)).toList

        val beforeRun = inited.toList.length

        for {
          result        <- resultMemo
          result1       <- result
          afterFirstRun <- ZIO.succeed(inited.toList)
          result2       <- result
        } yield (
          assert(beforeRun)(equalTo(0)) &&
            assert(result1.errors)(equalTo(MinSize("10", 10) :: Nil)) &&
            assert(afterFirstRun)(equalTo(expectedToInit)) &&
            assertTrue(result2.isInvalid) &&
            assert(inited.toList)(equalTo(expectedToInit))
        )
      },
      test("Rule.map evaluates its source effect once") {
        implicit val inited: ListBuffer[FieldPath] = new ListBuffer[FieldPath]
        val path                                   = FieldPath.fromPath("source")
        val source: Rule                           = Rule.effect(ZIO.succeed {
          init(path)(())
          V.valid
        })

        source
          .map(identity)
          .effect
          .map(result => assertTrue(result.isEmpty) && assert(inited.toList)(equalTo(path :: Nil)))
      },
    )
}
