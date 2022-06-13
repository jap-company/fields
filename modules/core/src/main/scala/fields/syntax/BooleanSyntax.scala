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

trait BooleanSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toBooleanFieldOps(field: Field[Boolean]): BooleanFieldOps[F, VR, E] =
    new BooleanFieldOps(field)
}

final class BooleanFieldOps[F[_], VR[_], E](private val field: Field[Boolean]) extends AnyVal {

  /** Validates [[jap.fields.Field]]#value is `true` */
  def isTrue(implicit M: ValidationModule[F, VR, E], FW: FailWithCompare[E]): F[VR[E]] =
    M.fieldAssert[Boolean](field, _ == true, FW.equal(true))

  /** Validates [[jap.fields.Field]]#value is `false` */
  def isFalse(implicit M: ValidationModule[F, VR, E], FW: FailWithCompare[E]): F[VR[E]] =
    M.fieldAssert[Boolean](field, _ == false, FW.equal(false))
}
