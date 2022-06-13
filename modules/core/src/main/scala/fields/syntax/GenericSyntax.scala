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

trait GenericSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toFieldOps[P](field: Field[P]): FieldOps[P, F, VR, E] =
    new FieldOps(field)
}

final class FieldOps[P, F[_], VR[_], E](private val field: Field[P]) extends AnyVal {

  /** See [[ValidationModule.fieldEnsure]] */
  def ensure(cond: P => Boolean, error: Field[P] => VR[E])(implicit
      M: ValidationModule[F, VR, E]
  ): F[VR[E]] = M.fieldEnsure(field, cond, error)

  /** See [[ValidationModule.fieldEnsureF]] */
  def ensureF(cond: P => F[Boolean], error: Field[P] => VR[E])(implicit
      M: ValidationModule[F, VR, E]
  ): F[VR[E]] = M.fieldEnsureF(field, cond, error)

  /** See [[ValidationModule.fieldAssert]] */
  def assert(cond: P => Boolean, error: Field[P] => E)(implicit
      M: ValidationModule[F, VR, E]
  ): F[VR[E]] = M.fieldEnsure(field, cond, error.andThen(M.invalid))

  /** See [[ValidationModule.fieldAssertF]] */
  def assertF(cond: P => F[Boolean], error: Field[P] => E)(implicit
      M: ValidationModule[F, VR, E]
  ): F[VR[E]] = M.fieldEnsureF(field, cond, error.andThen(M.invalid))

  /** See [[ValidationModule.fieldCheck]] */
  def check(f: Field[P] => VR[E])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.fieldCheck(field, f)

  /** See [[ValidationModule.fieldCheckF]] */
  def checkF(f: Field[P] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.fieldCheckF(field, f)

  /** Alias for [[equalTo]] */
  def ===[C](
      compared: C
  )(implicit M: ValidationModule[F, VR, E], FW: FailWithCompare[E], C: FieldCompare[P, C]): F[VR[E]] =
    equalTo[C](compared)

  /** Validates that [[jap.fields.Field]]#value is equal to `compared` */
  def equalTo[C](
      compared: C
  )(implicit M: ValidationModule[F, VR, E], FW: FailWithCompare[E], C: FieldCompare[P, C]): F[VR[E]] =
    assert(_ == C.value(compared), FW.equal[P, C](compared))

  /** Alias for [[notEqualTo]] */
  def !==[C](
      compared: C
  )(implicit M: ValidationModule[F, VR, E], FW: FailWithCompare[E], C: FieldCompare[P, C]): F[VR[E]] =
    notEqualTo[C](compared)

  /** Validates that [[jap.fields.Field]]#value is not equal to `compared` */
  def notEqualTo[C](compared: C)(implicit
      M: ValidationModule[F, VR, E],
      FW: FailWithCompare[E],
      C: FieldCompare[P, C],
  ): F[VR[E]] =
    assert(_ != C.value(compared), FW.notEqual[P, C](compared))

  /** Validates that [[jap.fields.Field]]#value is contained by `seq` */
  def in(seq: Seq[P])(implicit M: ValidationModule[F, VR, E], FW: FailWithOneOf[E]): F[VR[E]] =
    assert(seq.contains, FW.oneOf(seq))

  /** Combines all validations using AND */
  def all(f: Field[P] => F[VR[E]]*)(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.andAll(f.map(_.apply(field)).toList)

  /** Combines all validations using OR */
  def any(f: Field[P] => F[VR[E]]*)(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.orAll(f.map(_.apply(field)).toList)

  /** Runs validation only if true */
  def when(cond: Boolean)(f: Field[P] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.F.defer(if (cond) f(field) else M.validF)

  /** Runs validation only if false */
  def unless(cond: Boolean)(f: Field[P] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.F.defer(if (cond) M.validF else f(field))

  /** Validates [[jap.fields.Field]] using implicit [[ValidationPolicy]] */
  def validate(implicit P: ValidationPolicy[P, F, VR, E]): F[VR[E]] = P.validate(field)

  /** Validates [[jap.fields.Field]] using implicit [[ValidationPolicy]] */
  def validateEither(implicit M: ValidationModule[F, VR, E], P: ValidationPolicy[P, F, VR, E]): F[Either[VR[E], P]] =
    M.F.map(P.validate(field))(vr => if (M.VR.isValid(vr)) Right(field.value) else Left(vr))
}
