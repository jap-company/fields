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
package examples
package medium

import jap.fields.ZIOInterop._
import jap.fields._
import jap.fields.fail._
import zio.Task

object MediumCustomValidation
    extends ValidationModule[Task, MediumErrors, MediumErrorCode]
    with CanFailMediumErrorCode {
  def failCode[P](code: Int)(field: Field[P]) =
    V.invalid(MediumErrorCode(field.path, code))

  implicit object FailPostDateWithErrorCode extends FailWithCompare[MediumErrorCode, PostDate] {
    override def compare[P](operation: CompareOperation, compared: String)(field: Field[P]): MediumErrorCode =
      MediumErrorCode(field.path, 123)
  }
}
