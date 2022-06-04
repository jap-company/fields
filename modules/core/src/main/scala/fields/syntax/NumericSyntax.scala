package jap.fields
package syntax

trait NumericSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toNumericFieldOps[P](field: Field[P]): NumericFieldOps[P, F, VR, E] =
    new NumericFieldOps(field)

}

final class NumericFieldOps[P, F[_], VR[_], E](private val field: Field[P]) extends AnyVal {

  /** Validates that [[Field]]#value is greater or equal to `compared` */
  def >=[C](compared: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Numeric[P],
      FW: FailWithCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] = gte(compared)

  /** Validates that [[Field]]#value is greater or equal to `compared` */
  def gte[C](compared: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Numeric[P],
      FW: FailWithCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] = M.assert[P](field, N.gteq(_, C.value(compared)), FW.greaterEqual(compared))

  /** Validates that [[Field]]#value is greater than `compared` */
  def >[C](compared: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Numeric[P],
      FW: FailWithCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] = gt(compared)

  /** Validates that [[Field]]#value is greater than `compared` */
  def gt[C](compared: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Numeric[P],
      FW: FailWithCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] = M.assert[P](field, N.gt(_, C.value(compared)), FW.greater(compared))

  /** Validates that [[Field]]#value is less or equal to `compared` */
  def <=[C](compared: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Numeric[P],
      FW: FailWithCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] = lte(compared)

  /** Validates that [[Field]]#value is less or equal to `compared` */
  def lte[C](compared: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Numeric[P],
      FW: FailWithCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] = M.assert[P](field, N.lteq(_, C.value(compared)), FW.lessEqual(compared))

  /** Validates that [[Field]]#value is less than `compared` */
  def <[C](compared: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Numeric[P],
      FW: FailWithCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] = lt(compared)

  /** Validates that [[Field]]#value is less than `compared` */
  def lt[C](compared: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Numeric[P],
      FW: FailWithCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] = M.assert[P](field, N.lt(_, C.value(compared)), FW.less(compared))

  /** Validates that [[Field]]#value is  greaterEqual than `from` and lessEqual `to` */
  def isBetween(from: P, to: P)(implicit
      M: ValidationModule[F, VR, E],
      N: Numeric[P],
      FW: FailWithCompare[E],
  ): F[VR[E]] = M.and(gte(from), lte(to))
}
