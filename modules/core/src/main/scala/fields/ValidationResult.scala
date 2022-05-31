package jap.fields

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import ValidationResult.Strategy

trait ValidationResult[VR[_]] {
  type TypeClass[E] = VR[E]

  /** Implement to give hint for short-circuiting
    */
  def strategy: Strategy

  def valid[E]: VR[E]

  def invalid[E](e: E): VR[E]

  /** Create Invalid VR with many errors. Override this for perfomance
    */
  def invalidMany[E](eh: E, et: E*): VR[E] = and(invalid(eh), sequence(et.map(invalid).toList))

  def isValid[E](e: VR[E]): Boolean

  def isInvalid[E](e: VR[E]): Boolean = !isValid(e)

  def errors[E](vr: VR[E]): List[E]

  def and[E](a: VR[E], b: VR[E]): VR[E]

  def map[E, B](a: VR[E])(f: E => B): VR[B]

  def when[E](cond: Boolean)(a: => VR[E]): VR[E] = if (cond) a else valid[E]

  def unless[E](cond: Boolean)(a: => VR[E]): VR[E] = if (cond) valid[E] else a

  def or[E](a: VR[E], b: VR[E]): VR[E] = if (isValid(a) || isValid(b)) valid else and(a, b)

  def sequence[E](results: VR[E]*): VR[E] = sequence[E](results.toList)

  def sequence[E](results: List[VR[E]]): VR[E] = {
    if (results.size == 0) valid
    else if (results.size == 1) results.head
    else if (results.size == 2) and(results(0), results(1))
    else results.foldLeft(valid[E])(and)
  }
}

trait FailFastLike[VR[_]]   extends ValidationResult[VR] { val strategy: Strategy = Strategy.FailFast   }
trait AccumulateLike[VR[_]] extends ValidationResult[VR] { val strategy: Strategy = Strategy.Accumulate }

object ValidationResult {
  def apply[VR[_]](implicit vr: ValidationResult[VR]): ValidationResult[VR] = vr
  sealed trait Strategy
  object Strategy {
    case object Accumulate extends Strategy
    case object FailFast   extends Strategy
  }

  sealed abstract class Accumulate[+E]
  implicit object Accumulate extends AccumulateLike[Accumulate] {
    case object Valid                                        extends Accumulate[Nothing]
    case class Invalid[+E] private[fields] (errors: List[E]) extends Accumulate[E]

    def map[E, B](a: Accumulate[E])(f: E => B): Accumulate[B] =
      a match {
        case Valid           => Valid
        case Invalid(errors) => Invalid(errors.map(f))
      }

    def valid[E]: Accumulate[E]                               = Valid
    override def invalidMany[E](eh: E, et: E*): Accumulate[E] = Invalid(eh :: et.toList)
    def invalid[E](e: E): Accumulate[E]                       = Invalid(List(e))
    def isValid[E](a: Accumulate[E]): Boolean                 = a == Valid

    def and[E](a: Accumulate[E], b: Accumulate[E]): Accumulate[E] =
      (a, b) match {
        case (Valid, Valid)                   => Valid
        case (aa: Invalid[E], bb: Invalid[E]) => Invalid(aa.errors ++ bb.errors)
        case (_: Invalid[E], _)               => a
        case _                                => b
      }

    def errors[E](vr: Accumulate[E]): List[E] = vr match {
      case e: Invalid[E] => e.errors
      case Valid         => Nil
    }
  }

  type FailFast[+E] = Either[E, Unit]
  implicit object FailFast extends FailFastLike[FailFast] {
    def map[E, B](a: FailFast[E])(f: E => B): FailFast[B]   = a.left.map(f)
    def valid[E]: FailFast[E]                               = Right(())
    override def invalidMany[E](eh: E, et: E*): FailFast[E] = Left(eh)
    def invalid[E](e: E): FailFast[E]                       = Left(e)
    def isValid[E](e: FailFast[E]): Boolean                 = e.isRight
    def and[E](a: FailFast[E], b: FailFast[E]): FailFast[E] = a.flatMap(_ => b)
    def errors[E](vr: FailFast[E]): List[E]                 = vr.left.toSeq.toList
  }
}
