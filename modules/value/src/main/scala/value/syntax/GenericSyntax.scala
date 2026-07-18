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

import fail.*
import typeclass.*
import value.Field.GetValue
import value.syntax.PolicySyntax.*

import scala.reflect.ClassTag

trait GenericDsl[F[_], V[_], E] {
  implicit final def toFieldOps[P](field: Field[P]): FieldOps[P, F, V, E] =
    new FieldOps(field)
}

object GenericSyntax extends GenericSyntax
trait GenericSyntax {
  implicit final def toFieldOps[F[_], V[_], E, P](field: Field[P]): FieldOps[P, F, V, E] =
    new FieldOps(field)
}

final class FieldOps[P, F[_], V[_], E](private val field: Field[P]) extends AnyVal {

  /** Runs validation only if true */
  def when(test: => Boolean)(f: Field[P] => RuleK[F, V, E])(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.when(test)(f(field))

  /** Runs validation only if false */
  def unless(test: => Boolean)(f: Field[P] => RuleK[F, V, E])(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.when(!test)(f(field))

  /** See [[RuleK.when]] */
  def when(test: Assertion[P])(rule: Field[P] => RuleK[F, V, E])(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.when(test(field.value))(rule(field))

  /** See [[RuleK.whenF]] */
  def whenF(test: P => F[Boolean])(rule: Field[P] => RuleK[F, V, E])(implicit
      R: RuleOps[F, V, E]
  ): RuleK[F, V, E] =
    R.whenF(test(field.value))(rule(field))

  /** Runs rule for subtype `PP` else is valid */
  def whenType[PP <: P](rule: Field[PP] => RuleK[F, V, E])(implicit
      R: RuleOps[F, V, E],
      CT: ClassTag[PP],
  ): RuleK[F, V, E] =
    R.defer {
      field.value match {
        case _: PP => rule(field.asInstanceOf[Field[PP]])
        case _     => R.valid
      }
    }

  /** See [[RuleK.ensure]] */
  def ensure(test: Assertion[P], error: Field[P] => V[E])(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.ensure(error(field))(test(field.value))

  /** See [[RuleK.ensureF]] */
  def ensureF(test: P => F[Boolean], error: Field[P] => V[E])(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.ensureF(error(field))(test(field.value))

  /** Like [[RuleK.ensure]] but for explicit error */
  def assert(test: Assertion[P], error: Field[P] => E)(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.assert(error(field))(test(field.value))

  /** Like [[RuleK.ensureF]] but for explicit error */
  def assertF(test: P => F[Boolean], error: Field[P] => E)(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.assertF(error(field))(test(field.value))

  /** Returns Suspended Outcome of applying `f` to `field` */
  def check(f: Field[P] => V[E])(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.pure(f(field))

  /** Returns Defered Outcome of applying `f` to `field` */
  def checkF(f: Field[P] => RuleK[F, V, E])(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.defer(f(field))

  /** Alias for [[equalTo]] */
  def ===[C](compared: => C)(implicit
      R: RuleOps[F, V, E],
      FW: FailWithCompare[E, P],
      C: CompareShow[P, C],
      G: GetValue[P, C],
  ): RuleK[F, V, E] =
    equalTo[C](compared)

  /** Validates that [[fields.Field]]#value is equal to `compared` */
  def equalTo[C](compared: => C)(implicit
      R: RuleOps[F, V, E],
      FW: FailWithCompare[E, P],
      C: CompareShow[P, C],
      G: GetValue[P, C],
  ): RuleK[F, V, E] =
    assert(_ == G.value(compared), FW.equal(compared))

  /** Alias for [[notEqualTo]] */
  def !==[C](compared: => C)(implicit
      R: RuleOps[F, V, E],
      FW: FailWithCompare[E, P],
      C: CompareShow[P, C],
      G: GetValue[P, C],
  ): RuleK[F, V, E] =
    notEqualTo[C](compared)

  /** Validates that [[fields.Field]]#value is not equal to `compared` */
  def notEqualTo[C](compared: => C)(implicit
      R: RuleOps[F, V, E],
      FW: FailWithCompare[E, P],
      C: CompareShow[P, C],
      G: GetValue[P, C],
  ): RuleK[F, V, E] =
    assert(_ != G.value(compared), FW.notEqual(compared))

  /** Validates that [[fields.Field]]#value is contained by `seq` */
  def in(seq: => Seq[P])(implicit R: RuleOps[F, V, E], FW: FailWithOneOf[E, P]): RuleK[F, V, E] =
    assert(seq.contains, FW.oneOf(seq))

  /** Combines all validations using AND */
  def all(f: Field[P] => RuleK[F, V, E]*)(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.andAll(f.map(_.apply(field)).toList)

  /** Combines all validations using OR */
  def any(f: Field[P] => RuleK[F, V, E]*)(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
    R.orAll(f.map(_.apply(field)).toList)

  /** Validates [[fields.Field]] using implicit [[PolicyK]] */
  def validate(implicit P: PolicyK[Field[P], F, V, E]): RuleK[F, V, E] = P(field)

  /** Validates [[fields.Field]] using implicit [[PolicyK]] */
  def validateEither(implicit
      R: RuleOps[F, V, E],
      P: PolicyK[Field[P], F, V, E],
      E: HasErrors[V],
  ): F[Either[List[E], P]] =
    P.validateEither(field)
}
