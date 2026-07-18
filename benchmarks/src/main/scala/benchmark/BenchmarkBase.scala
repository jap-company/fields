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

import org.openjdk.jmh.annotations.*

import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(
  value = 1,
  jvmArgs = Array(
    "-Dcats.effect.tracing.mode=none",
    "-server",
    "-noclassgc",
    "-Xms8g",
    "-Xmx8g",
    "-XX:NewSize=7g",
    "-XX:MaxNewSize=7g",
    "-XX:InitialCodeCacheSize=512m",
    "-XX:ReservedCodeCacheSize=512m",
    "-XX:NonNMethodCodeHeapSize=32m",
    "-XX:NonProfiledCodeHeapSize=240m",
    "-XX:ProfiledCodeHeapSize=240m",
    "-XX:TLABSize=16m",
    "-XX:-ResizeTLAB",
    "-XX:+UseParallelGC",
    "-XX:-UseAdaptiveSizePolicy",
    "-XX:MaxInlineLevel=20",
    "-XX:InlineSmallCode=2500",  // Use defaults from Open JDK 17+
    "-XX:+AlwaysPreTouch",
//    "-XX:+UseTransparentHugePages", // Only on Linux
    "-XX:-UseDynamicNumberOfGCThreads",
    "-XX:+UseNUMA",
    "-XX:-UseAdaptiveNUMAChunkSizing",
    "-XX:+PerfDisableSharedMem", // See https://github.com/Simonis/mmap-pause#readme
    "-XX:-UseDynamicNumberOfCompilerThreads",
    "-XX:-UsePerfData",
    "-XX:+UnlockExperimentalVMOptions",
    "-XX:+TrustFinalNonStaticFields",
  ),
)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
abstract class BenchmarkBase {
  @Param(Array("all_valid", "all_invalid", "random"))
  var dataMode: String    = "all_valid"
  var data: BenchmarkData = _

  @Setup
  def setup(): Unit = {
    data = dataMode match {
      case "all_valid"   => BenchmarkData.allValid
      case "all_invalid" => BenchmarkData.allInvalid
      case "random"      => BenchmarkData.random
      case value         => throw new IllegalArgumentException(s"Unsupported data mode: $value")
    }
  }
}
