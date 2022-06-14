package jap.fields

import FieldPathConversions._
import ValidationError._
import ValidationResult._
import ZIOInterop._
import scala.collection.mutable.ListBuffer

import zio._
import zio.test._
import zio.test.Assertion._

object TaskValidationModule extends FailFastVM[Task, ValidationError]
import TaskValidationModule._

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
        val vr    = field.ensure(init(field)(_) > 10, _.failMinSize(10))

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
            field.ensure(init(field)(_) > i, _.failMinSize(10))
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
            assert(result1.errors)(equalTo(MinSize("10", 10) :: Nil)) &&
            assert(afterFirstRun)(equalTo(expectedToInit)) &&
            assertTrue(result2.isInvalid) &&
            assert(inited.toList)(equalTo(expectedToInit))
        )
      },
    )
}
