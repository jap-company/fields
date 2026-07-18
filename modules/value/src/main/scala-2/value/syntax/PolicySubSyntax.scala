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
package value
package syntax

object PolicySubSyntax extends PolicySubSyntax
trait PolicySubSyntax             {
  implicit def toPolicySubOps[P, F[_], V[_], E](policy: PolicyK[Field[P], F, V, E]): PolicySubOps[P, F, V, E] =
    new PolicySubOps(policy)
}
trait PolicySubDsl[F[_], V[_], E] {
  implicit def toPolicySubOps[P](policy: PolicyK[Field[P], F, V, E]): PolicySubOps[P, F, V, E] =
    new PolicySubOps(policy)
}

final class PolicySubOps[P, F[_], V[_], E](private val policy: PolicyK[Field[P], F, V, E]) extends AnyVal {

  /** Adds new subrule to builder. Uses `selector` to create [[fields.Field]], `rules` are applied to that field */
  // format: off
  def subRule[S](selector: P => S)(rules: Field[S] => RuleK[F, V, E]*): PolicyK[Field[P], F, V, E] = macro FieldMacro.policySubRuleMacro[P, S, F, V, E]
  // format: on

  /** Adds new subrule to builder. Same as `subRule` but for 2 subrules */
  // format: off
  def subRule[S1, S2](
      selector1: P => S1,
      selector2: P => S2,
  )(rules: (Field[S1], Field[S2]) => RuleK[F, V, E]*): PolicyK[Field[P], F, V, E] = macro FieldMacro.policySubRule2Macro[P, S1, S2, F, V, E]
  // format: on

  /** Adds new subrule to builder. Same as `subRule` but for 3 subrules */
  // format: off
  def subRule[S1, S2, S3](
      selector1: P => S1,
      selector2: P => S2,
      selector3: P => S3,
  )(rules: (Field[S1], Field[S2], Field[S3]) => RuleK[F, V, E]*): PolicyK[Field[P], F, V, E] = macro FieldMacro.policySubRule3Macro[P, S1, S2, S3, F, V, E]
  // format: on
}
