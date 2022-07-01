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
package zioenv

import jap.fields.ZioInterop._
import jap.fields._
import jap.fields.data.Accumulate
import jap.fields.error.ValidationError
import jap.fields.fail.CanFailWithValidationError
import jap.fields.syntax._
import jap.fields.typeclass.Effect
import zio._

trait HeartbeatApi {
  def isHeartbeatOk(heartbeat: Int): Task[Boolean]
}

trait BloodPressureApi {
  def isBloodPressureOk(bloodPressure: Int): Task[Boolean]
}

case class HealthReport(
    heartbeat: Int,
    bloodPressure: Int,
)

object Validation extends all with ZioSyntaxAll with CanFailWithValidationError {
  type ZRule[R]      = Rule[RIO[R, *], Accumulate, ValidationError]
  type ZPolicy[R, P] = ValidationPolicy[P, RIO[R, *], Accumulate, ValidationError]
  object ZPolicy {
    def builder[R, P]: ValidationPolicyBuilder[P, RIO[R, *], Accumulate, ValidationError] = ValidationPolicy.builder
  }
}
import Validation._

object HealthReport {
  def validateHeartbeat(heartbeat: Field[Int]): ZRule[Has[HeartbeatApi]] =
    heartbeat.ensureF(
      h => ZIO.serviceWith[HeartbeatApi](_.isHeartbeatOk(h)),
      _.failMessage("Check your heartbeat please"),
    )

  def validateBloodPressure(bloodPressure: Field[Int]): ZRule[Has[BloodPressureApi]] =
    bloodPressure.ensureF(
      h => ZIO.serviceWith[BloodPressureApi](_.isBloodPressureOk(h)),
      _.failMessage("Check your bloodpressure please"),
    )

  implicit val policy: ZPolicy[Has[BloodPressureApi] with Has[HeartbeatApi], HealthReport] =
    ZPolicy
      .builder[Has[BloodPressureApi] with Has[HeartbeatApi], HealthReport]
      .subRule(_.heartbeat)(validateHeartbeat)
      .subRule(_.bloodPressure)(validateBloodPressure)
      .build
}

object ZioEnvExample extends zio.App {
  showBuildInfo()

  val HeartbeatApiLayer     = ZLayer.succeed {
    new HeartbeatApi {
      def isHeartbeatOk(heartbeat: Int): Task[Boolean] = Task(false)
    }
  }
  val BloodPressureApiLayer = ZLayer.succeed {
    new BloodPressureApi {
      def isBloodPressureOk(bloodPressure: Int): Task[Boolean] = Task(false)
    }
  }

  val healthReport                                  = HealthReport(1, 2)
  val program                                       = showErrors("HEALTH")(Field.from(healthReport).validate)
  def run(args: List[String]): URIO[ZEnv, ExitCode] =
    program
      .provideLayer(HeartbeatApiLayer ++ BloodPressureApiLayer)
      .exitCode
}
