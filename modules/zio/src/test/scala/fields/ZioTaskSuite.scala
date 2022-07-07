package jap.fields

import jap.fields.error.ValidationError.MinSize
import zio._
import zio.test.Assertion._
import zio.test._

import scala.collection.mutable.ListBuffer

import FieldPathConversions._

object ZioTaskSuite extends DefaultRunnableSpec {
  object Validation {
    import jap.fields.ZIOInterop._
    object all extends FailFastVM[Task, error.ValidationError] with fail.CanFailWithValidationError
  }
  import Validation.all._

  def init[A](path: FieldPath)(a: A)(implicit inited: ListBuffer[FieldPath]): A = {
    inited.append(path)
    a
  }

  def spec =
    suite("ZIO.FailFast")(
      testM("FailFast.one") {
        implicit val inited: ListBuffer[FieldPath] = new ListBuffer[FieldPath]

        val field = Field(FieldPath.Root, 12)
        val rule  = field.ensure(init(field)(_) > 10, _.failMinSize(10))

        val beforeRun = inited.toList.length

        rule.effect.map { result =>
          assert(beforeRun)(equalTo(0)) &&
          assert(result)(equalTo(V.valid)) &&
          assert(inited.toList)(hasSize(equalTo(1)))
        }
      },
      testM("FailFast.sequence") {
        implicit val inited: ListBuffer[FieldPath] = new ListBuffer[FieldPath]

        val resultMemo =
          (0 to 100)
            .map { i =>
              val field = Field(i.toString, 10)
              field.ensure(init(field)(_) > i, _.failMinSize(10))
            }
            .combineAll
            .effect
            .memoize

        val expectedToInit = (0 to 10).map(v => FieldPath(v.toString)).toList

        val beforeRun = inited.toList.length

        for {
          result        <- resultMemo
          result1       <- result
          afterFirstRun <- UIO(inited.toList)
          result2       <- result
        } yield (
          assert(beforeRun)(equalTo(0)) &&
            assert(result1.errors)(equalTo(MinSize("10", 10) :: Nil)) &&
            assert(afterFirstRun)(equalTo(expectedToInit)) &&
            assertTrue(result2.isInvalid) &&
            assert(inited.toList)(equalTo(expectedToInit))
        )
      },
    )
}
