package jap.fields
package syntax

trait OptionSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toOptionFieldOps[P](field: Field[Option[P]]): OptionFieldOps[P, F, VR, E] =
    new OptionFieldOps(field)
}

final class OptionFieldOps[P, F[_], VR[_], E](private val field: Field[Option[P]]) extends AnyVal {
  def isDefined(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.assert[Option[P]](field, _.isDefined, _.empty)

  def isEmpty(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.assert[Option[P]](field, _.isEmpty, _.nonEmpty)

  def some(f: Field[P] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    field.value match {
      case Some(value) => M.F.defer(f(Field(field.path, value)))
      case None        => M.validF
    }
}
