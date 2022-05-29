package jap.fields
package syntax

import scala.concurrent.Future

import ValidationResult._

trait BaseSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toFieldOps[P](field: Field[P]): FieldOps[P, F, VR, E] =
    new FieldOps(field)
}

final class FieldOps[P, F[_], VR[_], E](private val field: Field[P]) extends AnyVal {
  def assertTrue(cond: => Boolean, error: ValidationContext[E, P] => E)(implicit
      M: ValidationModule[F, VR, E]
  ): F[VR[E]] =
    M.assertTrue(field, cond, error)

  def assert(cond: P => Boolean, error: ValidationContext[E, P] => E)(implicit
      M: ValidationModule[F, VR, E]
  ): F[VR[E]] =
    M.assert(field, cond, error)

  def assertF(cond: P => F[Boolean], error: ValidationContext[E, P] => E)(implicit
      M: ValidationModule[F, VR, E]
  ): F[VR[E]] =
    M.assertF(field, cond, error)

  def check(f: ValidationContext[E, P] => VR[E])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.check(field, f)

  def checkF(f: ValidationContext[E, P] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.checkF(field, f)

  def ===(value: P)(implicit M: ValidationModule[F, VR, E]): F[VR[E]] = equalTo(value)

  def equalTo(value: P)(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    assert(_ == value, _.equal(value.toString))

  def !==(value: P)(implicit M: ValidationModule[F, VR, E]): F[VR[E]] = notEqualTo(value)

  def notEqualTo(value: P)(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    assert(_ != value, _.notEqual(value.toString))

  def ===[PP <: P](f2: Field[PP])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] = equalTo(f2)

  def equalTo[PP <: P](f2: Field[PP])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    assert(_ == f2.value, _.equal(f2.fullPath))

  def !==[PP <: P](f2: Field[PP])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] = notEqualTo(f2)

  def notEqualTo[PP <: P](f2: Field[PP])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    assert(_ != f2.value, _.notEqual(f2.fullPath))

  def in(seq: Seq[P])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    assert(seq.contains, _.oneOf(seq.map(_.toString)))

  def validate(implicit M: ValidationModule[F, VR, E], P: ValidationPolicy[P, F, VR, E]): F[VR[E]] = P.validate(field)
}
