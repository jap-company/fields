package jap.fields

import syntax._
import ValidationResult._

object DefaultAccumulateValidationModule
    extends AccumulateValidationModule[ValidationEffect.Id, FieldError[ValidationError]]
object DefaultFailFastValidationModule
    extends FailFastValidationModule[ValidationEffect.Id, FieldError[ValidationError]]

abstract class FailFastValidationModule[F[_]: ValidationEffect, E: ValidationErrorMapper]
    extends ValidationModule[F, FailFast, E]
abstract class AccumulateValidationModule[F[_]: ValidationEffect, E: ValidationErrorMapper]
    extends ValidationModule[F, Accumulate, E]

abstract class ValidationModule[F[_], VR[_], E](implicit
    val F: ValidationEffect[F],
    val VR: ValidationResult[VR],
    val E: ValidationErrorMapper[E],
) extends BaseSyntax[F, VR, E]
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
  implicit def Module: ValidationModule[F, VR, E] = this
  def valid: VR[E]                                = VR.valid[E]
  def validF: F[VR[E]]                            = F.pure(valid)

  def assertTrue[P](field: Field[P], cond: => Boolean, error: ValidationContext[E, P] => E): F[VR[E]] =
    F.suspend(if (cond) VR.valid else VR.invalid(error(ValidationContext(field))))

  def assert[P](field: Field[P], cond: P => Boolean, error: ValidationContext[E, P] => E): F[VR[E]] =
    assertTrue(field, cond(field.value), error)

  def assertF[P](field: Field[P], cond: P => F[Boolean], error: ValidationContext[E, P] => E): F[VR[E]] =
    F.map(F.defer(cond(field.value)))(if (_) VR.valid else VR.invalid(error(ValidationContext(field))))

  def check[P](field: Field[P], f: ValidationContext[E, P] => VR[E]): F[VR[E]] = F.suspend(f(ValidationContext(field)))

  def checkF[P](field: Field[P], f: ValidationContext[E, P] => F[VR[E]]): F[VR[E]] =
    F.defer(f(ValidationContext(field)))

  // We do this here to ensure laziness of combining rules
  def and(a: F[VR[E]], b: F[VR[E]]) =
    VR.strategy match {
      case Strategy.Accumulate => F.map2(a, b)(VR.and)
      case Strategy.FailFast   => F.flatMap(a)(aa => if (VR.isInvalid(aa)) F.pure(aa) else b)
    }

  // We do this here to ensure laziness of combining rules
  def or(a: F[VR[E]], b: F[VR[E]]) =
    F.flatMap(a)(aa =>
      if (VR.isValid(aa)) F.pure(aa)
      else F.map(b)(bb => VR.or(bb, aa))
    )

  def combineAll(iterable: Iterable[F[VR[E]]]) = iterable.foldLeft(validF)(and)

  type Policy[P]        = ValidationPolicy[P, F, VR, E]
  type PolicyBuilder[P] = ValidationPolicyBuilder[P, F, VR, E]

  object Policy {
    def builder[P]: PolicyBuilder[P] = ValidationPolicy.builder
  }
}
