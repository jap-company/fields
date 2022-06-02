package jap.fields

import ValidationError._
import ValidationResult._
import ZIOInterop._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

import zio._
import zio.test._
import zio.test.Assertion._
import zio.test.TestAspect._
import zio.test.environment._

object TaskValidationModule extends FailFastVM[Task, FieldError[ValidationError]]
import TaskValidationModule.{assert => _, assertTrue => _, _}

object ZioTaskSuite extends DefaultRunnableSpec {
  def init[A](path: FieldPath)(a: A)(implicit inited: ListBuffer[FieldPath]): A = {
    inited.append(path)
    a
  }

  def spec =
    suite("ZIO.FailFast")(
      testM("FailFast.one") {
        implicit val inited: ListBuffer[FieldPath] = new ListBuffer[FieldPath]

        val field = Field(FieldPath.Root, 12)
        val vr    = field.assert(init(field)(_) > 10, _.minSizeError(10))

        val beforeRun = inited.toList.length

        vr.map { result =>
          assert(beforeRun)(equalTo(0)) &&
          assert(result)(equalTo(FailFast.valid)) &&
          assert(inited.toList)(hasSize(equalTo(1)))
        }
      },
      testM("FailFast.sequence") {
        implicit val inited: ListBuffer[FieldPath] = new ListBuffer[FieldPath]

        val vrMemo = (0 to 100)
          .map { i =>
            val field = Field(i.toString, 10)
            field.assert(init(field)(_) > i, _.minSizeError(10))
          }
          .combineAll
          .memoize

        val expectedToInit = (0 to 10).map(v => FieldPath(v.toString)).toList

        val beforeRun = inited.toList.length

        for {
          vr            <- vrMemo
          result1       <- vr
          afterFirstRun <- UIO(inited.toList)
          result2       <- vr
        } yield (
          assert(beforeRun)(equalTo(0)) &&
            assert(result1.errors)(equalTo(FieldError[ValidationError]("10", MinSize(10)) :: Nil)) &&
            assert(afterFirstRun)(equalTo(expectedToInit)) &&
            assertTrue(result2.isInvalid) &&
            assert(inited.toList)(equalTo(expectedToInit))
        )
      },
    )
}
