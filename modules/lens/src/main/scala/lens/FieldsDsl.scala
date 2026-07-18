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

import error.ValidationMessage
import lens.syntax.CoreDsl
import typeclass.{Effect, Validated}

import scala.annotation.implicitNotFound

/** Base Fields Lens DSL class to be used to create DSL for targeted F[_], V[_], E */
@implicitNotFound("ValidationDsl[${F}, ${V}, ${E}] not found")
class FieldsDsl[F[_], V[_], E](implicit val F: Effect[F], val V: Validated[V]) extends CoreDsl[F, V, E] {
  implicit val Rule: RuleOps[F, V, E] = RuleOps.instance

  /** [[fields.RuleK]] alias. Infers F, V, E */
  final type Rule = RuleK[F, V, E]

  /** [[fields.PolicyK]] alias. Infers F, V, E */
  final type Policy[-A] = PolicyK[A, F, V, E]
  object Policy {

    /** [[fields.PolicyK.apply]] alias. Infers F, V, E */
    def apply[A]: Policy[A] = PolicyK[A, F, V, E]
  }
}

/** Accumulating [[fields.lens.FieldsDsl]] */
abstract class FailFastDsl[F[_]: Effect, E] extends FieldsDsl[F, FailFast, E]

/** Accumulating [[fields.lens.FieldsDsl]] */
abstract class AccumulateDsl[F[_]: Effect, E] extends FieldsDsl[F, Accumulate, E]

object FieldsDsl {

  /** Default dsl which is sync, accumulating and uses ValidationMessage as error */
  object default extends accumulate

  /** Default Accumulating dsl which is sync, accumulating and uses ValidationMessage as error */
  trait accumulate  extends FieldsDsl[Effect.Sync, Accumulate, ValidationMessage] with ValidationMessage.failWith.Mixin
  object accumulate extends accumulate

  /** Default FailFast dsl which is sync, accumulating and uses ValidationMessage as error */
  trait failFast  extends FieldsDsl[Effect.Sync, FailFast, ValidationMessage] with ValidationMessage.failWith.Mixin
  object failFast extends failFast

  /** Base FailFast DSL [[fields.lens.FieldsDsl]] */
  abstract class BaseFailFast[F[_]: Effect, E] extends FieldsDsl[F, FailFast, E]

  /** Base Accumulating DSL [[fields.lens.FieldsDsl]] */
  abstract class BaseAccumulate[F[_]: Effect, E] extends FieldsDsl[F, Accumulate, E]
}
