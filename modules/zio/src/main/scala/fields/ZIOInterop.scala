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

trait ZIOEffectInstances {
  private[this] val effectInstance0: ZIOEffect[Any, Nothing] = new ZIOEffect[Any, Nothing]

  /** [[jap.fields.typeclass.Effect]] instance for `zio.ZIO` */
  implicit def zioEffect[R, E]: Effect[ZIO[R, E, _]] = effectInstance0.asInstanceOf[Effect[ZIO[R, E, _]]]
}

object ZIOInterop extends ZIOEffectInstances {

  /** ValidationModule with helpful stuff for ZIO */
  abstract class ZValidationModule[V[_], E](implicit val V: Validated[V])
      extends syntax.all
      with ZIOSyntaxAll
      with ZIOEffectInstances {

    type ZIORule[-ZR, +ZE]           = Rule[ZIO[ZR, ZE, *], V, E]
    type ZIOPolicy[ZR, ZE, P]        = ValidationPolicy[P, ZIO[ZR, ZE, *], V, E]
    type ZIOPolicyBuilder[ZR, ZE, P] = ValidationPolicyBuilder[P, ZIO[ZR, ZE, *], V, E]
    object ZIOPolicy {
      def builder[ZR, ZE, P]: ZIOPolicyBuilder[ZR, ZE, P] = ValidationPolicy.builder[P, ZIO[ZR, ZE, *], V, E]
    }

    type RIORule[-ZR]            = ZIORule[ZR, Throwable]
    type RIOPolicy[ZR, P]        = ZIOPolicy[ZR, Throwable, P]
    type RIOPolicyBuilder[ZR, P] = ZIOPolicyBuilder[ZR, Throwable, P]
    object RIOPolicy { def builder[ZR, P]: RIOPolicyBuilder[ZR, P] = ValidationPolicy.builder[P, RIO[ZR, *], V, E] }

    type IORule[+ZE]            = ZIORule[Any, ZE]
    type IOPolicy[ZE, P]        = ZIOPolicy[Any, ZE, P]
    type IOPolicyBuilder[ZE, P] = ZIOPolicyBuilder[Any, ZE, P]
    object IOPolicy { def builder[ZE, P]: IOPolicyBuilder[ZE, P] = ValidationPolicy.builder[P, IO[ZE, *], V, E] }

    type TaskRule             = ZIORule[Any, Throwable]
    type TaskPolicy[P]        = ZIOPolicy[Any, Throwable, P]
    type TaskPolicyBuilder[P] = ZIOPolicyBuilder[Any, Throwable, P]
    object TaskPolicy { def builder[P]: TaskPolicyBuilder[P] = ValidationPolicy.builder[P, Task, V, E] }

    type UIORule             = ZIORule[Any, Nothing]
    type UIOPolicy[P]        = ZIOPolicy[Any, Nothing, P]
    type UIOPolicyBuilder[P] = ZIOPolicyBuilder[Any, Nothing, P]
    object UIOPolicy { def builder[P]: UIOPolicyBuilder[P] = ValidationPolicy.builder[P, UIO, V, E] }

    type URIORule[-ZR]            = ZIORule[ZR, Nothing]
    type URIOPolicy[ZR, P]        = ZIOPolicy[ZR, Nothing, P]
    type URIOPolicyBuilder[ZR, P] = ZIOPolicyBuilder[ZR, Nothing, P]
    object URIOPolicy { def builder[ZR, P]: URIOPolicyBuilder[ZR, P] = ValidationPolicy.builder[P, URIO[ZR, *], V, E] }
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
