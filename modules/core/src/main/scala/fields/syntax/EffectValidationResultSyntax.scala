package jap.fields
package syntax

import scala.annotation.implicitAmbiguous

import ValidationResult._

trait EffectValidationResultSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toEffectValidationResultOps(a: F[VR[E]]): EffectValidationResultOps[F, VR, E] =
    new EffectValidationResultOps[F, VR, E](a)

  implicit final def toEffectValidationResultSequenceOps(
      iterable: Iterable[F[VR[E]]]
  ): EffectValidationResultSequenceOps[F, VR, E] =
    new EffectValidationResultSequenceOps(iterable)
}

final class EffectValidationResultSequenceOps[F[_], VR[_], E](
    private val iterable: Iterable[F[VR[E]]]
) extends AnyVal {
  def combineAll(implicit M: ValidationModule[F, VR, E]): F[VR[E]] = M.combineAll(iterable)
}

final class EffectValidationResultOps[F[_], VR[_], E](private val a: F[VR[E]]) extends AnyVal {
  def isInvalid(implicit M: ValidationModule[F, VR, E]): F[Boolean]      = M.F.map(a)(M.VR.isInvalid)
  def isValid(implicit M: ValidationModule[F, VR, E]): F[Boolean]        = M.F.map(a)(M.VR.isValid)
  def errors(implicit M: ValidationModule[F, VR, E]): F[List[E]]         = M.F.map(a)(M.VR.errors)
  def or(b: F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]]  = M.or(a, b)
  def ||(b: F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]]  = M.or(a, b)
  def and(b: F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] = M.and(a, b)
  def &&(b: F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]]  = M.and(a, b)
}
