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

package jap

import jap.fields.typeclass._
package object fields {

  /** Rule is tagged type alias for F[V[E]] If used this way we do not add any allocations while having separate syntax
    * for Rule. Also we get ability to convert back and forth.
    */
  type Rule[F[_], V[_], E] <: Rule.Type[F, V, E]
  object Rule {
    // ----TAGGED---- //
    trait Tag[F[_], V[_], +E] extends Any
    type Base[F[_], V[_], +E] = Any { type __Rule__ }
    type Type[F[_], V[_], +E] <: Base[F, V, E] with Tag[F, V, E]

    /** Same as [[Rule.wrap]] */
    @inline def apply[F[_], V[_], E](effect: F[V[E]]): Rule[F, V, E] = wrap(effect)

    /** Wraps `rule` into tagged type */
    @inline def wrap[F[_], V[_], E](effect: F[V[E]]): Rule[F, V, E] = effect.asInstanceOf[Rule[F, V, E]]

    /** Unwraps `rule` from tagged type */
    @inline def unwrap[F[_], V[_], E](rule: Rule[F, V, E]): F[V[E]] = rule.asInstanceOf[F[V[E]]]
    // ----TAGGED---- //

    /** Lazily converts an `V[E]` to `Rule[F, VR, E]` */
    def pure[F[_], V[_], E](validated: => V[E])(implicit F: Effect[F]): Rule[F, V, E] =
      Rule(F.suspend(validated))

    /** Lazily converts an `F[V[E]]` to a `Rule[F, VR, E]` */
    def effect[F[_], V[_], E](effect: => F[V[E]])(implicit F: Effect[F]): Rule[F, V, E] =
      Rule(F.defer(effect))

    /** Defers Rule */
    def defer[F[_], V[_], E](rule: => Rule[F, V, E])(implicit F: Effect[F]): Rule[F, V, E] =
      Rule(F.defer(rule.unwrap))

    /** Converts `E` to a `Rule[F, V, E]` */
    def invalid[F[_], V[_], E](error: => E)(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
      pure(V.invalid(error))

    /** Returns always valid `Rule[F, V, E]` */
    def valid[F[_], V[_], E](implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
      pure(V.valid)

    /** Combines two `Rule[F, V, E]`'s using logical AND. Short-circuits if `Validated.strategy` is
      * [[jap.fields.typeclass.FailFastStrategy]]
      */
    def and[F[_], V[_], E](ra: Rule[F, V, E], rb: Rule[F, V, E])(implicit
        F: Effect[F],
        V: Validated[V],
    ): Rule[F, V, E] =
      Rule {
        V.strategy match {
          case AccumulateStrategy => F.flatMap(ra.unwrap)(aa => F.map(rb.unwrap)(bb => V.and(aa, bb)))
          case FailFastStrategy   => F.flatMap(ra.unwrap)(aa => if (V.isInvalid(aa)) F.pure(aa) else rb.unwrap)
        }
      }

    /** Combines two `Rule[F, V, E]`'s using logical OR. Short-circuits if first rule is valid */
    def or[F[_], V[_], E](ra: Rule[F, V, E], rb: Rule[F, V, E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
      Rule {
        F.flatMap(ra.unwrap) { aa =>
          if (V.isValid(aa)) F.pure(aa)
          else F.map(rb.unwrap)(bb => V.or(bb, aa))
        }
      }

    /** Applies `rule` only when `test` pass */
    def when[F[_]: Effect, V[_]: Validated, E](test: => Boolean)(rule: => Rule[F, V, E]): Rule[F, V, E] =
      defer(if (test) rule else valid)

    /** Applies `rule` only when `test` pass */
    def whenF[F[_], V[_], E](test: => F[Boolean])(rule: => Rule[F, V, E])(implicit
        F: Effect[F],
        V: Validated[V],
    ): Rule[F, V, E] =
      effect(F.flatMap(F.defer(test))(if (_) rule.unwrap else valid.unwrap))

    /** Ensures that if `test` pass else returns provided `V[E]` */
    def ensure[F[_], V[_], E](v: => V[E])(test: => Boolean)(implicit
        F: Effect[F],
        V: Validated[V],
    ): Rule[F, V, E] =
      pure(if (test) V.valid else v)

    /** Ensures that if `test` pass else returns provided `V[E]` */
    def ensureF[F[_], V[_], E](v: => V[E])(test: => F[Boolean])(implicit
        F: Effect[F],
        V: Validated[V],
    ): Rule[F, V, E] =
      effect(F.map(F.defer(test))(if (_) V.valid else v))

    /** Asserts that if `test` pass else returns provided `E` */
    def assert[F[_], V[_], E](e: => E)(test: => Boolean)(implicit
        F: Effect[F],
        V: Validated[V],
    ): Rule[F, V, E] = ensure(V.invalid(e))(test)

    /** Asserts that if `test` pass else returns provided `E` */
    def assertF[F[_], V[_], E](e: => E)(test: => F[Boolean])(implicit
        F: Effect[F],
        V: Validated[V],
    ): Rule[F, V, E] = ensureF(V.invalid(e))(test)

    /** Combines all rules using AND */
    def andAll[F[_]: Effect, V[_]: Validated, E](rules: List[Rule[F, V, E]]): Rule[F, V, E] =
      FoldUtil.fold[Rule[F, V, E]](rules, valid, and)

    /** Combines all rules using OR */
    def orAll[F[_]: Effect, V[_]: Validated, E](rules: List[Rule[F, V, E]]): Rule[F, V, E] =
      FoldUtil.fold[Rule[F, V, E]](rules, valid, or)

    /** Modifies `rule` Validated value using `f` */
    def modify[F[_], V[_], E](rule: Rule[F, V, E])(f: V[E] => V[E])(implicit F: Effect[F]) =
      Rule(F.map(rule.effect)(f))

    /** Modifies `rule` Validated value using `f` */
    def modifyM[F[_], V[_], E](rule: Rule[F, V, E])(f: V[E] => Rule[F, V, E])(implicit F: Effect[F]) =
      Rule(F.flatMap(rule.effect)(f.andThen(_.unwrap)))

    implicit final class RuleOps[F[_], V[_], E](private val rule: Rule[F, V, E]) extends AnyVal {

      /** Unwraps `rule` to its actual type */
      def unwrap: F[V[E]] = Rule.unwrap(rule)

      /** Alias for [[unwrap]] */
      def effect: F[V[E]] = Rule.unwrap(rule)

      /** Combines `rule` and result of running `f` on `rule` using [[Rule.and]]. This is tricky syntax for usage with
        * for-comprehension. Check example in [[flatMap]]
        */
      def map(f: V[E] => V[E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
        Rule.and(rule, Rule.modify(rule)(f))

      /** Combines `rule` and result of running `f` on `rule` using [[Rule.and]]. This is tricky syntax for usage with
        * for-comprehension yield should always return valid Validated if you want this to work correctly.
        *
        * {{{
        * scala> import jap.fields._
        * scala> import jap.fields.DefaultAccumulateVM._
        * scala> val intF = Field(4)
        * val intF: jap.fields.Field[Int] = root:4
        * scala> for {
        *      |  _ <- intF > 4
        *      |  _ <- intF < 4
        *      |  _ <- intF !== 4
        *      | } yield V.valid
        * val res0:
        *   jap.fields.Rule[[A] =>> A, jap.fields.typeclass.Validated.Accumulate,
        *     jap.fields.ValidationError
        *   ] = Invalid(List(root -> should be greater than 4, root -> should be less than 4, root -> should not be equal to 4))
        * }}}
        */
      def flatMap(f: V[E] => Rule[F, V, E])(implicit F: Effect[F], V: Validated[V]): Rule[F, V, E] =
        Rule.and(rule, Rule.modifyM(rule)(f))
    }
  }
}
