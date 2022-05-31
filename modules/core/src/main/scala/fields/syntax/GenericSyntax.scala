package jap.fields
package syntax

import scala.concurrent.Future

import ValidationResult._

trait GenericSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toFieldOps[P](field: Field[P]): FieldOps[P, F, VR, E] =
    new FieldOps(field)
}

final class FieldOps[P, F[_], VR[_], E](private val field: Field[P]) extends AnyVal {
  def assertTrue(cond: => Boolean, error: Field[P] => E)(implicit
      M: ValidationModule[F, VR, E]
  ): F[VR[E]] =
    M.assertTrue(field, cond, error)

  def assert(cond: P => Boolean, error: Field[P] => E)(implicit
      M: ValidationModule[F, VR, E]
  ): F[VR[E]] =
    M.assert(field, cond, error)

  def assertF(cond: P => F[Boolean], error: Field[P] => E)(implicit
      M: ValidationModule[F, VR, E]
  ): F[VR[E]] =
    M.assertF(field, cond, error)

  def check(f: Field[P] => VR[E])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.check(field, f)

  def checkF(f: Field[P] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.checkF(field, f)

  def ===[C](c: C)(implicit M: ValidationModule[F, VR, E], CF: CanFailCompare[E], C: FieldCompare[P, C]): F[VR[E]] =
    equalTo(c)

  def equalTo[C](c: C)(implicit M: ValidationModule[F, VR, E], CF: CanFailCompare[E], C: FieldCompare[P, C]): F[VR[E]] =
    assert(_ == C.value(c), CF.equal(c))

  def !==[C](c: C)(implicit M: ValidationModule[F, VR, E], CF: CanFailCompare[E], C: FieldCompare[P, C]): F[VR[E]] =
    notEqualTo(c)

  def notEqualTo[C](c: C)(implicit
      M: ValidationModule[F, VR, E],
      CF: CanFailCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] =
    assert(_ != C.value(c), CF.notEqual(c))

  def in(seq: Seq[P])(implicit M: ValidationModule[F, VR, E], CF: CanFailOneOf[E]): F[VR[E]] =
    assert(seq.contains, CF.oneOf(seq))

  /** Combines all validations using AND
    */
  def all(f: Field[P] => F[VR[E]]*)(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.and(f.map(_.apply(field)).toList)

  /** Combines all validations using OR
    */
  def any(f: Field[P] => F[VR[E]]*)(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.or(f.map(_.apply(field)).toList)

  /** Run validation only if true
    */
  def when(cond: Boolean)(f: Field[P] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.F.defer(if (cond) f(field) else M.validF)

  /** Run validation only if false
    */
  def unless(cond: Boolean)(f: Field[P] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.F.defer(if (cond) M.validF else f(field))

  def validate(implicit M: ValidationModule[F, VR, E], P: ValidationPolicy[P, F, VR, E]): F[VR[E]] = P.validate(field)
}
