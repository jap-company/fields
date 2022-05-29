package jap.fields

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import ValidationResult.Strategy

trait ValidationResult[VR[_]] {
  def strategy: Strategy
  type TypeClass[E] = VR[E]

  def map[E, B](a: VR[E])(f: E => B): VR[B]
  def valid[E]: VR[E]
  def invalid[E](e: Iterable[E]): VR[E]
  def isValid[E](e: VR[E]): Boolean
  def errors[E](vr: VR[E]): List[E]
  def and[E](a: VR[E], b: VR[E]): VR[E]
  def when[E](cond: Boolean)(a: => VR[E]): VR[E]    = if (cond) a else valid[E]
  def whenNot[E](cond: Boolean)(a: => VR[E]): VR[E] = if (cond) valid[E] else a
  def invalid[E](e: E): VR[E]                       = invalid(List(e))
  def isInvalid[E](e: VR[E]): Boolean               = !isValid(e)
  def or[E](a: VR[E], b: VR[E]): VR[E]              = if (this.isValid(a) || this.isValid(b)) valid else this.and(a, b)
  def sequence[E](results: Iterable[VR[E]]): VR[E]  = results.foldLeft(valid[E])(and[E](_, _))
  def sequence[E](results: VR[E]*): VR[E]           = sequence[E](results)
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

    def valid[E]: Accumulate[E]                   = Valid
    def invalid[E](l: Iterable[E]): Accumulate[E] = Invalid(l.toList)
    def isValid[E](a: Accumulate[E]): Boolean     = a == Valid

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

  case class FailFast[+E](either: Either[E, Unit])
  implicit object FailFast extends FailFastLike[FailFast] {
    def map[E, B](a: FailFast[E])(f: E => B): FailFast[B]   = FailFast(a.either.left.map(f))
    def valid[E]: FailFast[E]                               = FailFast(Right(()))
    def invalid[E](e: Iterable[E]): FailFast[E]             = FailFast(Left(e.head))
    def isValid[E](e: FailFast[E]): Boolean                 = e.either.isRight
    def and[E](a: FailFast[E], b: FailFast[E]): FailFast[E] = FailFast(a.either.flatMap(_ => b.either))
    def errors[E](vr: FailFast[E]): List[E]                 = vr.either.left.toSeq.toList
  }
}
