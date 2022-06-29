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

import error.FieldError

/** Wrap existing error into FieldError given [[FailWith]] instance for type `E`
  */
class FailWithFieldError[E, +P](FW: FailWith[E, P]) extends FailWith[FieldError[E], P] {
  def invalid[PP >: P](field: Field[PP]): FieldError[E]            = FieldError(field.path, FW.invalid[PP](field))
  def empty[PP >: P](field: Field[PP]): FieldError[E]              = FieldError(field.path, FW.empty[PP](field))
  def nonEmpty[PP >: P](field: Field[PP]): FieldError[E]           = FieldError(field.path, FW.nonEmpty[PP](field))
  def minSize[PP >: P](size: Int)(field: Field[PP]): FieldError[E] =
    FieldError(field.path, FW.minSize[PP](size)(field))
  def maxSize[PP >: P](size: Int)(field: Field[PP]): FieldError[E] =
    FieldError(field.path, FW.maxSize[PP](size)(field))
  def oneOf[PP >: P](variants: Seq[PP])(field: Field[PP]): FieldError[E]                               =
    FieldError(field.path, FW.oneOf[PP](variants)(field))
  def message[PP >: P](error: String, message: Option[String])(field: Field[PP]): FieldError[E]        =
    FieldError(field.path, FW.message[PP](error, message)(field))
  def compare[PP >: P](operation: CompareOperation, compared: String)(field: Field[PP]): FieldError[E] =
    FieldError(field.path, FW.compare[PP](operation, compared)(field))
}
