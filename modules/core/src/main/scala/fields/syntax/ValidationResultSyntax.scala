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

  /** See [[ValidationResult.isInvalid]] */
  def isInvalid(implicit VR: ValidationResult[VR]): Boolean = VR.isInvalid(vr)

  /** See [[ValidationResult.isValid]] */
  def isValid(implicit VR: ValidationResult[VR]): Boolean = VR.isValid(vr)

  /** See [[ValidationResult.and]] */
  def and(that: VR[E])(implicit VR: ValidationResult[VR]): VR[E] = VR.and(vr, that)

  /** See [[ValidationResult.and]] */
  def &&(that: VR[E])(implicit VR: ValidationResult[VR]): VR[E] = VR.and(vr, that)

  /** See [[ValidationResult.or]] */
  def or(that: VR[E])(implicit VR: ValidationResult[VR]): VR[E] = VR.or(vr, that)

  /** See [[ValidationResult.or]] */
  def ||(that: VR[E])(implicit VR: ValidationResult[VR]): VR[E] = VR.or(vr, that)

  /** See [[ValidationResult.errors]] */
  def errors(implicit VR: ValidationResult[VR]): List[E] = VR.errors(vr)

  /** See [[ValidationResult.when]] */
  def when(cond: Boolean)(implicit VR: ValidationResult[VR]): VR[E] = VR.when(cond)(vr)

  /** See [[ValidationResult.unless]] */
  def unless(cond: Boolean)(implicit VR: ValidationResult[VR]): VR[E] = VR.unless(cond)(vr)
}
