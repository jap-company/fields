package jap.fields
package syntax

import scala.concurrent.Future

import ValidationResult._

trait FailSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toFailFieldOps[P](field: Field[P]): FailFieldOps[P, F, VR, E] =
    new FailFieldOps(field)
}

final class FailFieldOps[P, F[_], VR[_], E](private val field: Field[P]) extends AnyVal {

  /** Just pathrought error
    */
  def error(error: E): E = error

  /** Useful when your error is wrapped in FieldError
    */
  def fieldError[EE](error: EE)(implicit ev: E =:= FieldError[EE]): E = FieldError(field.path, error).asInstanceOf[E]

  def invalidError(implicit CF: CanFailInvalid[E]): E = CF.invalid(field)

  def emptyError(implicit CF: CanFailEmpty[E]): E = CF.empty(field)

  def nonEmptyError(implicit CF: CanFailNonEmpty[E]): E = CF.nonEmpty(field)

  def greaterError[C](c: C)(implicit CF: CanFailCompare[E], C: FieldCompare[P, C]): E = CF.greater(c)(field)

  def greaterEqualError[C](c: C)(implicit CF: CanFailCompare[E], C: FieldCompare[P, C]): E =
    CF.greaterEqual(c)(field)

  def lessError[C](c: C)(implicit CF: CanFailCompare[E], C: FieldCompare[P, C]): E = CF.less(c)(field)

  def lessEqualError[C](c: C)(implicit CF: CanFailCompare[E], C: FieldCompare[P, C]): E =
    CF.lessEqual(c)(field)

  def equalError[C](c: C)(implicit CF: CanFailCompare[E], C: FieldCompare[P, C]): E = CF.equal(c)(field)

  def notEqualError[C](c: C)(implicit CF: CanFailCompare[E], C: FieldCompare[P, C]): E = CF.notEqual(c)(field)

  def minSizeError(size: Int)(implicit CF: CanFailMinSize[E]): E = CF.minSize(size)(field)

  def maxSizeError(size: Int)(implicit CF: CanFailMaxSize[E]): E = CF.maxSize(size)(field)

  def oneOfError[PP >: P](variants: Seq[PP])(implicit CF: CanFailOneOf[E]): E = CF.oneOf(variants)(field)

  def messageError(error: String, description: Option[String] = None)(implicit CF: CanFailMessage[E]): E =
    CF.message(error, description)(field)
}
