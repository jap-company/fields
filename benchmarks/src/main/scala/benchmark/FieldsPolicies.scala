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

import cats.data.Chain
import fields.*
import fields.CatsInterop.*
import fields.fail.FailWith
import fields.typeclass.Effect.Sync

object FieldsPolicies {
  type Error = (FieldPath, String)
  implicit lazy val FW: FailWith.Base[(FieldPath, String)] = FailWith.pathedError(FailWith.string.errorMessage)

  object lensPolicies {
    lazy val syncAccumulate   = makeLens(new lens.FieldsDsl.BaseAccumulate[Sync, Error] {})
    lazy val syncFailFast     = makeLens(new lens.FieldsDsl.BaseFailFast[Sync, Error] {})
    lazy val syncChain        = makeLens(new lens.FieldsDsl[Sync, Chain, Error] {})
    lazy val syncValidatedNec = makeLens(new lens.FieldsDsl[Sync, ValidatedAccumulateNec, Error] {})
  }

  object policy {
    lazy val syncAccumulate   = makePolicy(new value.FieldsDsl.BaseAccumulate[Sync, Error] {})
    lazy val syncFailFast     = makePolicy(new value.FieldsDsl.BaseFailFast[Sync, Error] {})
    lazy val syncChain        = makePolicy(new value.FieldsDsl[Sync, Chain, Error] {})
    lazy val syncValidatedNec = makePolicy(new value.FieldsDsl[Sync, ValidatedAccumulateNec, Error] {})
  }

  private def makeLens[F[_], V[_]](dsl: lens.FieldsDsl[F, V, Error]): dsl.Policy[BenchmarkData] = {
    import dsl.*

    val deepBenchmarkDataPolicy: LensPolicy[DeepBenchmarkData] =
      LensPolicy[DeepBenchmarkData] { implicit base =>
        _.subRule(_.boolean)(_.isTrue)
          .subRule(_.int)(_.isBetween(1, 10))
          .subRule(_.long)(_ > 0L)
          .subRule(_.byte)(_ >= 0.toByte)
          .subRule(_.three)(_ < 0d)
          .subRule(_.float)(_ <= -1f)
          .subRule(_.bigDecimal)(_ === BigDecimal(1))
          .subRule(_.bigInt)(_ !== BigInt(0))
          .subRule(_.string)(_.minSize(4), _.maxSize(10), _.nonEmpty)
          .subRule(_.listInt)(_.assert(_.forall(_ >= 0), failMessage("each element must be gte 0")))
          .subRule(_.optionInt)(_.assert(_.forall(_ >= 0), failMessage("must be gte 0")))
      }

    Policy[BenchmarkData].subRule(_.nested.deep)(deepBenchmarkDataPolicy.apply)
  }

  private def makePolicy[F[_], V[_]](dsl: value.FieldsDsl[F, V, Error]): dsl.Policy[BenchmarkData] = {
    import dsl.*

    implicit val deepBenchmarkDataPolicy: Policy[DeepBenchmarkData] =
      Policy[DeepBenchmarkData]
        .subRule(_.boolean)(_.isTrue)
        .subRule(_.int)(_.isBetween(1, 10))
        .subRule(_.long)(_ > 0L)
        .subRule(_.byte)(_ >= 0.toByte)
        .subRule(_.three)(_ < 0d)
        .subRule(_.float)(_ <= -1f)
        .subRule(_.bigDecimal)(_ === BigDecimal(1))
        .subRule(_.bigInt)(_ !== BigInt(0))
        .subRule(_.string)(_.minSize(4), _.maxSize(10), _.nonEmpty)
        .subRule(_.listInt)(_.assert(_.forall(_ >= 0), _.failMessage("each element must be gte 0")))
        .subRule(_.optionInt)(_.assert(_.forall(_ >= 0), _.failMessage("must be gte 0")))

    Policy[BenchmarkData].subRule(_.nested.deep)(_.validate)
  }

}
