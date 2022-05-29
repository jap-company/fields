package jap.fields

import cats._
import cats.data._

object CatsInterop {
  implicit def fromCatsMonadDefer[F[_]: Monad: Defer]: ValidationEffect[F] = new ValidationEffect[F] {
    def pure[A](a: A): F[A]                         = Monad[F].pure(a)
    def defer[A](a: => F[A]): F[A]                  = Defer[F].defer(a)
    def suspend[A](a: => A): F[A]                   = defer(pure(a))
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B] = Monad[F].flatMap(fa)(f)
    def map[A, B](fa: F[A])(f: A => B): F[B]        = Monad[F].map(fa)(f)
  }

  type ValidatedUK[L[_], E] = Validated[L[E], Unit]

  implicit def fromCatsValidated[L[_]: Applicative: SemigroupK: Foldable]: ValidationResult[ValidatedUK[L, _]] =
    new AccumulateLike[ValidatedUK[L, _]] {
      def map[E, B](a: TypeClass[E])(f: E => B): TypeClass[B]    = a.leftMap(l => Applicative[L].map[E, B](l)(f))
      def valid[E]: TypeClass[E]                                 = Validated.valid(())
      def invalid[E](e: Iterable[E]): TypeClass[E]               = sequence(e.map(invalid[E]))
      def isValid[E](e: TypeClass[E]): Boolean                   = e.isValid
      def and[E](a: TypeClass[E], b: TypeClass[E]): TypeClass[E] = a.combine(b)(SemigroupK[L].algebra[E], implicitly)
      def errors[E](vr: TypeClass[E]): List[E]                   = vr.fold(Foldable[L].toList, _ => Nil)
    }
}
