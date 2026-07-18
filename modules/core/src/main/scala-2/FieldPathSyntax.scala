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

trait FieldPathSyntax {
  implicit def toFieldFromOps(path: FieldPath.type): FieldPathFromOps = new FieldPathFromOps(path)
}

final class FieldPathFromOps(private val path: FieldPath.type) extends AnyVal {

  /** Returns [[fields.FieldPath]] inferered from field selects
    *
    * Example:
    * {{{
    * scala> val request = Request(User("ann"))
    * val request: Request = Request(User(ann))
    * scala> val path = FieldPath.from(request.user.name)
    * val path: fields.FieldPath = request.user.name
    * }}}
    */
  def from[V](value: V): FieldPath = macro FieldPathMacro.fromMacro[V]

  /** Similar to `Field.from` but drops first selector path
    *
    * Example:
    * {{{
    * scala> val request = Request(User("ann"))
    * val request: Request = Request(User(ann))
    * scala> val path = FieldPath.sub(request.user.name)
    * val path: fields.FieldPath = user.name
    * }}}
    */
  def sub[V](value: V): FieldPath = macro FieldPathMacro.subMacro[V]
}
