package jap.fields
package syntax

trait IterableSyntax[F[_], VR[_], E] { self: ValidationModule[F, VR, E] =>
  implicit final def toIterableFieldOps[I[_] <: Iterable[?], P](field: Field[I[P]]): IterableFieldOps[I, P, F, VR, E] =
    new IterableFieldOps(field)
}

final class IterableFieldOps[I[_] <: Iterable[?], P, F[_], VR[_], E](private val field: Field[I[P]]) extends AnyVal {
  def nonEmpty(implicit M: ValidationModule[F, VR, E]): F[VR[E]]      = M.assert[I[P]](field, _.nonEmpty, _.empty)
  def min(min: Int)(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.assert[I[P]](field, _.size >= min, _.minSize(min))
  def max(max: Int)(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.assert[I[P]](field, _.size <= max, _.maxSize(max))

  def each(check: Field[P] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    each((f, _) => check(f))

  def each(check: (Field[P], Int) => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.combineAll(
      field.value.zipWithIndex
        .map { case (a: P, i) => check(field.provideSub(i.toString, a), i) }
    )
}
