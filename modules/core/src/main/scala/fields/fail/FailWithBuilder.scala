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

package jap.fields
package fail

import jap.fields.error.{ValidationMessages => M, ValidationTypes => T}

trait FailWithBuilder[E, +P] extends FailWith[E, P] {
  def build[PP >: P](field: Field[PP], errorType: => String, errorMessage: => Option[String]): E
  def invalid[PP >: P](field: Field[PP]): E            = build[PP](field, T.Invalid, Some(M.Invalid))
  def empty[PP >: P](field: Field[PP]): E              = build[PP](field, T.Empty, Some(M.Empty))
  def nonEmpty[PP >: P](field: Field[PP]): E           = build[PP](field, T.NonEmpty, Some(M.NonEmpty))
  def minSize[PP >: P](size: Int)(field: Field[PP]): E = build[PP](field, T.MinSize, Some(M.MinSize(size)))
  def maxSize[PP >: P](size: Int)(field: Field[PP]): E = build[PP](field, T.MaxSize, Some(M.MaxSize(size)))
  def message[PP >: P](error: String, message: Option[String])(field: Field[PP]): E = build[PP](field, error, message)
  def oneOf[PP >: P](variants: Seq[PP])(field: Field[PP]): E                        =
    build[PP](field, T.OneOf, Some(M.OneOf(variants.map(_.toString))))
  def compare[PP >: P](operation: CompareOperation, compared: String)(field: Field[PP]): E =
    build[PP](field, operation.constraint, Some(operation.message(compared)))
}
