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

object FieldSyntax extends FieldSyntax
trait FieldSyntax {
  implicit def toFieldSubOps[P, S](field: FieldLens[P, S]): FieldSubOps[P, S] = new FieldSubOps(field)
}

final class FieldSubOps[P, S](private val field: FieldLens[P, S]) extends AnyVal {

  /** Returns subfield using `selector` function to extract value and as path
    *
    * Example
    * {{{
    * scala> val request = Request(User("ann"))
    * scala> val field = Field.from(request)
    * val field: fields.Field[Request] = request:Request(User(ann))
    * scala> field.sub(_.user.name)
    * val res1: fields.Field[String] = request.user.name:ann
    * }}}
    */
  def sub[SS](selector: S => SS): FieldLens[P, SS] = macro FieldMacro.subMacro[P, S, SS]
}
