package examples
package medium

import jap.fields.typeclass._

import scala.util.control.NoStackTrace

case class MediumErrors[E](errors: List[E]) extends Throwable with NoStackTrace {
  override def toString = errors.mkString("ERRORS:\n\t", "\n\t", "\n")
}
object MediumErrors {
  implicit object MediumErrorsValidated extends AccumulateLike[MediumErrors] {
    def valid[E]: MediumErrors[E]                                         =
      MediumErrors(Nil)
    def invalid[E](e: E): MediumErrors[E]                                 =
      MediumErrors(e :: Nil)
    def and[E](va: MediumErrors[E], vb: MediumErrors[E]): MediumErrors[E] =
      MediumErrors(va.errors ++ vb.errors)
    def isValid[E](v: MediumErrors[E]): Boolean                           =
      v.errors.isEmpty
    def errors[E](v: MediumErrors[E]): List[E]                            =
      v.errors
    def map[E, B](v: MediumErrors[E])(f: E => B): MediumErrors[B]         =
      v.copy(v.errors.map(f))
  }
}
