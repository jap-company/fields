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

package object fields {
  type Endo[X]        = X => X
  type FailFast[+E]   = Option[E]
  type Accumulate[+E] = List[E]

  /** Rule is tagged type alias for F[V[E]] If used this way we do not add any allocations while having separate syntax
    * for Rule. Also we get ability to convert back and forth.
    */
  type RuleK[+F[_], +V[_], +E] <: RuleK.Type[F, V, E]

  type PathedFn[-A, +B] = (FieldPath, A) => B

  type PolicyK[-A, F[_], V[_], E]          = A => RuleK[F, V, E]
  type PolicyK2[-A, -B, F[_], V[_], E]     = (A, B) => RuleK[F, V, E]
  type PolicyK3[-A, -B, -C, F[_], V[_], E] = (A, B, C) => RuleK[F, V, E]
}
