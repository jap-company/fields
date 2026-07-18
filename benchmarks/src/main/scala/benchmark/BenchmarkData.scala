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

import java.security.SecureRandom
import scala.util.Random

case class BenchmarkData(nested: NestedBenchmarkData)

object BenchmarkData {
  val rng = new SecureRandom(Array(1, 2, 3, 4))

  // Generates random BenchmarkData
  def random: BenchmarkData =
    BenchmarkData(
      nested = NestedBenchmarkData(
        DeepBenchmarkData(
          boolean = rng.nextBoolean(),
          int = rng.nextInt(),
          long = rng.nextLong(),
          byte = rng.nextInt().toByte,
          three = rng.nextDouble(),
          float = rng.nextFloat(),
          bigDecimal = BigDecimal(rng.nextDouble()),
          bigInt = BigInt(rng.nextInt()),
          string = Random.alphanumeric.take(rng.nextInt(20)).mkString,
          listInt = (0 to rng.nextInt(10)).map(_ => rng.nextInt()).toList,
          optionInt = if (rng.nextBoolean()) Option(rng.nextInt()) else None,
        )
      )
    )

  val allValid =
    BenchmarkData(
      nested = NestedBenchmarkData(
        DeepBenchmarkData(
          boolean = true,
          int = 5,
          long = 2,
          byte = 2,
          three = -2,
          float = -1,
          bigDecimal = 1,
          bigInt = 2,
          string = "abcde",
          listInt = (0 to 10).toList,
          optionInt = Some(0),
        )
      )
    )

  val allInvalid =
    BenchmarkData(
      nested = NestedBenchmarkData(
        DeepBenchmarkData(
          boolean = false,
          int = 0,
          long = -1,
          byte = -1,
          three = 5,
          float = 5,
          bigDecimal = 0,
          bigInt = 0,
          string = "",
          listInt = (0 to 10).map(_ * -1).toList,
          optionInt = Some(-1),
        )
      )
    )
}

case class NestedBenchmarkData(deep: DeepBenchmarkData)

case class DeepBenchmarkData(
    boolean: Boolean,
    int: Int,
    long: Long,
    byte: Byte,
    three: Double,
    float: Float,
    bigDecimal: BigDecimal,
    bigInt: BigInt,
    string: String,
    listInt: List[Int],
    optionInt: Option[Int],
)
