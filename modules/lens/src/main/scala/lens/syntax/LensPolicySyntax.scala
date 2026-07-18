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

object LensPolicySyntax extends LensPolicySyntax
trait LensPolicySyntax {
  implicit def toLensPolicyCompanionOps[F[_], V[_], E](p: LensPolicyK.type): LensPolicyCompanionOps[F, V, E] =
    new LensPolicyCompanionOps(p)
}

trait LensPolicyDsl[F[_], V[_], E] { self: CoreDsl[F, V, E] =>
  implicit def toLensPolicyKCompanionOps(p: LensPolicyK.type): LensPolicyCompanionOps[F, V, E] =
    new LensPolicyCompanionOps(p)

  implicit def toLensPolicyCompanionOps(p: LensPolicy.type): LensPolicyCompanionOps[F, V, E] = {
    val _ = p
    new LensPolicyCompanionOps(LensPolicyK)
  }
}

final class LensPolicyCompanionOps[F[_], V[_], E](private val p: LensPolicyK.type) extends AnyVal {
  def apply[P](builder: FieldLens[P, P] => Endo[PolicyK[P, F, V, E]])(implicit
      R: RuleOps[F, V, E]
  ): LensPolicyK[P, F, V, E] =
    new LensPolicyK(builder(_)(PolicyK.valid))

  def none[P](implicit R: RuleOps[F, V, E]): LensPolicyK[P, F, V, E] =
    new LensPolicyK[P, F, V, E](_ => PolicyK.valid)

  def root[P](builder: FieldLens[P, P] => PolicyK[P, F, V, E])(implicit
      R: RuleOps[F, V, E]
  ): LensPolicyK[P, F, V, E] = {
    val _ = R
    new LensPolicyK(builder)
  }

  def of[P](builder: FieldLens[P, P] => Endo[PolicyK[P, F, V, E]])(implicit
      R: RuleOps[F, V, E]
  ): LensPolicyK[P, F, V, E] =
    new LensPolicyK[P, F, V, E](lens => builder(lens)(PolicyK.valid))
}
