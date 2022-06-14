/*
 * Copyright 2022 Jap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jap.fields

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

  /** Returns `error` if `vr` is invalid */
  def asError[E](vr: VR[E])(error: E): VR[E] = whenInvalid[E](vr)(_ => invalid(error))

  /** Returns `invalid` if `vr` is invalid */
  def asInvalid[E](vr: VR[E])(invalid: VR[E]): VR[E] = whenInvalid[E](vr)(_ => invalid)

  /** Returns `b` if `a` is valid else returns `a` */
  def whenValid[E](a: VR[E])(b: => VR[E]): VR[E] = if (isValid(a)) b else a

  /** Returns `f` applied to `a` if `a` is invalid else returns `a` */
  def whenInvalid[E](a: VR[E])(f: VR[E] => VR[E]): VR[E] = if (isInvalid(a)) f(a) else a

  /** Returns `vr` if `cond` is true else returns valid */
  def when[E](cond: Boolean)(vr: => VR[E]): VR[E] = if (cond) vr else valid[E]

  /** Returns `vr` if `cond` is false else returns valid */
  def unless[E](cond: Boolean)(vr: => VR[E]): VR[E] = if (cond) valid[E] else vr

  /** Combiness all `results` using AND */
  def sequence[E](results: List[VR[E]]): VR[E] = andAll[E](results)

  /** Combiness all `results` using AND */
  def andAll[E](results: List[VR[E]]): VR[E] = FoldUtil.fold[VR[E]](results, valid[E], and[E])

  /** Combiness all `results` using OR */
  def orAll[E](results: List[VR[E]]): VR[E] = FoldUtil.fold[VR[E]](results, valid[E], or[E])

  def traverse[A, E](a: List[A])(error: A => VR[E]): VR[E] =
    sequence(a.toList.map(error))

  def traverse[A, E](a: A*)(error: A => VR[E]): VR[E] =
    sequence(a.toList.map(error))
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
    case object Valid                       extends Accumulate[Nothing]
    case class Invalid[+E](errors: List[E]) extends Accumulate[E]

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
