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
package value.syntax

import typeclass.HasErrors
import value.Field

object PolicySyntax extends PolicySyntax
trait PolicySyntax {
  implicit def toPolicyOps[P, F[_], V[_], E](policy: PolicyK[Field[P], F, V, E]): PolicyOps[P, F, V, E] = new PolicyOps(
    policy
  )
}

class PolicyOps[P, F[_], V[_], E](private val policy: PolicyK[Field[P], F, V, E]) extends AnyVal {
  def validateEither(field: Field[P])(implicit R: RuleOps[F, V, E], E: HasErrors[V]): F[Either[List[E], P]] = {
    R.F.map(policy(field).effect) { v =>
      if (R.V.isValid(v)) Right(field.value)
      else Left(E.errors(v))
    }
  }

  def validate(field: Field[P]): RuleK[F, V, E] = policy(field)

  def validate(field: P): RuleK[F, V, E] = policy(Field(field))
}
