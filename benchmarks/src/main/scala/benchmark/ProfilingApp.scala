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

import scala.concurrent.duration.DurationInt

object ProfilingApp {
  private val profilingDuration = 10.seconds.toMillis
  private val start             = System.currentTimeMillis()

  def main(args: Array[String]): Unit = {
    val benchmarks = new FieldsLensSyncAccumulateBenchmark()
    benchmarks.dataMode = "all_valid"
    benchmarks.setup()

    while (System.currentTimeMillis() - start < profilingDuration) {
      benchmarks.fieldsLensSyncAccumulate()
    }
  }
}
