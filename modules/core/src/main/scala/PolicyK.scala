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

object PolicyK {
  final def apply[A, F[_], V[_], E](implicit R: RuleOps[F, V, E]): PolicyK[A, F, V, E] = valid

  final def valid[A, F[_], V[_], E](implicit R: RuleOps[F, V, E]): PolicyK[A, F, V, E] =
    _ => R.valid

  final def and[A, F[_], V[_], E](v1: PolicyK[A, F, V, E], v2: PolicyK[A, F, V, E])(implicit
      R: RuleOps[F, V, E]
  ): PolicyK[A, F, V, E] =
    value => R.and(v1(value), v2(value))

  final def or[A, F[_], V[_], E](v1: PolicyK[A, F, V, E], v2: PolicyK[A, F, V, E])(implicit
      R: RuleOps[F, V, E]
  ): PolicyK[A, F, V, E] =
    value => R.or(v1(value), v2(value))

  final def combine[A, F[_], V[_], E](first: PolicyK[A, F, V, E], other: PolicyK[A, F, V, E]*)(implicit
      R: RuleOps[F, V, E]
  ): PolicyK[A, F, V, E] = {
    val policies = (first :: other.toList).reverse
    policies.tail.foldLeft(policies.head) { (acc, policy) => value => R.and(policy(value), acc(value)) }
  }

  final def orAll[A, F[_], V[_], E](first: PolicyK[A, F, V, E], validations: PolicyK[A, F, V, E]*)(implicit
      R: RuleOps[F, V, E]
  ): PolicyK[A, F, V, E] = {
    val policies = (first :: validations.toList).reverse
    policies.tail.foldLeft(policies.head) { (acc, policy) => value => R.or(policy(value), acc(value)) }
  }
}

object PolicyKSyntax extends PolicyKSyntax
trait PolicyKSyntax {
  implicit final def toPolicyKOps[A, F[_], V[_], E](v: PolicyK[A, F, V, E]): PolicyKOps[A, F, V, E] =
    new PolicyKOps[A, F[_], V[_], E](v)
}

trait PolicyKDsl[F[_], V[_], E] {
  implicit final def toPolicyKOps[A](v: PolicyK[A, F, V, E]): PolicyKOps[A, F, V, E] =
    new PolicyKOps[A, F[_], V[_], E](v)
}

class PolicyKOps[A, F[_], V[_], E](private val v: PolicyK[A, F, V, E]) extends AnyVal {
  def mapK[FF[_]](f: F[V[E]] => FF[V[E]]): PolicyK[A, FF, V, E] = value => v(value).mapK(f)

  def contramap[B](f: B => A): PolicyK[B, F, V, E] = v.compose(f)

  def and(ov: PolicyK[A, F, V, E])(implicit R: RuleOps[F, V, E]): PolicyK[A, F, V, E] =
    PolicyK.and(v, ov)

  def or(ov: PolicyK[A, F, V, E])(implicit R: RuleOps[F, V, E]): PolicyK[A, F, V, E] =
    PolicyK.or(v, ov)

  def rule(ov: PolicyK[A, F, V, E]*)(implicit R: RuleOps[F, V, E]): PolicyK[A, F, V, E] =
    PolicyK.combine(v, ov*)

  def andAll(ov: PolicyK[A, F, V, E]*)(implicit R: RuleOps[F, V, E]): PolicyK[A, F, V, E] =
    PolicyK.combine(v, ov*)

  def orAll(ov: PolicyK[A, F, V, E]*)(implicit R: RuleOps[F, V, E]): PolicyK[A, F, V, E] =
    PolicyK.orAll(v, ov*)

  def mappedRule[B](s: A => B)(ov: PolicyK[B, F, V, E]*)(implicit R: RuleOps[F, V, E]): PolicyK[A, F, V, E] = {
    val subV = ov.toList.reverse match {
      case Nil                  => (_: B) => R.valid
      case last :: restReversed =>
        restReversed.foldLeft(last)((acc, policy) => value => R.and(policy(value), acc(value)))
    }

    and(a => subV(s(a)))
  }

  def mappedRule[B, C](
      s1: A => B,
      s2: A => C,
  )(ov: PolicyK2[B, C, F, V, E]*)(implicit R: RuleOps[F, V, E]): PolicyK[A, F, V, E] = {
    val subV = ov.toList.reverse match {
      case Nil                  => (_: B, _: C) => R.valid
      case last :: restReversed =>
        restReversed.foldLeft(last)((acc, policy) => (b, c) => R.and(policy(b, c), acc(b, c)))
    }

    and(v => subV(s1(v), s2(v)))
  }

  def mappedRule[B, C, D](
      s1: A => B,
      s2: A => C,
      s3: A => D,
  )(ov: PolicyK3[B, C, D, F, V, E]*)(implicit R: RuleOps[F, V, E]): PolicyK[A, F, V, E] = {
    val subV = ov.toList.reverse match {
      case Nil                  => (_: B, _: C, _: D) => R.valid
      case last :: restReversed =>
        restReversed.foldLeft(last)((acc, policy) => (b, c, d) => R.and(policy(b, c, d), acc(b, c, d)))
    }

    and(v => subV(s1(v), s2(v), s3(v)))
  }

}
