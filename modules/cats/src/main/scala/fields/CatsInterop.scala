package jap.fields

import cats._
import cats.data._

object CatsInterop {
  type ValidatedUK[L[_], E] = Validated[L[E], Unit]
  type ValidatedNecUnit[E]  = Validated[NonEmptyChain[E], Unit]
  type ValidatedNelUnit[E]  = Validated[NonEmptyList[E], Unit]

  /** ValidationEffect instance for any F[_] that has [[cats.Monad]] and [[cats.Defer]] instances */
  implicit def fromCatsMonadDefer[F[_]: Monad: Defer]: ValidationEffect[F] = new ValidationEffect[F] {
    def pure[A](a: A): F[A]                         = Monad[F].pure(a)
    def defer[A](a: => F[A]): F[A]                  = Defer[F].defer(a)
    def suspend[A](a: => A): F[A]                   = defer(pure(a))
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B] = Monad[F].flatMap(fa)(f)
    def map[A, B](fa: F[A])(f: A => B): F[B]        = Monad[F].map(fa)(f)
  }

  /** ValidationResult instance for [[cats.data.Validated]] where error is collection type like
    * [[cats.data.NonEmptyList]] or [[cats.data.NonEmptyChain]]
    */
  implicit def fromCatsValidated[L[_]](implicit
      A: Applicative[L],
      SK: SemigroupK[L],
      F: Foldable[L],
      FNES: FromNonEmptySeq[L],
  ): ValidationResult[ValidatedUK[L, _]] =
    new AccumulateLike[ValidatedUK[L, _]] {
      def map[E, B](a: TypeClass[E])(f: E => B): TypeClass[B]    = a.leftMap(l => A.map[E, B](l)(f))
      def isValid[E](e: TypeClass[E]): Boolean                   = e.isValid
      def and[E](a: TypeClass[E], b: TypeClass[E]): TypeClass[E] = a.combine(b)(SK.algebra[E], implicitly)
      def errors[E](vr: TypeClass[E]): List[E]                   = vr.fold(F.toList, _ => Nil)
      def valid[E]: TypeClass[E]                                 = Validated.valid(())
      def invalid[E](e: E): TypeClass[E]                         = Validated.invalid(A.pure(e))
      override def invalidMany[E](eh: E, et: E*): TypeClass[E]   = Validated.invalid(FNES.fromNes(eh, et))
    }

  /** Base trait for ValidationModule where ValidationResult is Validated[NonEmptyChain[E], Unit] */
  abstract class ValidatedNecVM[F[_]: ValidationEffect, E] extends ValidationModule[F, ValidatedNecUnit, E]

  /** Base trait for ValidationModule where ValidationResult is Validated[NonEmptyList[E], Unit] */
  abstract class ValidatedNelVM[F[_]: ValidationEffect, E] extends ValidationModule[F, ValidatedNelUnit, E]

  /** Default ValidationModule where:
    *   - ValidationEffect is Id
    *   - ValidationResult is Validated[NonEmptyList[E], Unit]
    *   - Error is FieldError[ValidationError]
    */
  object DefaultValidatedNelVM extends ValidatedNelVM[ValidationEffect.Id, FieldError[ValidationError]]

  /** Default ValidationModule where:
    *   - ValidationEffect is Id
    *   - ValidationResult is Validated[NonEmptyChain[E], Unit]
    *   - Error is FieldError[ValidationError]
    */
  object DefaultValidatedNecVM extends ValidatedNecVM[ValidationEffect.Id, FieldError[ValidationError]]
}

/** Typeclass that gives capability to construct collection L[_] from first element `eh` and rest element `et` */
trait FromNonEmptySeq[L[_]] {
  def fromNes[E](eh: E, et: Seq[E]): L[E]
}
object FromNonEmptySeq      {
  implicit val necFromList: FromNonEmptySeq[NonEmptyChain] = new FromNonEmptySeq[NonEmptyChain] {
    def fromNes[E](eh: E, et: Seq[E]): NonEmptyChain[E] = NonEmptyChain(eh, et: _*)
  }
  implicit val nelFromList: FromNonEmptySeq[NonEmptyList]  = new FromNonEmptySeq[NonEmptyList] {
    def fromNes[E](eh: E, et: Seq[E]): NonEmptyList[E] = NonEmptyList(eh, et.toList)
  }
}
