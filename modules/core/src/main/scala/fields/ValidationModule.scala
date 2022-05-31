package jap.fields

import scala.annotation.implicitNotFound

import syntax._
import ValidationResult._
import ValidationEffect.Id

abstract class FailFastVM[F[_]: ValidationEffect, E]   extends ValidationModule[F, FailFast, E]
abstract class AccumulateVM[F[_]: ValidationEffect, E] extends ValidationModule[F, Accumulate, E]
object DefaultAccumulateVM                             extends AccumulateVM[Id, FieldError[ValidationError]]
object DefaultFailFastVM                               extends FailFastVM[Id, FieldError[ValidationError]]

@implicitNotFound("ValidationModule[${F}, ${VR}, ${E}] not found")
abstract class ValidationModule[F[_], VR[_], E](implicit
    val F: ValidationEffect[F],
    val VR: ValidationResult[VR],
) extends GenericSyntax[F, VR, E]
    with FailSyntax[F, VR, E]
    with BooleanSyntax[F, VR, E]
    with EffectValidationResultSyntax[F, VR, E]
    with NumericSyntax[F, VR, E]
    with OptionSyntax[F, VR, E]
    with StringSyntax[F, VR, E]
    with IterableSyntax[F, VR, E]
    with MapSyntax[F, VR, E]
    with PolicySyntax[F, VR, E]
    with FieldSyntax
    with ValidationResultSyntax {

  /** Syntax classes requires implicit ValidationModule in scope
    */
  implicit def Module: ValidationModule[F, VR, E] = this

  def valid: VR[E] = VR.valid[E]

  def validF: F[VR[E]] = F.pure(valid)

  def assertTrue[P](field: Field[P], cond: => Boolean, error: Field[P] => E): F[VR[E]] =
    F.suspend(if (cond) VR.valid else VR.invalid(error(field)))

  def assert[P](field: Field[P], cond: P => Boolean, error: Field[P] => E): F[VR[E]] =
    assertTrue(field, cond(field.value), error)

  def assertF[P](field: Field[P], cond: P => F[Boolean], error: Field[P] => E): F[VR[E]] =
    F.map(F.defer(cond(field.value)))(if (_) VR.valid else VR.invalid(error(field)))

  def check[P](field: Field[P], f: Field[P] => VR[E]): F[VR[E]] = F.suspend(f(field))

  def checkF[P](field: Field[P], f: Field[P] => F[VR[E]]): F[VR[E]] = F.defer(f(field))

  /** Combines `a` and `b` using AND. Short-circuits if ValidationResult Strategy supports it.
    */
  def and(a: F[VR[E]], b: F[VR[E]]) =
    VR.strategy match {
      case Strategy.Accumulate => F.map2(a, b)(VR.and)
      case Strategy.FailFast   => F.flatMap(a)(aa => if (VR.isInvalid(aa)) F.pure(aa) else b)
    }

  /** Combines `a` and `b` using OR. Short-circuits if `a` is valid
    */
  def or(a: F[VR[E]], b: F[VR[E]]) =
    F.flatMap(a)(aa =>
      if (VR.isValid(aa)) F.pure(aa)
      else F.map(b)(bb => VR.or(bb, aa))
    )

  /** Combines all validations using provided combine function. This has minor optimistions that checks size to handle
    * simple cases.
    */
  @inline def fold(list: List[F[VR[E]]], combine: (F[VR[E]], F[VR[E]]) => F[VR[E]]) =
    F.defer(
      if (list.size == 0) validF
      else if (list.size == 1) list.head
      else if (list.size == 2) combine(list(0), list(1))
      else list.reduce(combine)
    )

  /** Alias for and
    */
  def combineAll(list: List[F[VR[E]]]): F[VR[E]] = and(list)

  /** Combine all validations using AND
    */
  def and(list: List[F[VR[E]]]): F[VR[E]] = fold(list, and)

  /** Combine all validations using OR
    */
  def or(list: List[F[VR[E]]]): F[VR[E]] = fold(list, or)

  /** Shorthands for Policy
    */
  type PolicyBuilder[P] = ValidationPolicyBuilder[P, F, VR, E]
  type Policy[P]        = ValidationPolicy[P, F, VR, E]
  object Policy {
    def builder[P]: PolicyBuilder[P] = ValidationPolicy.builder
  }
}
