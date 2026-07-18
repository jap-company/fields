/*
 * Copyright 2022 Jap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fields

import typeclass.*

import cats.*
import cats.data.{Validated as CatsValidated, *}

trait CatsInteropInstances0 {
  type ValidatedAccumulate[K[_], E] = CatsValidated[K[E], Unit]
  type ValidatedAccumulateNec[E]    = CatsValidated[NonEmptyChain[E], Unit]
  type ValidatedAccumulateNel[E]    = CatsValidated[NonEmptyList[E], Unit]

  implicit object ChainValidated extends AccumulateLike[Chain] {
    type V[E] = Chain[E]
    def valid[E]: V[E]                   = Chain.nil
    def invalid[E](e: E): V[E]           = Chain.one(e)
    def isValid[E](v: V[E]): Boolean     = v.isEmpty
    def and[E](a: V[E], b: V[E]): V[E]   = a ++ b
    def map[E](v: V[E])(f: E => E): V[E] = v.map(f)
  }

  implicit def toFromCatsValidated[K[_]: Applicative: SemigroupK]: FromCatsValidated[K] =
    new FromCatsValidated[K]

  /** Validated instance for `cats.data.Validated` where error is collection type like `cats.data.NonEmptyList` or
    * `cats.data.NonEmptyChain`
    */
  class FromCatsValidated[K[_]](implicit
      A: Applicative[K],
      SK: SemigroupK[K],
  ) extends AccumulateLike[ValidatedAccumulate[K, _]] {
    type V[E] = ValidatedAccumulate[K, E]
    def valid[E]: V[E]                   = CatsValidated.valid(())
    def invalid[E](e: E): V[E]           = CatsValidated.invalid(A.pure(e))
    def isValid[E](v: V[E]): Boolean     = v.isValid
    def and[E](a: V[E], b: V[E]): V[E]   = a.combine(b)(SK.algebra[E], implicitly)
    def map[E](v: V[E])(f: E => E): V[E] = v.leftMap(A.map(_)(f))
  }
}

trait CatsInteropInstances extends CatsInteropInstances0 {
  implicit val catsValidatedNecValidated: Validated[ValidatedAccumulateNec] = new FromCatsValidated[NonEmptyChain]
  implicit val catsValidatedNelValidated: Validated[ValidatedAccumulateNel] = new FromCatsValidated[NonEmptyList]
}

object CatsInterop extends CatsInteropInstances {

  /** [[fields.typeclass.Effect]] instance for any F[_] that has `cats.Monad` and `cats.Defer` instances */
  implicit def fromCatsMonadDefer[F[_]: Monad: Defer]: Effect[F] = new Effect[F] {
    def pure[A](a: A): F[A] = Monad[F].pure(a)

    def defer[A](a: => F[A]): F[A] = Defer[F].defer(a)

    def suspend[A](a: => A): F[A] = defer(pure(a))

    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B] = Monad[F].flatMap(fa)(f)

    def map[A, B](fa: F[A])(f: A => B): F[B] = Monad[F].map(fa)(f)
  }

  implicit object EvalRunSync extends RunSync[Eval] {
    def run[A](effect: Eval[A]): A = effect.value
  }

  implicit object NecHasErrors extends HasErrors[NonEmptyChain] {
    def errors[E](v: NonEmptyChain[E]): List[E] = v.iterator.toList
  }

  implicit object NelHasErrors extends HasErrors[NonEmptyList] {
    def errors[E](v: NonEmptyList[E]): List[E] = v.toList
  }

  implicit object ValidatedNelHasErrors extends HasErrors[ValidatedAccumulateNel] {
    def errors[E](v: ValidatedAccumulateNel[E]): List[E] = v.fold(_.toList, _ => Nil)
  }

  implicit object ValidatedNecHasErrors extends HasErrors[ValidatedAccumulateNec] {
    def errors[E](v: ValidatedAccumulateNec[E]): List[E] = v.fold(_.toChain.toList, _ => Nil)
  }

  trait ValidatedNelCanHasErrors {
    implicit def toHasErrors: HasErrors[ValidatedAccumulateNel] = ValidatedNelHasErrors
  }

  trait ValidatedNecCanHasErrors {
    implicit def toHasErrors: HasErrors[ValidatedAccumulateNec] = ValidatedNecHasErrors
  }
}
