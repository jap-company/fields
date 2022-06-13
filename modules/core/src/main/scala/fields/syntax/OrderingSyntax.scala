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

trait OrderingSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toOrderingFieldOps[P](field: Field[P]): OrderingFieldOps[P, F, VR, E] =
    new OrderingFieldOps(field)

}

final class OrderingFieldOps[P, F[_], VR[_], E](private val field: Field[P]) extends AnyVal {

  /** Validates that [[jap.fields.Field]]#value is greater or equal to `compared` */
  def >=[C](compared: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Ordering[P],
      FW: FailWithCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] = gte[C](compared)

  /** Validates that [[jap.fields.Field]]#value is greater or equal to `compared` */
  def gte[C](compared: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Ordering[P],
      FW: FailWithCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] = M.fieldAssert[P](field, N.gteq(_, C.value(compared)), FW.greaterEqual[P, C](compared))

  /** Validates that [[jap.fields.Field]]#value is greater than `compared` */
  def >[C](compared: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Ordering[P],
      FW: FailWithCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] = gt[C](compared)

  /** Validates that [[jap.fields.Field]]#value is greater than `compared` */
  def gt[C](compared: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Ordering[P],
      FW: FailWithCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] = M.fieldAssert[P](field, N.gt(_, C.value(compared)), FW.greater[P, C](compared))

  /** Validates that [[jap.fields.Field]]#value is less or equal to `compared` */
  def <=[C](compared: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Ordering[P],
      FW: FailWithCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] = lte(compared)

  /** Validates that [[jap.fields.Field]]#value is less or equal to `compared` */
  def lte[C](compared: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Ordering[P],
      FW: FailWithCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] = M.fieldAssert[P](field, N.lteq(_, C.value(compared)), FW.lessEqual[P, C](compared))

  /** Validates that [[jap.fields.Field]]#value is less than `compared` */
  def <[C](compared: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Ordering[P],
      FW: FailWithCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] = lt[C](compared)

  /** Validates that [[jap.fields.Field]]#value is less than `compared` */
  def lt[C](compared: C)(implicit
      M: ValidationModule[F, VR, E],
      N: Ordering[P],
      FW: FailWithCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] = M.fieldAssert[P](field, N.lt(_, C.value(compared)), FW.less[P, C](compared))

  /** Validates that [[jap.fields.Field]]#value is  greaterEqual than `from` and lessEqual `to` */
  def isBetween[FROM, TO](from: FROM, to: TO)(implicit
      M: ValidationModule[F, VR, E],
      N: Ordering[P],
      FW: FailWithCompare[E],
      CF: FieldCompare[P, FROM],
      CT: FieldCompare[P, TO],
  ): F[VR[E]] = M.and(gte[FROM](from), lte[TO](to))
}
