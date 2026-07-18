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

import fields.syntax.{RuleSyntax, ValidatedSyntax}

object all extends all
trait all
    extends GenericSyntax
    with BooleanSyntax
    with OrderingSyntax
    with OptionSyntax
    with StringSyntax
    with MapSyntax
    with IterableSyntax
    with PolicySubSyntax
    with LensPolicySyntax
    with CommonSyntax

trait CoreDsl[F[_], V[_], E]
    extends GenericDsl[F, V, E]
    with BooleanDsl[F, V, E]
    with OrderingDsl[F, V, E]
    with OptionDsl[F, V, E]
    with StringDsl[F, V, E]
    with MapDsl[F, V, E]
    with IterableDsl[F, V, E]
    with PolicySubDsl[F, V, E]
    with LensPolicyDsl[F, V, E]
    with CommonSyntax {
  final type LensPolicy[P] = LensPolicyK[P, F, V, E]
  object LensPolicy
}

object CommonSyntax extends CommonSyntax
trait CommonSyntax  extends ValidatedSyntax with RuleSyntax with FailFieldSyntax with PolicyKSyntax with FieldSyntax {
  type FieldLens[P, +V] = lens.FieldLens[P, V]
  val FieldLens: lens.FieldLens.type = lens.FieldLens
  type FieldPath = fields.FieldPath
  val FieldPath: fields.FieldPath.type = fields.FieldPath
}
