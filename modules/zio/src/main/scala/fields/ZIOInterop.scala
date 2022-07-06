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

object ZIOInterop {

  /** [[jap.fields.typeclass.Effect]] instance for `zio.ZIO` */
  implicit def zioEffectInstance[R, E]: Effect[ZIO[R, E, _]] = effectInstance0.asInstanceOf[Effect[ZIO[R, E, _]]]

  private[this] val effectInstance0: ZIOEffect[Any, Nothing] = new ZIOEffect[Any, Nothing]
  private class ZIOEffect[R, E] extends Effect[ZIO[R, E, _]] {
    type F[A] = ZIO[R, E, A]
    def pure[A](a: A): F[A]                         = UIO(a)
    def suspend[A](a: => A): F[A]                   = UIO(a)
    def defer[A](a: => F[A]): F[A]                  = ZIO.effectSuspendTotal(a)
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B] = fa.flatMap(f)
    def map[A, B](fa: F[A])(f: A => B): F[B]        = fa.map(f)
  }

  /** ValidationModule with helpful stuff for ZIO */
  abstract class ZValidationModule[V[_], E](implicit val V: Validated[V]) extends syntax.all with ZIOSyntaxAll {
    implicit def effectInstance[ZR, ZE]: Effect[ZIO[ZR, ZE, _]] = zioEffectInstance

    type ZIORule[ZR, ZE]             = Rule[ZIO[ZR, ZE, *], V, E]
    type ZIOPolicy[ZR, ZE, P]        = ValidationPolicy[P, ZIO[ZR, ZE, *], V, E]
    type ZIOPolicyBuilder[ZR, ZE, P] = ValidationPolicyBuilder[P, ZIO[ZR, ZE, *], V, E]
    object ZIOPolicy { def builder[ZR, ZE, P]: ZIOPolicyBuilder[ZR, ZE, P] = ValidationPolicy.builder }

    type RIORule[ZR]             = ZIORule[ZR, Throwable]
    type RIOPolicy[ZR, P]        = ZIOPolicy[ZR, Throwable, P]
    type RIOPolicyBuilder[ZR, P] = ZIOPolicyBuilder[ZR, Throwable, P]
    object RIOPolicy { def builder[ZR, P]: RIOPolicyBuilder[ZR, P] = ValidationPolicy.builder }

    type IORule[E]             = ZIORule[Any, E]
    type IOPolicy[E, P]        = ZIOPolicy[Any, E, P]
    type IOPolicyBuilder[E, P] = ZIOPolicyBuilder[Any, E, P]
    object IOPolicy { def builder[E, P]: IOPolicyBuilder[E, P] = ValidationPolicy.builder }

    type TaskRule             = ZIORule[Any, Throwable]
    type TaskPolicy[P]        = ZIOPolicy[Any, Throwable, P]
    type TaskPolicyBuilder[P] = ZIOPolicyBuilder[Any, Throwable, P]
    object TaskPolicy { def builder[P]: TaskPolicyBuilder[P] = ValidationPolicy.builder }

    type UIORule             = ZIORule[Any, Nothing]
    type UIOPolicy[P]        = ZIOPolicy[Any, Nothing, P]
    type UIOPolicyBuilder[P] = ZIOPolicyBuilder[Any, Nothing, P]
    object UIOPolicy { def builder[P]: UIOPolicyBuilder[P] = ValidationPolicy.builder }

    type URIORule[R]             = ZIORule[R, Nothing]
    type URIOPolicy[R, P]        = ZIOPolicy[R, Nothing, P]
    type URIOPolicyBuilder[R, P] = ZIOPolicyBuilder[R, Nothing, P]
    object URIOPolicy { def builder[R, P]: URIOPolicyBuilder[R, P] = ValidationPolicy.builder }
  }

  object ZIOSyntaxAll extends ZIOSyntaxAll
  trait ZIOSyntaxAll  extends ZIOPolicySyntax

  object ZIOPolicySyntax extends ZIOPolicySyntax
  trait ZIOPolicySyntax {
    implicit def toFieldZIOPolicyOps[P, V[_], E](field: Field[P]): ZIOPolicyOps[P, V, E] = new ZIOPolicyOps(field)
  }
}

final class ZIOPolicyOps[P, V[_], E](private val field: Field[P]) extends AnyVal {
  def validateIO(implicit V: Validated[V], E: HasErrors[V], P: ValidationPolicy[P, UIO, V, E]): IO[List[E], P] =
    P.validate(field).effect.flatMap { v =>
      if (V.isValid(v)) IO.succeed(field.value)
      else IO.fail(E.errors(v))
    }
}
