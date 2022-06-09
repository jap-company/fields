package jap.fields
package syntax

import scala.concurrent.Future

import ValidationResult._

trait GenericSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toFieldOps[P](field: Field[P]): FieldOps[P, F, VR, E] =
    new FieldOps(field)
}

final class FieldOps[P, F[_], VR[_], E](private val field: Field[P]) extends AnyVal {

  /** See [[ValidationModule.assertTrue]] */
  def assertTrue(cond: => Boolean, error: Field[P] => E)(implicit
      M: ValidationModule[F, VR, E]
  ): F[VR[E]] =
    M.assertTrue(field, cond, error)

  /** See [[ValidationModule.assert]] */
  def assert(cond: P => Boolean, error: Field[P] => E)(implicit
      M: ValidationModule[F, VR, E]
  ): F[VR[E]] =
    M.assert(field, cond, error)

  /** See [[ValidationModule.assertF]] */
  def assertF(cond: P => F[Boolean], error: Field[P] => E)(implicit
      M: ValidationModule[F, VR, E]
  ): F[VR[E]] =
    M.assertF(field, cond, error)

  /** See [[ValidationModule.check]] */
  def check(f: Field[P] => VR[E])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.check(field, f)

  /** See [[ValidationModule.checkF]] */
  def checkF(f: Field[P] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.checkF(field, f)

  /** Alias for [[equalTo]] */
  def ===[C](
      compared: C
  )(implicit M: ValidationModule[F, VR, E], FW: FailWithCompare[E], C: FieldCompare[P, C]): F[VR[E]] =
    equalTo(compared)

  /** Validates that [[Field]]#value is equal to `compared` */
  def equalTo[C](
      compared: C
  )(implicit M: ValidationModule[F, VR, E], FW: FailWithCompare[E], C: FieldCompare[P, C]): F[VR[E]] =
    assert(_ == C.value(compared), FW.equal(compared))

  /** Alias for [[notEqualTo]] */
  def !==[C](
      compared: C
  )(implicit M: ValidationModule[F, VR, E], FW: FailWithCompare[E], C: FieldCompare[P, C]): F[VR[E]] =
    notEqualTo(compared)

  /** Validates that [[Field]]#value is not equal to `compared` */
  def notEqualTo[C](compared: C)(implicit
      M: ValidationModule[F, VR, E],
      FW: FailWithCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] =
    assert(_ != C.value(compared), FW.notEqual(compared))

  /** Validates that [[Field]]#value is contained by `seq` */
  def in(seq: Seq[P])(implicit M: ValidationModule[F, VR, E], FW: FailWithOneOf[E]): F[VR[E]] =
    assert(seq.contains, FW.oneOf(seq))

  /** Combines all validations using AND */
  def all(f: Field[P] => F[VR[E]]*)(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.and(f.map(_.apply(field)).toList)

  /** Combines all validations using OR */
  def any(f: Field[P] => F[VR[E]]*)(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.or(f.map(_.apply(field)).toList)

  /** Runs validation only if true */
  def when(cond: Boolean)(f: Field[P] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.F.defer(if (cond) f(field) else M.validF)

  /** Runs validation only if false */
  def unless(cond: Boolean)(f: Field[P] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.F.defer(if (cond) M.validF else f(field))

  /** Validates [[Field]] using implicit [[ValidationPolicy]] */
  def validate(implicit M: ValidationModule[F, VR, E], P: ValidationPolicy[P, F, VR, E]): F[VR[E]] = P.validate(field)

  /** Validates [[Field]] using implicit [[ValidationPolicy]] */
  def validateEither(implicit M: ValidationModule[F, VR, E], P: ValidationPolicy[P, F, VR, E]): F[Either[VR[E], P]] =
    M.F.map(P.validate(field))(vr => if (M.VR.isValid(vr)) Right(field.value) else Left(vr))
}
