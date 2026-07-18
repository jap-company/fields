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
package typeclass

trait PrependPath[E] {
  def prependPath(path: FieldPath, e: E): E
  def prependPath(part: FieldPart, e: E): E = prependPath(FieldPath(part), e)
}

object PrependPath {
  implicit val stringPrependPath: PrependPath[String]                 = _.full + _
  implicit def pathedErrorPrependPath[E]: PrependPath[(FieldPath, E)] = (path, e) => (path ++ e._1, e._2)
}
