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
package typeclass

import jap.fields.error._

/** Type class that helps extracting FieldPath from error */
trait HasFieldPath[E] {
  def fieldPath(e: E): FieldPath
}

object HasFieldPath {
  def apply[E](implicit instance: HasFieldPath[E]) = instance

  implicit object ValidationMessageHasFieldPath extends HasFieldPath[ValidationMessage] {
    def fieldPath(e: ValidationMessage): FieldPath = e.path
  }

  implicit object ValidationErrorHasFieldPath extends HasFieldPath[ValidationError] {
    def fieldPath(e: ValidationError): FieldPath = e.path
  }

  implicit def toFieldErrorHasFieldPath[T]: FieldErrorHasFieldPath[T] = new FieldErrorHasFieldPath[T]

  class FieldErrorHasFieldPath[T] extends HasFieldPath[FieldError[T]] {
    def fieldPath(e: FieldError[T]): FieldPath = e.path
  }
}
