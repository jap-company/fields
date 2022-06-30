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

import zio._

import typeclass._

object ZioInterop {

  /** [[jap.fields.typeclass.Effect]] instance for `zio.ZIO` */
  implicit def effectInstance[R, E]: Effect[ZIO[R, E, _]] = effectInstance0.asInstanceOf[Effect[ZIO[R, E, _]]]

  private[this] val effectInstance0: ZioEffect[Any, Nothing] = new ZioEffect[Any, Nothing]
  private class ZioEffect[R, E] extends Effect[ZIO[R, E, _]] {
    type F[A] = ZIO[R, E, A]
    def pure[A](a: A): F[A]                         = UIO(a)
    def suspend[A](a: => A): F[A]                   = UIO(a)
    def defer[A](a: => F[A]): F[A]                  = ZIO.effectSuspendTotal(a)
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B] = fa.flatMap(f)
    def map[A, B](fa: F[A])(f: A => B): F[B]        = fa.map(f)
  }

  object ZioPolicySyntax extends ZioPolicySyntax
  trait ZioPolicySyntax {
    implicit def toFieldZioPolicyOps[P, V[_], E](field: Field[P]): ZioPolicyOps[P, V, E] = new ZioPolicyOps(field)
  }
}

final class ZioPolicyOps[P, V[_], E](private val field: Field[P]) extends AnyVal {
  def validateIO(implicit V: Validated[V], E: HasErrors[V], P: ValidationPolicy[P, UIO, V, E]): IO[List[E], P] =
    P.validate(field).effect.flatMap { v =>
      if (V.isValid(v)) IO.succeed(field.value)
      else IO.fail(E.errors(v))
    }
}
