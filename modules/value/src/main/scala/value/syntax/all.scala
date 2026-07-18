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
package value
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
    with CommonSyntax
    with FailFieldSyntax
    with PolicySubSyntax

trait AllDsl[F[_], V[_], E]
    extends GenericDsl[F, V, E]
    with BooleanDsl[F, V, E]
    with OrderingDsl[F, V, E]
    with OptionDsl[F, V, E]
    with StringDsl[F, V, E]
    with MapDsl[F, V, E]
    with IterableDsl[F, V, E]
    with FailFieldDsl[F, V, E]
    with PolicySubDsl[F, V, E]
    with CommonSyntax

object CommonSyntax extends CommonSyntax
trait CommonSyntax  extends FieldSyntax with PolicySyntax with RuleSyntax with ValidatedSyntax with PolicyKSyntax {
  type Field[+P] = value.Field[P]
  val Field: value.Field.type = value.Field
  type FieldPath = fields.FieldPath
  val FieldPath: fields.FieldPath.type = fields.FieldPath
}
