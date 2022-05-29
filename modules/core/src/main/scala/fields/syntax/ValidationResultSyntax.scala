package jap.fields
package syntax

trait ValidationResultSyntax {
  implicit final def toValidationResultOps[VR[_]: ValidationResult, E](vr: VR[E]): ValidationResultOps[VR, E] =
    new ValidationResultOps(vr)
  implicit final def toValidationResultIdOps[E](error: E): ValidationResultIdOps[E] = new ValidationResultIdOps(error)
}

final class ValidationResultIdOps[E](private val error: E) extends AnyVal {
  def invalid[VR[_]](implicit VR: ValidationResult[VR]): VR[E] = VR.invalid(error)
}

final class ValidationResultOps[VR[_], E](private val vr: VR[E]) extends AnyVal {
  def isInvalid(implicit VR: ValidationResult[VR]): Boolean            = VR.isInvalid(vr)
  def isValid(implicit VR: ValidationResult[VR]): Boolean              = VR.isValid(vr)
  def and(that: VR[E])(implicit VR: ValidationResult[VR]): VR[E]       = VR.and(vr, that)
  def &&(that: VR[E])(implicit VR: ValidationResult[VR]): VR[E]        = VR.and(vr, that)
  def or(that: VR[E])(implicit VR: ValidationResult[VR]): VR[E]        = VR.or(vr, that)
  def ||(that: VR[E])(implicit VR: ValidationResult[VR]): VR[E]        = VR.or(vr, that)
  def errors(implicit VR: ValidationResult[VR]): List[E]               = VR.errors(vr)
  def when(cond: Boolean)(implicit VR: ValidationResult[VR]): VR[E]    = VR.when(cond)(vr)
  def whenNot(cond: Boolean)(implicit VR: ValidationResult[VR]): VR[E] = VR.whenNot(cond)(vr)
}
