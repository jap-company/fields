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

import PolicyKSyntax.*

object LensPolicyK
class LensPolicyK[P, F[_], V[_], E](val builder: FieldLens[P, P] => PolicyK[P, F, V, E]) {
  lazy val root: PolicyK[P, F, V, E]  = builder(FieldLens[P])
  def apply(value: P): RuleK[F, V, E] = root(value)

  def apply[R](lens: FieldLens[R, P])(implicit R: RuleOps[F, V, E]): PolicyK[R, F, V, E] = {
    val validation = builder(FieldLens[P](lens.path))
    v => lens.rule(v)(validation)
  }

  def mapK[FF[_]](f: F[V[E]] => FF[V[E]]): LensPolicyK[P, FF, V, E] =
    new LensPolicyK(builder.andThen(_.mapK(f)))

  def and(other: LensPolicyK[P, F, V, E])(implicit R: RuleOps[F, V, E]): LensPolicyK[P, F, V, E] = {
    new LensPolicyK(l => builder(l).and(other.builder(l)))
  }
}
