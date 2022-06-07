package jap.fields

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import ValidationResult.Strategy

/** Typeclass that represents ValidationResult */
trait ValidationResult[VR[_]] {
  type TypeClass[E] = VR[E]

  /** Returns this Strategy */
  def strategy: Strategy

  /** Returns valid VR[E] */
  def valid[E]: VR[E]

  /** Returns invalid VR[E] with given error */
  def invalid[E](error: E): VR[E]

  /** Returns invalid VR[E] provided errors. Override this for perfomance */
  def invalidMany[E](eh: E, et: E*): VR[E] = and(invalid(eh), sequence(et.map(invalid).toList))

  /** Checks if `vr` is valid */
  def isValid[E](vr: VR[E]): Boolean

  /** Checks if `vr` is invalid */
  def isInvalid[E](vr: VR[E]): Boolean = !isValid(vr)

  /** Returns `vr` errors as [[scala.collection.immutable.List]] */
  def errors[E](vr: VR[E]): List[E]

  /** Combines `a` and `b` using AND */
  def and[E](a: VR[E], b: VR[E]): VR[E]

  /** Combines `a` and `b` using OR */
  def or[E](a: VR[E], b: VR[E]): VR[E] = if (isValid(a) || isValid(b)) valid else and(a, b)

  /** Maps `vr` using `f` function */
  def map[E, B](vr: VR[E])(f: E => B): VR[B]

  /** Returns `vr` if `cond` is true else returns valid */
  def when[E](cond: Boolean)(vr: => VR[E]): VR[E] = if (cond) vr else valid[E]

  /** Returns `vr` if `cond` is false else returns valid */
  def unless[E](cond: Boolean)(vr: => VR[E]): VR[E] = if (cond) valid[E] else vr

  /** Combiness all `results` using AND */
  def sequence[E](results: VR[E]*): VR[E] = sequence[E](results.toList)

  /** Combiness all `results` using AND */
  def sequence[E](results: List[VR[E]]): VR[E] = {
    if (results.size == 0) valid
    else if (results.size == 1) results.head
    else if (results.size == 2) and(results(0), results(1))
    else results.foldLeft(valid[E])(and)
  }
}

/** Base trait for [[jap.fields.ValidationResult]] that fail-fast */
trait FailFastLike[VR[_]] extends ValidationResult[VR] { val strategy: Strategy = Strategy.FailFast }

/** Base trait for [[jap.fields.ValidationResult]] that accumulate errors */
trait AccumulateLike[VR[_]] extends ValidationResult[VR] { val strategy: Strategy = Strategy.Accumulate }

object ValidationResult {

  /** Returns [[jap.fields.ValidationResult]] instance for given VR */
  def apply[VR[_]](implicit vr: ValidationResult[VR]): ValidationResult[VR] = vr

  /** Will change behaviour of combining ValidationResult's depending on strategy */
  sealed trait Strategy
  object Strategy {

    /** This strategy will accumulate all errors that occur */
    case object Accumulate extends Strategy

    /** When first error occur validation will short-circuit and will not execute other validations */
    case object FailFast extends Strategy
  }

  /** Accumulate [[jap.fields.ValidationResult]] implementation */
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

  /** FailFast [[jap.fields.ValidationResult]] implementation built on top of Either */
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
