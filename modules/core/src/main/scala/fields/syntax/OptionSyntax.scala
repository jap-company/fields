package jap.fields
package syntax

trait OptionSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toOptionFieldOps[P](field: Field[Option[P]]): OptionFieldOps[P, F, VR, E] =
    new OptionFieldOps(field)
}

final class OptionFieldOps[P, F[_], VR[_], E](private val field: Field[Option[P]]) extends AnyVal {

  /** Alias for [[isDefined]] */
  def isSome(implicit M: ValidationModule[F, VR, E], FW: FailWithEmpty[E]): F[VR[E]] = isDefined

  /** Validates that [[Field]]#value is [[Some]] */
  def isDefined(implicit M: ValidationModule[F, VR, E], FW: FailWithEmpty[E]): F[VR[E]] =
    M.assert[Option[P]](field, _.isDefined, FW.empty)

  /** Alias for [[isEmpty]] */
  def isNone(implicit M: ValidationModule[F, VR, E], FW: FailWithNonEmpty[E]): F[VR[E]] = isEmpty

  /** Validates that [[Field]]#value is [[None]] */
  def isEmpty(implicit M: ValidationModule[F, VR, E], FW: FailWithNonEmpty[E]): F[VR[E]] =
    M.assert[Option[P]](field, _.isEmpty, FW.nonEmpty)

  /** Appies `check` to [[Field]]#value if it is [[Some]] or returns valid */
  def some(check: Field[P] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.F.defer(
      field.value match {
        case Some(value) => check(Field(field.path, value))
        case None        => M.validF
      }
    )
}
