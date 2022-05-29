package jap.fields
package syntax

trait NumericSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toNumericFieldOps[P](field: Field[P]): NumericFieldOps[P, F, VR, E] =
    new NumericFieldOps(field)

}

final class NumericFieldOps[P, F[_], VR[_], E](private val field: Field[P]) extends AnyVal {
  def >=(p: P)(implicit M: ValidationModule[F, VR, E], N: Numeric[P]): F[VR[E]]  = gte(p)
  def gte(p: P)(implicit M: ValidationModule[F, VR, E], N: Numeric[P]): F[VR[E]] =
    M.assert[P](field, N.gteq(_, p), _.greaterEqual(p.toString))

  def >(p: P)(implicit M: ValidationModule[F, VR, E], N: Numeric[P]): F[VR[E]]  = gt(p)
  def gt(p: P)(implicit M: ValidationModule[F, VR, E], N: Numeric[P]): F[VR[E]] =
    M.assert[P](field, N.gt(_, p), _.greater(p.toString))

  def <=(p: P)(implicit M: ValidationModule[F, VR, E], N: Numeric[P]): F[VR[E]]  = lte(p)
  def lte(p: P)(implicit M: ValidationModule[F, VR, E], N: Numeric[P]): F[VR[E]] =
    M.assert[P](field, N.lteq(_, p), _.lessEqual(p.toString))

  def <(p: P)(implicit M: ValidationModule[F, VR, E], N: Numeric[P]): F[VR[E]]  = lt(p)
  def lt(p: P)(implicit M: ValidationModule[F, VR, E], N: Numeric[P]): F[VR[E]] =
    M.assert[P](field, N.lt(_, p), _.less(p.toString))

  def isBetween(from: P, to: P)(implicit M: ValidationModule[F, VR, E], N: Numeric[P]): F[VR[E]] =
    M.and(gte(from), lte(to))
}
