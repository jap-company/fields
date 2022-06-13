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
import cats.data._

trait CatsInteropInstances0 {
  type ValidatedUK[L[_], E] = Validated[L[E], Unit]
  type ValidatedNecUnit[E]  = Validated[NonEmptyChain[E], Unit]
  type ValidatedNelUnit[E]  = Validated[NonEmptyList[E], Unit]

  implicit def toFromCatsValidated[L[_]: Applicative: SemigroupK: Foldable]: FromCatsValidated[L] =
    new FromCatsValidated[L]

  /** ValidationResult instance for `cats.data.Validated` where error is collection type like `cats.data.NonEmptyList`
    * or `cats.data.NonEmptyChain`
    */
  class FromCatsValidated[L[_]](implicit A: Applicative[L], SK: SemigroupK[L], F: Foldable[L])
      extends AccumulateLike[ValidatedUK[L, _]] {
    def map[E, B](a: TypeClass[E])(f: E => B): TypeClass[B]    = a.leftMap(l => A.map[E, B](l)(f))
    def isValid[E](e: TypeClass[E]): Boolean                   = e.isValid
    def and[E](a: TypeClass[E], b: TypeClass[E]): TypeClass[E] = a.combine(b)(SK.algebra[E], implicitly)
    def errors[E](vr: TypeClass[E]): List[E]                   = vr.fold(F.toList, _ => Nil)
    def valid[E]: TypeClass[E]                                 = Validated.valid(())
    def invalid[E](e: E): TypeClass[E]                         = Validated.invalid(A.pure(e))
  }
}

trait CatsInteropInstances extends CatsInteropInstances0 {
  implicit val validatedNeqVR: ValidationResult[ValidatedNecUnit] = new FromCatsValidated[NonEmptyChain] {
    override def invalidMany[E](eh: E, et: E*): TypeClass[E] = Validated.invalid(NonEmptyChain.of(eh, et: _*))
  }
  implicit val validatedNelVR: ValidationResult[ValidatedNelUnit] = new FromCatsValidated[NonEmptyList] {
    override def invalidMany[E](eh: E, et: E*): TypeClass[E] = Validated.invalid(NonEmptyList.of(eh, et: _*))
  }
}

object CatsInterop extends CatsInteropInstances {

  /** [[jap.fields.ValidationEffect]] instance for any F[_] that has `cats.Monad` and `cats.Defer` instances */
  implicit def fromCatsMonadDefer[F[_]: Monad: Defer]: ValidationEffect[F] = new ValidationEffect[F] {
    def pure[A](a: A): F[A]                         = Monad[F].pure(a)
    def defer[A](a: => F[A]): F[A]                  = Defer[F].defer(a)
    def suspend[A](a: => A): F[A]                   = defer(pure(a))
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B] = Monad[F].flatMap(fa)(f)
    def map[A, B](fa: F[A])(f: A => B): F[B]        = Monad[F].map(fa)(f)
  }

  /** Base trait for ValidationModule where [[jap.fields.ValidationResult]] is Validated[NonEmptyChain[E], Unit] */
  abstract class ValidatedNecVM[F[_]: ValidationEffect, E] extends ValidationModule[F, ValidatedNecUnit, E]

  /** Base trait for ValidationModule where [[jap.fields.ValidationResult]] is Validated[NonEmptyList[E], Unit] */
  abstract class ValidatedNelVM[F[_]: ValidationEffect, E] extends ValidationModule[F, ValidatedNelUnit, E]

  /** Default ValidationModule where:
    *   - ValidationEffect is [[jap.fields.ValidationEffect.Sync]]
    *   - ValidationResult is Validated[NonEmptyList[E], Unit]
    *   - Error is ValidationError
    */
  object DefaultValidatedNelVM extends ValidatedNelVM[ValidationEffect.Sync, ValidationError]

  /** Default ValidationModule where:
    *   - ValidationEffect is [[jap.fields.ValidationEffect.Sync]]
    *   - ValidationResult is Validated[NonEmptyChain[E], Unit]
    *   - Error is ValidationError
    */
  object DefaultValidatedNecVM extends ValidatedNecVM[ValidationEffect.Sync, ValidationError]
}
