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

import scala.collection.generic.IsIterable
import scala.concurrent.Future

import ValidationResult._

trait PolicySyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] with FieldSyntax =>
  extension [P](builder: ValidationPolicyBuilder[P, F, VR, E]) {

    /** Adds new subrule to builder. Uses `selector` to create [[jap.fields.Field]], `rules` are applied to that field
      */
    inline def subRule[S](
        inline selector: P => S
    )(rules: Field[S] => F[VR[E]]*): ValidationPolicyBuilder[P, F, VR, E] =
      builder.fieldRule(_.sub(selector))(rules: _*)

    /** Adds new subrule to builder. Same as `subRule` but for 2 subrules */
    inline def subRule[S1, S2](
        inline selector1: P => S1,
        inline selector2: P => S2,
    )(rules: Tuple2[Field[S1], Field[S2]] => F[VR[E]]*): ValidationPolicyBuilder[P, F, VR, E] =
      builder.fieldRule[S1, S2](_.sub(selector1), _.sub(selector2))(rules.map(r => r(_: Field[S1], _: Field[S2])): _*)

    /** Adds new subrule to builder. Same as `subRule` but for 3 subrules */
    inline def subRule[S1, S2, S3](
        inline selector1: P => S1,
        inline selector2: P => S2,
        inline selector3: P => S3,
    )(rules: Tuple3[Field[S1], Field[S2], Field[S3]] => F[VR[E]]*): ValidationPolicyBuilder[P, F, VR, E] =
      builder.fieldRule[S1, S2, S3](_.sub(selector1), _.sub(selector2), _.sub(selector3))(
        rules.map(r => r(_: Field[S1], _: Field[S2], _: Field[S3])): _*
      )
  }

}
