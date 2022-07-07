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
package error

/** Carries `error` with `path` where it occured. Using this can be useful when your `error` type does not support
  * carrying `path` where it occured but you actually want to know it.
  */
case class FieldError[E](
    path: FieldPath,
    error: E,
) {
  override def toString: String = s"${path.full} -> $error"
}
