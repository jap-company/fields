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

package examples
package medium

import zio.Task
import fields.*
import fields.fail.*
import fields.value.*
import FieldsInteropZIO.*

object MediumValidation
    extends FieldsDsl[Task, Accumulate, MediumErrorCode]
    with FieldsSyntaxZIO
    with CanFailMediumErrorCode {
  implicit class MediumFailFieldOps[P](private val field: Field[P]) extends AnyVal {
    def failCode(code: Int): MediumErrorCode = MediumErrorCode(field.path, code)
  }

  implicit object FailPostDateWithErrorCode extends FailWithCompare[MediumErrorCode, PostDate] {
    override def compare[P](operation: CompareOperation, compared: String)(path: FieldPath, value: P): MediumErrorCode =
      MediumErrorCode(path, 123)
  }
}
