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

import jap.fields._
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

/** In this example we focus on using ZIO environment for validation, so we define custom syntax module where Rule and
  * Policy can specify R environment
  */
object Validation      {
  import jap.fields.data.Accumulate
  import jap.fields.error.ValidationError
  import jap.fields.fail.CanFailWithValidationError
  import jap.fields.ZIOInterop._
  object all extends ZValidationModule[Accumulate, ValidationError] with CanFailWithValidationError
}
import Validation.all._

object HealthReport {

  /** Notice in env we say only what is needed for this validation */
  def validateHeartbeat(heartbeat: Field[Int]): RIORule[Has[HeartbeatApi]] =
    heartbeat.ensureF(
      h => ZIO.serviceWith[HeartbeatApi](_.isHeartbeatOk(h)),
      _.failMessage("Check your heartbeat please"),
    )

  /** Notice in env we say only what is needed for this validation */
  def validateBloodPressure(bloodPressure: Field[Int]): RIORule[Has[BloodPressureApi]] =
    bloodPressure.ensureF(
      h => ZIO.serviceWith[BloodPressureApi](_.isBloodPressureOk(h)),
      _.failMessage("Check your bloodpressure please"),
    )

  implicit val policy: RIOPolicy[Has[BloodPressureApi] with Has[HeartbeatApi], HealthReport] =
    RIOPolicy
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
