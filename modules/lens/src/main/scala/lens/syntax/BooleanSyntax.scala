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

import fail.*
import lens.syntax.GenericSyntax.*

object BooleanSyntax extends BooleanSyntax
trait BooleanSyntax {
  implicit final def toBooleanFieldOps[P, F[_], V[_], E](field: FieldLens[P, Boolean]): BooleanFieldOps[P, F, V, E] =
    new BooleanFieldOps(field)
}

trait BooleanDsl[F[_], V[_], E] {
  implicit final def toBooleanFieldOps[P](field: FieldLens[P, Boolean]): BooleanFieldOps[P, F, V, E] =
    new BooleanFieldOps(field)
}

final class BooleanFieldOps[P, F[_], V[_], E](private val field: FieldLens[P, Boolean]) extends AnyVal {

  /** Validates [[fields.Field]]#value is `true` */
  def isTrue(implicit R: RuleOps[F, V, E], FW: FailWithCompare[E, Boolean]): PolicyK[P, F, V, E] =
    field.assert(_ == true, FW.equal(true))

  /** Validates [[fields.Field]]#value is `false` */
  def isFalse(implicit R: RuleOps[F, V, E], FW: FailWithCompare[E, Boolean]): PolicyK[P, F, V, E] =
    field.assert(_ == false, FW.equal(false))
}
