package jap.fields
package syntax

import scala.collection.generic.IsIterable

trait IterableSyntax[F[_], VR[_], E] { self: ValidationModule[F, VR, E] =>
  extension [P](field: Field[P])(using II: IsIterable[P], M: ValidationModule[F, VR, E]) {

    /** Checks that collection is not empty */
    def nonEmpty(using FailWithEmpty[E]): F[VR[E]] = M.assert(field, II(_).nonEmpty, _.emptyError)

    /** Checks that collection minimum size is `min` */
    def minSize(min: Int)(using FailWithMinSize[E]): F[VR[E]] = M.assert(field, II(_).size >= min, _.minSizeError(min))

    /** Checks that collection maximum size is `max` */
    def maxSize(max: Int)(using FailWithMaxSize[E]): F[VR[E]] = M.assert(field, II(_).size <= max, _.maxSizeError(max))

    /** Applies `check` to each collection element, each should succeed */
    def each(check: Field[II.A] => F[VR[E]]): F[VR[E]] = each((f, _) => check(f))

    /** Applies `check` to each collection element, each should succeed */
    def each(check: (Field[II.A], Int) => F[VR[E]]): F[VR[E]] =
      M.and(II(field.value).zipWithIndex.map { case (a: II.A, i) => check(field.provideSub(i.toString, a), i) }.toList)

    /** Applies `check` to each collection element, any should succeed */
    def any(check: Field[II.A] => F[VR[E]]): F[VR[E]] = any((f, _) => check(f))

    /** Applies `check` to each collection element, any should succeed */
    def any(check: (Field[II.A], Int) => F[VR[E]]): F[VR[E]] =
      M.or(II(field.value).zipWithIndex.map { case (a: II.A, i) => check(field.provideSub(i.toString, a), i) }.toList)
  }
}
