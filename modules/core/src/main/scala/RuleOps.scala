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

import typeclass.*

abstract class RuleOps[F[_], V[_], E](implicit val F: Effect[F], val V: Validated[V]) {
  type R = RuleK[F, V, E]

  def apply(effect: F[V[E]]): R = wrap(effect)

  /** Wraps `rule` into tagged type */
  def wrap(effect: F[V[E]]): R = effect.asInstanceOf[R]

  /** Unwraps `rule` from tagged type */
  def unwrap(rule: R): F[V[E]] = rule.asInstanceOf[F[V[E]]]

  /** Lazily converts an `V[E]` to `Rule[F, VR, E]` */
  def pure(validated: => V[E]): R = RuleK(F.suspend(validated))

  /** Lazily converts an `F[V[E]]` to a `Rule[F, VR, E]` */
  def effect(effect: => F[V[E]]): R = RuleK(F.defer(effect))

  /** Defers Rule */
  def defer(rule: => R): R = RuleK(F.defer(rule.unwrap))

  /** Flattens Effect with Rule */
  def flatten(rule: => F[R]): R = effect(F.flatMap(rule)(_.effect))

  /** Converts `E` to a `R` */
  def invalid(error: => E): R = pure(V.invalid(error))

  /** Returns always valid `R` */
  val valid: R = pure(V.valid)

  /** Combines two `R`'s using logical AND. Short-circuits if `Validated.strategy` is
    * [[fields.typeclass.FailFastStrategy]]
    */
  def and(ra: => R, rb: => R): R

  /** Combines two `R`'s using logical OR, evaluating left-to-right and short-circuiting if the first rule is valid. */
  def or(ra: => R, rb: => R): R =
    RuleK {
      F.flatMap(ra.unwrap) { aa =>
        if (V.isValid(aa)) F.pure(aa)
        else F.map(rb.unwrap)(bb => V.or(aa, bb))
      }
    }

  /** Applies `rule` only when `test` pass */
  def when(test: => Boolean)(rule: => R): R = defer(if (test) rule else valid)

  /** Applies `rule` only when `test` pass */
  def whenF(test: => F[Boolean])(rule: => R): R = effect(F.flatMap(F.defer(test))(if (_) rule.unwrap else valid.unwrap))

  /** Ensures that if `test` pass else returns provided `V[E]` */
  def ensure(v: => V[E])(test: => Boolean): R = pure(if (test) V.valid else v)

  /** Ensures that if `test` pass else returns provided `V[E]` */
  def ensureF(v: => V[E])(test: => F[Boolean]): R = effect(F.map(F.defer(test))(if (_) V.valid else v))

  /** Asserts that if `test` pass else returns provided `E` */
  def assert(e: => E)(test: => Boolean): R = pure(if (test) V.valid else V.invalid(e))

  /** Asserts that if `test` pass else returns provided `E` */
  def assertF(e: => E)(test: => F[Boolean]): R = ensureF(V.invalid(e))(test)

  /** Combines all rules using AND */
  def combine(rules: List[R]): R = andAll(rules)

  /** Combines all rules using AND */
  def andAll(rules: List[R]): R = associateRight(rules)((left, right) => and(left, right))

  /** Combines all rules using OR */
  def orAll(rules: List[R]): R = associateRight(rules)((left, right) => or(left, right))

  /** Builds a right-associated rule tree while preserving left-to-right evaluation. */
  private def associateRight(rules: List[R])(combine: (R, R) => R): R =
    rules.reverse match {
      case Nil          => valid
      case last :: rest => rest.foldLeft(last)((acc, rule) => combine(rule, acc))
    }

  /** Modifies `rule` Validated value using `f` */
  def modify(rule: R)(f: V[E] => V[E]): R = RuleK(F.map(rule.effect)(f))

  /** Modifies `rule` Validated value using `f` */
  def modifyM(rule: R)(f: V[E] => R): R = RuleK(F.flatMap(rule.effect)(f.andThen(_.unwrap)))
}

trait RuleOpsConversions {
  implicit def validatedFromRuleOps[F[_], V[_], E](implicit R: RuleOps[F, V, E]): Validated[V] = R.V
  implicit def effectFromRuleOps[F[_], V[_], E](implicit R: RuleOps[F, V, E]): Effect[F]       = R.F
}

abstract class SyncRule[V[_]: Validated, E] extends RuleOps[Effect.Sync, V, E] {
  override def pure(validated: => V[E]): R = wrap(validated)
  override def effect(effect: => V[E]): R  = wrap(effect)
  override def defer(rule: => R): R        = rule
  override def flatten(rule: => R): R      = rule
  override def or(ra: => R, rb: => R): R   =
    wrap {
      val aa = ra.unwrap
      if (V.isValid(aa)) aa
      else V.or(aa, rb.unwrap)
    }
}

class AccumulateLikeSyncRule[V[_]: Validated, E] extends SyncRule[V, E] {
  override def and(ra: => R, rb: => R): R = wrap(V.and(ra.unwrap, rb.unwrap))
}

class FailFastLikeSyncRule[V[_]: Validated, E] extends SyncRule[V, E] {
  override def and(ra: => R, rb: => R): R =
    wrap {
      val aa = ra.unwrap
      if (V.isInvalid(aa)) F.pure(aa) else rb.unwrap
    }
}

object RuleOpsConversions extends RuleOpsConversions

object RuleOps {
  def instance[F[_], V[_], E](implicit F0: Effect[F], V0: Validated[V]): RuleOps[F, V, E] = {
    val accumulateSync = new AccumulateLikeSyncRule[V, E].asInstanceOf[RuleOps[F, V, E]]
    val failFastSync   = new FailFastLikeSyncRule[V, E].asInstanceOf[RuleOps[F, V, E]]

    V0 match {
      case _: AccumulateLike[V] =>
        F0 match {
          case Effect.SyncInstance => accumulateSync
          case _                   =>
            new RuleOps[F, V, E] {
              override def and(ra: => R, rb: => R): R =
                RuleK(F.flatMap(ra.unwrap)(aa => F.map(rb.unwrap)(bb => V.and(aa, bb))))
            }
        }
      case _: FailFastLike[V]   =>
        F0 match {
          case Effect.SyncInstance => failFastSync
          case _                   =>
            new RuleOps[F, V, E] {
              override def and(ra: => R, rb: => R): R =
                RuleK(F.flatMap(ra.unwrap)(aa => if (V.isInvalid(aa)) F.pure(aa) else rb.unwrap))
            }
        }
      case null                 => throw new MatchError(V0)
    }
  }
}
