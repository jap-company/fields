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

import _root_.zio.*

object FieldsInteropZIO extends ZIOEffectInstances with ZIOPolicySyntax {
  trait FieldsSyntaxZIO extends ZIOPolicySyntax
}

trait ZIOEffectInstances {
  private[ZIOEffectInstances] val effectInstance0: ZIOEffect[Any, Nothing] = new ZIOEffect[Any, Nothing]

  /** [[fields.typeclass.Effect]] instance for `zio.ZIO` */
  implicit def zioEffect[R, E]: Effect[ZIO[R, E, _]] = effectInstance0.asInstanceOf[Effect[ZIO[R, E, _]]]
}

trait ZIOPolicySyntax {
  implicit def toFieldZIOPolicyOps[P, V[_], E](field: value.Field[P]): ZIOPolicyOps[P, V, E] = new ZIOPolicyOps(field)
}
final class ZIOPolicyOps[P, V[_], E](private val field: value.Field[P]) extends AnyVal {
  def validateIO(implicit V: Validated[V], E: HasErrors[V], P: PolicyK[value.Field[P], UIO, V, E]): IO[List[E], P] =
    P(field).effect.flatMap { v =>
      if (V.isValid(v)) ZIO.succeed(field.value)
      else ZIO.fail(E.errors(v))
    }
}
