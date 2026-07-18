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
import lens.FieldLens.GetValue
import typeclass.*

import scala.reflect.ClassTag

trait GenericDsl[F[_], V[_], E] {
  implicit final def toFieldOps[P, S](field: FieldLens[P, S]): FieldOps[P, S, F, V, E] =
    new FieldOps(field)
}

object GenericSyntax extends GenericSyntax
trait GenericSyntax {
  implicit final def toFieldOps[P, S, F[_], V[_], E](field: FieldLens[P, S]): FieldOps[P, S, F, V, E] =
    new FieldOps(field)
}

final class FieldOps[P, S, F[_], V[_], E](private val lens: FieldLens[P, S]) extends AnyVal {

  /** Runs validation only if true */
  def when(test: => Boolean)(f: FieldLens[P, S] => PolicyK[P, F, V, E])(implicit
      R: RuleOps[F, V, E]
  ): PolicyK[P, F, V, E] =
    v => R.when(test)(f(lens)(v))

  /** Runs validation only if false */
  def unless(test: => Boolean)(f: FieldLens[P, S] => PolicyK[P, F, V, E])(implicit
      R: RuleOps[F, V, E]
  ): PolicyK[P, F, V, E] =
    v => R.when(!test)(f(lens)(v))

  /** See [[RuleK.when]] */
  def when(test: Assertion[S])(rule: FieldLens[P, S] => PolicyK[P, F, V, E])(implicit
      R: RuleOps[F, V, E]
  ): PolicyK[P, F, V, E] = {
    val subRule = rule(lens)
    v =>
      lens.rule(v) { value =>
        R.when(test(value))(subRule(v))
      }
  }

  /** See [[RuleK.whenF]] */
  def whenF(test: S => F[Boolean])(rule: FieldLens[P, S] => PolicyK[P, F, V, E])(implicit
      R: RuleOps[F, V, E]
  ): PolicyK[P, F, V, E] = {
    val subRule = rule(lens)
    v =>
      lens.rule(v) { value =>
        R.whenF(test(value))(subRule(v))
      }
  }

  /** Runs rule for subtype `PP` else is valid */
  def whenType[SS <: S](rule: FieldLens[P, SS] => PolicyK[P, F, V, E])(implicit
      R: RuleOps[F, V, E],
      CT: ClassTag[SS],
  ): PolicyK[P, F, V, E] =
    v =>
      lens.rule(v) { selected =>
        R.defer {
          selected match {
            case _: SS => rule(lens.asInstanceOf[FieldLens[P, SS]])(v)
            case _     => R.valid
          }
        }
      }

  /** See [[RuleK.ensure]] */
  def ensure(test: Assertion[S], error: PathedFn[S, V[E]])(implicit R: RuleOps[F, V, E]): PolicyK[P, F, V, E] =
    lens.rule(_) { value =>
      R.ensure(error(lens.path, value))(test(value))
    }

  /** See [[RuleK.ensureF]] */
  def ensureF(test: S => F[Boolean], error: PathedFn[S, V[E]])(implicit
      R: RuleOps[F, V, E]
  ): PolicyK[P, F, V, E] =
    lens.rule(_) { value =>
      R.ensureF(error(lens.path, value))(test(value))
    }

  /** Like [[RuleK.ensure]] but for explicit error */
  def assert(test: Assertion[S], error: PathedFn[S, E])(implicit R: RuleOps[F, V, E]): PolicyK[P, F, V, E] =
    lens.rule(_) { value =>
      R.assert(error(lens.path, value))(test(value))
    }

  def assertParent(test: Assertion2[P, S], error: PathedFn[S, E])(implicit
      R: RuleOps[F, V, E]
  ): PolicyK[P, F, V, E] = { v =>
    lens.rule(v) { value =>
      R.assert(error(lens.path, value))(test(v, value))
    }
  }

  /** Like [[RuleK.ensureF]] but for explicit error */
  def assertF(test: S => F[Boolean], error: PathedFn[S, E])(implicit R: RuleOps[F, V, E]): PolicyK[P, F, V, E] =
    lens.rule(_) { value =>
      R.assertF(error(lens.path, value))(test(value))
    }

  /** Returns Suspended Outcome of applying `f` to `field` */
  def check(f: PathedFn[S, V[E]])(implicit R: RuleOps[F, V, E]): PolicyK[P, F, V, E] =
    lens.rule(_) { value =>
      R.pure(f(lens.path, value))
    }

  /** Returns Defered Outcome of applying `f` to `field` */
  def checkF(f: PathedFn[S, RuleK[F, V, E]])(implicit R: RuleOps[F, V, E]): PolicyK[P, F, V, E] =
    lens.rule(_) { value =>
      R.defer(f(lens.path, value))
    }

  /** Alias for [[equalTo]] */
  def ===[C](compared: C)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[S],
      FW: FailWithCompare[E, S],
      C: CompareShow[S, C],
      G: GetValue[P, S, C],
  ): PolicyK[P, F, V, E] =
    equalTo[C](compared)

  /** Validates that [[fields.Field]]#value is equal to `compared` */
  def equalTo[C](compared: C)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[S],
      FW: FailWithCompare[E, S],
      C: CompareShow[S, C],
      G: GetValue[P, S, C],
  ): PolicyK[P, F, V, E] =
    assertParent((p, v) => G.value[Boolean](p, compared)(O.compare(v, _) == 0, false), FW.equal[S, C](compared))

  /** Alias for [[notEqualTo]] */
  def !==[C](compared: C)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[S],
      FW: FailWithCompare[E, S],
      C: CompareShow[S, C],
      G: GetValue[P, S, C],
  ): PolicyK[P, F, V, E] =
    notEqualTo[C](compared)

  /** Validates that [[fields.Field]]#value is not equal to `compared` */
  def notEqualTo[C](compared: C)(implicit
      R: RuleOps[F, V, E],
      O: Ordering[S],
      FW: FailWithCompare[E, S],
      C: CompareShow[S, C],
      G: GetValue[P, S, C],
  ): PolicyK[P, F, V, E] =
    assertParent((p, v) => G.value[Boolean](p, compared)(O.compare(v, _) != 0, false), FW.notEqual[S, C](compared))

  /** Validates that [[fields.Field]]#value is contained by `seq` */
  def in(seq: Seq[S])(implicit R: RuleOps[F, V, E], FW: FailWithOneOf[E, P]): PolicyK[P, F, V, E] =
    assert(seq.contains, FW.oneOf(seq))

  /** Combines all validations using AND */
  def all(f: FieldLens[P, S] => PolicyK[P, F, V, E]*)(implicit R: RuleOps[F, V, E]): PolicyK[P, F, V, E] =
    v => R.andAll(f.map(_.apply(lens)(v)).toList)

  /** Combines all validations using OR */
  def any(f: FieldLens[P, S] => PolicyK[P, F, V, E]*)(implicit R: RuleOps[F, V, E]): PolicyK[P, F, V, E] =
    v => R.orAll(f.map(_.apply(lens)(v)).toList)

  /** Validates [[fields.Field]] using implicit [[PolicyK]] */
  def validate(implicit
      policy: PolicyK[S, F, V, E],
      R: RuleOps[F, V, E],
      PP: PrependPath[E],
  ): PolicyK[P, F, V, E] =
    lens.rule(_)(policy(_).prependPath(lens.path))

  /** Validates [[fields.Field]] using implicit [[PolicyK]] */
  def validateEither(p: P)(implicit
      R: RuleOps[F, V, E],
      P: PolicyK[P, F, V, E],
      E: HasErrors[V],
  ): F[Either[List[E], P]] =
    R.F.map(P(p).effect)(v => if (R.V.isValid(v)) Right(p) else Left(E.errors(v)))
}
