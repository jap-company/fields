package jap.fields
package syntax

import scala.collection.generic.IsIterable

trait IterableSyntax[F[_], VR[_], E] { self: ValidationModule[F, VR, E] =>
  extension [P](field: Field[P])(using II: IsIterable[P], M: ValidationModule[F, VR, E]) {
    def nonEmpty(using CanFailEmpty[E]): F[VR[E]]             = M.assert(field, II(_).nonEmpty, _.emptyError)
    def min(min: Int)(using CanFailMinSize[E]): F[VR[E]]      = M.assert(field, II(_).size >= min, _.minSizeError(min))
    def max(max: Int)(using CanFailMaxSize[E]): F[VR[E]]      = M.assert(field, II(_).size <= max, _.maxSizeError(max))
    def each(check: Field[II.A] => F[VR[E]]): F[VR[E]]        = each((f, _) => check(f))
    def each(check: (Field[II.A], Int) => F[VR[E]]): F[VR[E]] =
      M.combineAll(
        II(field.value).zipWithIndex.map { case (a: II.A, i) => check(field.provideSub(i.toString, a), i) }.toList
      )
  }
}
