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

import PolicyKSyntax.*

trait PolicySubDsl[F[_], V[_], E] extends PolicySubSyntax

object PolicySubSyntax extends PolicySubSyntax
trait PolicySubSyntax {
  extension [P, F[_], V[_], E](policy: PolicyK[Field[P], F, V, E])(using R: RuleOps[F, V, E]) {

    /** Adds new subrule to builder. Uses `selector` to create [[fields.Field]], `rules` are applied to that field
      */
    inline def subRule[S](
        inline selector: P => S
    )(rules: PolicyK[Field[S], F, V, E]*): PolicyK[Field[P], F, V, E] = {
      val subPath = FieldPath.sub(selector)
      policy.mappedRule(_.downS(subPath, selector))(rules: _*)
    }

    /** Adds new subrule to builder. Same as `subRule` but for 2 subrules */
    inline def subRule[S1, S2](
        inline selector1: P => S1,
        inline selector2: P => S2,
    )(rules: PolicyK2[Field[S1], Field[S2], F, V, E]*): PolicyK[Field[P], F, V, E] = {
      val subPath1 = FieldPath.sub(selector1)
      val subPath2 = FieldPath.sub(selector2)
      policy.mappedRule(_.downS(subPath1, selector1), _.downS(subPath2, selector2))(
        rules.map(r => r(_: Field[S1], _: Field[S2])): _*
      )
    }

    /** Adds new subrule to builder. Same as `subRule` but for 3 subrules */
    inline def subRule[S1, S2, S3](
        inline selector1: P => S1,
        inline selector2: P => S2,
        inline selector3: P => S3,
    )(rules: PolicyK3[Field[S1], Field[S2], Field[S3], F, V, E]*): PolicyK[Field[P], F, V, E] = {
      val subPath1 = FieldPath.sub(selector1)
      val subPath2 = FieldPath.sub(selector2)
      val subPath3 = FieldPath.sub(selector3)

      policy.mappedRule(
        _.downS(subPath1, selector1),
        _.downS(subPath2, selector2),
        _.downS(subPath3, selector3),
      )(
        rules.map(r => r(_: Field[S1], _: Field[S2], _: Field[S3])): _*
      )
    }
  }
}
