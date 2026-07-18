package benchmark

import org.openjdk.jmh.annotations.*

import scala.annotation.nowarn

class OctopusSyncBenchmark extends BenchmarkBase {
  @Benchmark
  @nowarn
  def octopusValidatorSyncAccumulate(): Unit = OctopusPolicy.validateSync(data)
}