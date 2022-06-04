package jap.fields
package syntax

trait IterableSyntax[F[_], VR[_], E] { self: ValidationModule[F, VR, E] =>
  implicit final def toIterableFieldOps[I[_] <: Iterable[?], P](field: Field[I[P]]): IterableFieldOps[I, P, F, VR, E] =
    new IterableFieldOps(field)
}

final class IterableFieldOps[I[_] <: Iterable[?], P, F[_], VR[_], E](private val field: Field[I[P]]) extends AnyVal {

  /** Checks that collection is not empty */
  def nonEmpty(implicit M: ValidationModule[F, VR, E], FW: FailWithEmpty[E]): F[VR[E]] =
    M.assert[I[P]](field, _.nonEmpty, FW.empty)

  /** Checks that collection minimum size is `min` */
  def minSize(min: Int)(implicit M: ValidationModule[F, VR, E], FW: FailWithMinSize[E]): F[VR[E]] =
    M.assert[I[P]](field, _.size >= min, FW.minSize(min))

  /** Checks that collection maximum size is `max` */
  def maxSize(max: Int)(implicit M: ValidationModule[F, VR, E], FW: FailWithMaxSize[E]): F[VR[E]] =
    M.assert[I[P]](field, _.size <= max, FW.maxSize(max))

  /** Applies `check` to each collection element */
  def each(check: Field[P] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    each((f, _) => check(f))

  /** Applies `check` to each collection element */
  def each(check: (Field[P], Int) => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.and(field.value.zipWithIndex.map { case (a: P, i) => check(field.provideSub(i.toString, a), i) }.toList)

  /** Applies `check` to each collection element, any should succeed */
  def any(check: Field[P] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] = any((f, _) => check(f))

  /** Applies `check` to each collection element, any should succeed */
  def any(check: (Field[P], Int) => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.or(field.value.zipWithIndex.map { case (a: P, i) => check(field.provideSub(i.toString, a), i) }.toList)
}
