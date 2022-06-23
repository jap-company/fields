package jap.fields
package data

import typeclass._

/** Accumulate [[jap.fields.typeclass.Validated]] implementation */
sealed trait Accumulate[+E]
object Accumulate extends AccumulateLike[Accumulate] {
  case object Valid                       extends Accumulate[Nothing]
  case class Invalid[+E](errors: List[E]) extends Accumulate[E]

  def map[E, B](a: Accumulate[E])(f: E => B): Accumulate[B] =
    a match {
      case Valid           => Valid
      case Invalid(errors) => Invalid(errors.map(f))
    }

  def valid[E]: Accumulate[E]               = Valid
  def invalid[E](e: E): Accumulate[E]       = Invalid(List(e))
  def isValid[E](a: Accumulate[E]): Boolean = a == Valid

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

  implicit val accumulateValidated: Validated[Accumulate] = this
}
