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

import typeclass.PrependPath

object RuleK {
  // ----TAGGED---- //
  trait Tag[+F[_], +V[_], +E] extends Any
  type Base[+F[_], +V[_], +E] = Any { type __Rule__ }
  type Type[+F[_], +V[_], +E] <: Base[F, V, E] with Tag[F, V, E]

  /** Same as [[RuleK.wrap]] */
  def apply[F[_], V[_], E](effect: F[V[E]]): RuleK[F, V, E] = wrap(effect)

  /** Wraps `rule` into tagged type */
  def wrap[F[_], V[_], E](effect: F[V[E]]): RuleK[F, V, E] = effect.asInstanceOf[RuleK[F, V, E]]

  /** Unwraps `rule` from tagged type */
  def unwrap[F[_], V[_], E](rule: RuleK[F, V, E]): F[V[E]] = rule.asInstanceOf[F[V[E]]]
  // ----TAGGED---- //

  implicit final class RuleExtensions[F[_], V[_], E](private val rule: RuleK[F, V, E]) extends AnyVal {

    /** Unwraps `rule` to its actual type */
    def unwrap: F[V[E]] = RuleK.unwrap(rule)

    /** Alias for [[unwrap]] */
    def effect: F[V[E]] = RuleK.unwrap(rule)

    /** Combines the result of `rule` with `f` without evaluating `rule` more than once. */
    def map(f: V[E] => V[E])(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
      RuleK(R.F.map(rule.effect)(current => R.V.and(current, f(current))))

    /** Combines `rule` and result of running `f` on `rule` using [[RuleK.and]]. This is tricky syntax for usage with
      * for-comprehension yield should always return valid Validated if you want this to work correctly.
      *
      * {{{
      * scala> import fields._
      * scala> import fields.defaultDsl._
      * scala> val intF = Field(4)
      * val intF: fields.Field[Int] = root:4
      * scala> for {
      *      |  _ <- intF > 4
      *      |  _ <- intF < 4
      *      |  _ <- intF !== 4
      *      | } yield V.valid
      * val res0:
      *   fields.Rule[[A] =>> A, fields.typeclass.Validated.Accumulate,
      *     fields.ValidationError
      *   ] = Invalid(List(root -> must be greater than 4, root -> must be less than 4, root -> must not be equal to 4))
      * }}}
      */
    def flatMap(f: V[E] => RuleK[F, V, E])(implicit R: RuleOps[F, V, E]): RuleK[F, V, E] =
      RuleK(R.F.flatMap(rule.effect)(current => R.F.map(f(current).effect)(next => R.V.and(current, next))))

    def mapK[FF[_]](f: F[V[E]] => FF[V[E]]): RuleK[FF, V, E] = RuleK(f(effect))

    def prependPath(path: FieldPath)(implicit R: RuleOps[F, V, E], P: PrependPath[E]): RuleK[F, V, E] =
      RuleK(R.F.map(unwrap)(R.V.map(_)((e: E) => P.prependPath(path, e))))
  }
}
