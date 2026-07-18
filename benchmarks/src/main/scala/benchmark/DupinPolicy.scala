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

package benchmark

import cats.data.ValidatedNec
import cats.{Applicative, Id}
import dupin.*

object DupinPolicy {
  type Error = (String, String)

  def validator[F[_]: Applicative]: Validator[F, Error, BenchmarkData] = {
    type V[A] = Validator[F, Error, A]
    val V = Validator[F, Error]

    implicit val deepDataValidator: V[DeepBenchmarkData] = V
      .success[DeepBenchmarkData]
      .combinePR(_.boolean)(identity, c => c.path.toString -> "must be true")
      .combinePR(_.int)(_ >= 1, c => c.path.toString -> "must be greater than 0")
      .combinePR(_.int)(_ <= 10, c => c.path.toString -> "must be less then equal than 10")
      .combinePR(_.long)(_ > 0L, c => c.path.toString -> "must be greater than 0")
      .combinePR(_.byte)(_ >= 0.toByte, c => c.path.toString -> "must be greater than or equal to 0")
      .combinePR(_.three)(_ < 0d, c => c.path.toString -> "must be less than 0")
      .combinePR(_.float)(_ <= -1f, c => c.path.toString -> "must be less than or equal to -1")
      .combinePR(_.bigDecimal)(_ == BigDecimal(1), c => c.path.toString -> "must be equal to BigDecimal(1)")
      .combinePR(_.bigInt)(_ != BigInt(0), c => c.path.toString -> "must not be equal to BigInt(0)")
      .combinePR(_.string)(_.nonEmpty, c => c.path.toString -> "must not be empty")
      .combinePR(_.string)(_.length >= 4, c => c.path.toString -> "must have a size bigger than 4")
      .combinePR(_.string)(_.length <= 10, c => c.path.toString -> "must have size less than 10")
      .combinePR(_.listInt)(_.forall(_ >= 0), c => c.path.toString -> "each element must be gte 0")
      .combinePR(_.optionInt)(_.forall(_ >= 0), c => c.path.toString -> "must be gte 0")

    implicit val nestedBenchmarkDataValidator: V[NestedBenchmarkData] =
      V.success[NestedBenchmarkData].combinePI(_.deep)

    V.success[BenchmarkData].combinePI(_.nested)
  }

  private lazy val syncValidator = validator[Id]

  def validateSync(data: BenchmarkData): ValidatedNec[(String, String), BenchmarkData] = syncValidator.validate(data)
}
