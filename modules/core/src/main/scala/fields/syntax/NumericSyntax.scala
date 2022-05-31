package jap.fields
package syntax

trait NumericSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toNumericFieldOps[P](field: Field[P]): NumericFieldOps[P, F, VR, E] =
    new NumericFieldOps(field)

}

final class NumericFieldOps[P, F[_], VR[_], E](private val field: Field[P]) extends AnyVal {
  def >=[C](p: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Numeric[P],
      CF: CanFailCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] =
    gte(p)

  def gte[C](p: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Numeric[P],
      CF: CanFailCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] =
    M.assert[P](field, N.gteq(_, C.value(p)), CF.greaterEqual(p))

  def >[C](p: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Numeric[P],
      CF: CanFailCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] = gt(p)

  def gt[C](p: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Numeric[P],
      CF: CanFailCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] =
    M.assert[P](field, N.gt(_, C.value(p)), CF.greater(p))

  def <=[C](p: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Numeric[P],
      CF: CanFailCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] = lte(p)

  def lte[C](p: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Numeric[P],
      CF: CanFailCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] =
    M.assert[P](field, N.lteq(_, C.value(p)), CF.lessEqual(p))

  def <[C](p: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Numeric[P],
      CF: CanFailCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] = lt(p)
  def lt[C](p: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Numeric[P],
      CF: CanFailCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] =
    M.assert[P](field, N.lt(_, C.value(p)), CF.less(p))

  def isBetween(from: P, to: P)(implicit
      M: ValidationModule[F, VR, E],
      N: Numeric[P],
      CF: CanFailCompare[E],
  ): F[VR[E]] =
    M.and(gte(from), lte(to))
}
