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

package jap.fields

import cats._
import cats.data.{Validated => CatsValidated, _}
import jap.fields.error.ValidationError
import jap.fields.fail._
import jap.fields.typeclass._
trait CatsInteropInstances0 {
  type ValidatedAccumulate[K[_], E] = CatsValidated[K[E], Unit]
  type ValidatedAccumulateNec[E]    = CatsValidated[NonEmptyChain[E], Unit]
  type ValidatedAccumulateNel[E]    = CatsValidated[NonEmptyList[E], Unit]

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
    def valid[E]: V[E]                 = CatsValidated.valid(())
    def invalid[E](e: E): V[E]         = CatsValidated.invalid(A.pure(e))
    def isValid[E](v: V[E]): Boolean   = v.isValid
    def and[E](a: V[E], b: V[E]): V[E] = a.combine(b)(SK.algebra[E], implicitly)
  }
}

trait CatsInteropInstances extends CatsInteropInstances0 {
  implicit val catsValidatedNecValidated: Validated[ValidatedAccumulateNec] = new FromCatsValidated[NonEmptyChain]
  implicit val catsValidatedNelValidated: Validated[ValidatedAccumulateNel] = new FromCatsValidated[NonEmptyList]
}

object CatsInterop extends CatsInteropInstances {

  /** [[jap.fields.typeclass.Effect]] instance for any F[_] that has `cats.Monad` and `cats.Defer` instances */
  implicit def fromCatsMonadDefer[F[_]: Monad: Defer]: Effect[F] = new Effect[F] {
    def pure[A](a: A): F[A]                         = Monad[F].pure(a)
    def defer[A](a: => F[A]): F[A]                  = Defer[F].defer(a)
    def suspend[A](a: => A): F[A]                   = defer(pure(a))
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B] = Monad[F].flatMap(fa)(f)
    def map[A, B](fa: F[A])(f: A => B): F[B]        = Monad[F].map(fa)(f)
  }

  /** Base trait for ValidationModule where [[jap.fields.typeclass.Validated]] is Validated[NonEmptyChain[E], Unit] */
  abstract class ValidatedNecVM[F[_]: Effect, E] extends ValidationModule[F, ValidatedAccumulateNec, E]

  /** Base trait for ValidationModule where [[jap.fields.typeclass.Validated]] is Validated[NonEmptyList[E], Unit] */
  abstract class ValidatedNelVM[F[_]: Effect, E] extends ValidationModule[F, ValidatedAccumulateNel, E]

  /** Default ValidationModule where:
    *   - Effect is [[jap.fields.typeclass.Effect.Sync]]
    *   - Validated is Validated[NonEmptyList[E], Unit]
    *   - Error is ValidationError
    */
  trait DefaultValidatedNelVM  extends ValidatedNelVM[Effect.Sync, ValidationError] with CanFailWithValidationError
  object DefaultValidatedNelVM extends DefaultValidatedNelVM

  /** Default ValidationModule where:
    *   - Effect is [[jap.fields.typeclass.Effect.Sync]]
    *   - Validated is Validated[NonEmptyChain[E], Unit]
    *   - Error is ValidationError
    */
  trait DefaultValidatedNecVM  extends ValidatedNecVM[Effect.Sync, ValidationError] with CanFailWithValidationError
  object DefaultValidatedNecVM extends DefaultValidatedNecVM
}
