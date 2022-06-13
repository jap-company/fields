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
package syntax

trait PolicySyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] with FieldSyntax =>
  implicit final class PolicyOps[P](policy: ValidationPolicyBuilder[P, F, VR, E]) {
    /** Adds new subrule to builder. Uses `selector` to create [[jap.fields.Field]], `rules` are applied to that field */
    def subRule[S](
        selector: P => S
    )(rules: Field[S] => F[VR[E]]*): ValidationPolicyBuilder[P, F, VR, E] =
      macro FieldMacro.policySubRuleMacro[P, S, F, VR, E]

    /** Adds new subrule to builder. Same as `subRule` but for 3 subrules */
    def subRule[S1, S2](
        selector1: P => S1,
        selector2: P => S2,
    )(rules: (Field[S1], Field[S2]) => F[VR[E]]*): ValidationPolicyBuilder[P, F, VR, E] =
      macro FieldMacro.policySubRule2Macro[P, S1, S2, F, VR, E]

    /** Adds new subrule to builder. Same as `subRule` but for 3 subrules */
    def subRule[S1, S2, S3](
        selector1: P => S1,
        selector2: P => S2,
        selector3: P => S3,
    )(rules: (Field[S1], Field[S2], Field[S3]) => F[VR[E]]*): ValidationPolicyBuilder[P, F, VR, E] =
      macro FieldMacro.policySubRule3Macro[P, S1, S2, S3, F, VR, E]
  }
}
