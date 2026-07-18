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

import fields.value.syntax.PolicySyntax.*
import org.openjdk.jmh.annotations.*

import scala.annotation.nowarn

class FieldsLensSyncAccumulateBenchmark extends BenchmarkBase {
  @Benchmark
  @nowarn
  def fieldsLensSyncAccumulate(): Unit = FieldsPolicies.lensPolicies.syncAccumulate(data)
}

class FieldsLensSyncFailFastBenchmark extends BenchmarkBase {
  @Benchmark
  @nowarn
  def fieldsLensSyncFailFast(): Unit = FieldsPolicies.lensPolicies.syncFailFast(data)
}

class FieldsLensSyncChainBenchmark extends BenchmarkBase {
  @Benchmark
  @nowarn
  def fieldsLensSyncChain(): Unit = FieldsPolicies.lensPolicies.syncChain(data)
}

class FieldsLensSyncValidatedNecBenchmark extends BenchmarkBase {
  @Benchmark
  @nowarn
  def fieldsLensSyncValidatedNec(): Unit = FieldsPolicies.lensPolicies.syncValidatedNec(data)
}

class FieldsPolicySyncAccumulateBenchmark extends BenchmarkBase {
  @Benchmark
  @nowarn
  def fieldsValuePolicySyncAccumulate(): Unit = FieldsPolicies.policy.syncAccumulate.validate(data)
}

class FieldsPolicySyncFailFastBenchmark extends BenchmarkBase {
  @Benchmark
  @nowarn
  def fieldsValuePolicySyncFailFast(): Unit = FieldsPolicies.policy.syncFailFast.validate(data)
}

class FieldsPolicySyncChainBenchmark extends BenchmarkBase {
  @Benchmark
  @nowarn
  def fieldsValuePolicySyncChain(): Unit = FieldsPolicies.policy.syncChain.validate(data)
}

class FieldsPolicySyncValidatedNecBenchmark extends BenchmarkBase {
  @Benchmark
  @nowarn
  def fieldsValuePolicySyncValidatedNec(): Unit = FieldsPolicies.policy.syncValidatedNec.validate(data)
}

class DupinSyncBenchmark extends BenchmarkBase {
  @Benchmark
  @nowarn
  def dupinValidatorSyncValidatedNec(): Unit = DupinPolicy.validateSync(data)
}
