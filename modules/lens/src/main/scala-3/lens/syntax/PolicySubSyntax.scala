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
package lens
package syntax

import FieldSyntax.*
import scala.compiletime.summonFrom

trait PolicySubDsl[F[_], V[_], E] extends PolicySubSyntax

object PolicySubSyntax extends PolicySubSyntax
trait PolicySubSyntax {
  inline def inferBasePath: FieldPath =
    summonFrom {
      case lens: FieldLens[?, ?] => lens.path
      case path: FieldPath       => path
      case _                     => FieldPath.Root
    }

  extension [P, F[_], V[_], E](policy: PolicyK[P, F, V, E])(using R: RuleOps[F, V, E]) {

    /** Adds new subrule to builder. Uses `selector` to create [[fields.Field]], `rules` are applied to that field
      */
    inline def subRule[S](
        inline selector: P => S
    )(rules: FieldLens[P, S] => PolicyK[P, F, V, E]*): PolicyK[P, F, V, E] = {
      val base = FieldLens[P](inferBasePath)
      val sl   = base.sub(selector)
      PolicyK.combine(policy, rules.map(rule => rule(sl)): _*)
    }

    /** Adds new subrule to builder. Same as `subRule` but for 2 subrules */
    inline def subRule[S1, S2](
        inline selector1: P => S1,
        inline selector2: P => S2,
    )(rules: (FieldLens[P, S1], FieldLens[P, S2]) => PolicyK[P, F, V, E]*): PolicyK[P, F, V, E] = {
      val base = FieldLens[P](inferBasePath)
      val sl1  = base.sub(selector1)
      val sl2  = base.sub(selector2)
      PolicyK.combine(policy, rules.map(rule => rule(sl1, sl2)): _*)
    }

    /** Adds new subrule to builder. Same as `subRule` but for 3 subrules */
    inline def subRule[S1, S2, S3](
        inline selector1: P => S1,
        inline selector2: P => S2,
        inline selector3: P => S3,
    )(rules: (FieldLens[P, S1], FieldLens[P, S2], FieldLens[P, S3]) => PolicyK[P, F, V, E]*): PolicyK[P, F, V, E] = {
      val base = FieldLens[P](inferBasePath)
      val sl1  = base.sub(selector1)
      val sl2  = base.sub(selector2)
      val sl3  = base.sub(selector3)
      PolicyK.combine(policy, rules.map(rule => rule(sl1, sl2, sl3)): _*)
    }
  }
}
