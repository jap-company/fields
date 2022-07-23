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

import GenericSyntax._
import typeclass._
import fail._

object BooleanSyntax extends BooleanSyntax
trait BooleanSyntax {
  implicit final def toBooleanFieldOps[F[_], V[_], E](field: Field[Boolean]): BooleanFieldOps[F, V, E] =
    new BooleanFieldOps(field)
}

trait ModuleBooleanSyntax[F[_], V[_], E] {
  implicit final def toBooleanFieldOps(field: Field[Boolean]): BooleanFieldOps[F, V, E] =
    new BooleanFieldOps(field)
}

final class BooleanFieldOps[F[_], V[_], E](private val field: Field[Boolean]) extends AnyVal {

  /** Validates [[jap.fields.Field]]#value is `true` */
  def isTrue(implicit F: Effect[F], V: Validated[V], FW: FailWithCompare[E, Boolean]): Rule[F, V, E] =
    field.ensure(_ == true, _.failEqual(true))

  /** Validates [[jap.fields.Field]]#value is `false` */
  def isFalse(implicit F: Effect[F], V: Validated[V], FW: FailWithCompare[E, Boolean]): Rule[F, V, E] =
    field.ensure(_ == false, _.failEqual(false))
}
