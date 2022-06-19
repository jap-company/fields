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
  type ValidatedUK[L[_], E] = CatsValidated[L[E], Unit]
  type ValidatedNecUnit[E]  = CatsValidated[NonEmptyChain[E], Unit]
  type ValidatedNelUnit[E]  = CatsValidated[NonEmptyList[E], Unit]

  implicit def toFromCatsValidated[L[_]: Applicative: SemigroupK: Foldable]: FromCatsValidated[L] =
    new FromCatsValidated[L]

  /** Validated instance for `cats.data.Validated` where error is collection type like `cats.data.NonEmptyList` or
    * `cats.data.NonEmptyChain`
    */
  class FromCatsValidated[L[_]](implicit A: Applicative[L], SK: SemigroupK[L], F: Foldable[L])
      extends AccumulateLike[ValidatedUK[L, _]] {
    def map[E, B](a: TypeClass[E])(f: E => B): TypeClass[B]    = a.leftMap(l => A.map[E, B](l)(f))
    def isValid[E](e: TypeClass[E]): Boolean                   = e.isValid
    def and[E](a: TypeClass[E], b: TypeClass[E]): TypeClass[E] = a.combine(b)(SK.algebra[E], implicitly)
    def errors[E](vr: TypeClass[E]): List[E]                   = vr.fold(F.toList, _ => Nil)
    def valid[E]: TypeClass[E]                                 = CatsValidated.valid(())
    def invalid[E](e: E): TypeClass[E]                         = CatsValidated.invalid(A.pure(e))
  }
}

trait CatsInteropInstances extends CatsInteropInstances0 {
  implicit val validatedNeqV: Validated[ValidatedNecUnit] = new FromCatsValidated[NonEmptyChain]
  implicit val validatedNelV: Validated[ValidatedNelUnit] = new FromCatsValidated[NonEmptyList]
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
  abstract class ValidatedNecVM[F[_]: Effect, E] extends ValidationModule[F, ValidatedNecUnit, E]

  /** Base trait for ValidationModule where [[jap.fields.typeclass.Validated]] is Validated[NonEmptyList[E], Unit] */
  abstract class ValidatedNelVM[F[_]: Effect, E] extends ValidationModule[F, ValidatedNelUnit, E]

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
