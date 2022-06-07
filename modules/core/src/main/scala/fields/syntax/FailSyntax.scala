package jap.fields
package syntax

import scala.concurrent.Future

import ValidationResult._

trait FailSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toFailFieldOps[P](field: Field[P]): FailFieldOps[P, F, VR, E] =
    new FailFieldOps(field)
}

final class FailFieldOps[P, F[_], VR[_], E](private val field: Field[P]) extends AnyVal {

  /** Just pathrought error */
  def error(error: E): E = error

  /** Useful when your error is wrapped in FieldError */
  def fieldError[EE](error: EE)(implicit ev: E =:= FieldError[EE]): E = FieldError(field.path, error).asInstanceOf[E]

  /** Returns InvalidError using [[FailWithInvalid]] typeclass */
  def invalidError(implicit FW: FailWithInvalid[E]): E = FW.invalid(field)

  /** Returns EmptyError using [[FailWithEmpty]] typeclass */
  def emptyError(implicit FW: FailWithEmpty[E]): E = FW.empty(field)

  /** Returns NonEmptyError using [[FailWithNonEmpty]] typeclass */
  def nonEmptyError(implicit FW: FailWithNonEmpty[E]): E = FW.nonEmpty(field)

  /** Returns CompareError using [[FailWithCompare]] typeclass */
  def greaterError[C](c: C)(implicit FW: FailWithCompare[E], C: FieldCompare[P, C]): E = FW.greater(c)(field)

  /** Returns CompareError using [[FailWithCompare]] typeclass */
  def greaterEqualError[C](c: C)(implicit FW: FailWithCompare[E], C: FieldCompare[P, C]): E =
    FW.greaterEqual(c)(field)

  /** Returns CompareError using [[FailWithCompare]] typeclass */
  def lessError[C](c: C)(implicit FW: FailWithCompare[E], C: FieldCompare[P, C]): E = FW.less(c)(field)

  /** Returns CompareError using [[FailWithCompare]] typeclass */
  def lessEqualError[C](c: C)(implicit FW: FailWithCompare[E], C: FieldCompare[P, C]): E =
    FW.lessEqual(c)(field)

  /** Returns CompareError using [[FailWithCompare]] typeclass */
  def equalError[C](c: C)(implicit FW: FailWithCompare[E], C: FieldCompare[P, C]): E = FW.equal(c)(field)

  /** Returns CompareError using [[FailWithCompare]] typeclass */
  def notEqualError[C](c: C)(implicit FW: FailWithCompare[E], C: FieldCompare[P, C]): E = FW.notEqual(c)(field)

  /** Returns MinSizeError using [[FailWithMinSize]] typeclass */
  def minSizeError(size: Int)(implicit FW: FailWithMinSize[E]): E = FW.minSize(size)(field)

  /** Returns MaxSizeError using [[FailWithMaxSize]] typeclass */
  def maxSizeError(size: Int)(implicit FW: FailWithMaxSize[E]): E = FW.maxSize(size)(field)

  /** Returns OneOfError using [[FailWithOneOf]] typeclass */
  def oneOfError[PP >: P](variants: Seq[PP])(implicit FW: FailWithOneOf[E]): E = FW.oneOf(variants)(field)

  /** Returns MessageError using [[FailWithMessage]] typeclass */
  def messageError(error: String, description: Option[String] = None)(implicit FW: FailWithMessage[E]): E =
    FW.message(error, description)(field)

  /** Returns MessageError using [[FailWithMessage]] typeclass */
  def messageError(error: String, description: String)(implicit FW: FailWithMessage[E]): E =
    FW.message(error, Some(description))(field)
}
