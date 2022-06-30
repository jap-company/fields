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
package github

import jap.fields._

import zio._
import zio.console._

object Validation {
  import jap.fields.error._
  import jap.fields.fail._
  import jap.fields.ZioInterop._
  object all extends AccumulateVM[Task, ValidationError] with CanFailWithValidationError
}
import Validation.all.*

case class AddStarCmd(
    organization: String,
    project: String,
    user: String,
)

object AddStarCmd {

  /** Default policy that does not have any dependencies */
  implicit val policy: Policy[AddStarCmd] =
    Policy
      .builder[AddStarCmd]
      .subRule(_.organization)(_.minSize(2))
      .subRule(_.project)(_.minSize(2))
      .subRule(_.user)(_.minSize(2))
      .build

  /** Extracted for convenience */
  def checkNotStarredRule(userF: Field[String])(project: Project) =
    userF.ensure(
      !project.stargazers.contains(_),
      _.failMessage("user-already-starred-project"),
    )

  def policy(api: GithubApi): Policy[AddStarCmd] = { cmdF =>
    // Access Field`s
    val organizationF = cmdF.sub(_.organization)
    val projectF      = cmdF.sub(_.project)
    val userF         = cmdF.sub(_.user)

    // Declare Rule`s
    val organizationRule =
      organizationF.ensureF(api.findOrganization(_).map(_.isDefined), _.failMessage("organization-does-not-exist"))

    val projectDontExist = MRule.pure(V.traverse(organizationF, projectF)(_.failMessage("project-does-not-exist")))

    /** We use Rule.apply cause we sure this is lazy */
    val projectRule = Rule {
      api
        .findProject(organizationF.value, projectF.value)
        .flatMap(_.fold(projectDontExist)(checkNotStarredRule(userF)).effect)
    }

    cmdF.validate &&                        // Call validate to use base validations
    organizationRule.whenValid(projectRule) // Make sure we only apply projectRule if organization exists
  }
}

case class Organization(name: String)

case class Project(
    organization: String,
    name: String,
    stargazers: List[String],
)

trait GithubApi {
  def findOrganization(organization: String): Task[Option[Organization]]
  def findProject(organization: String, name: String): Task[Option[Project]]
}

object GithubExample extends zio.App {
  showBuildInfo()

  val api: GithubApi = new GithubApi {
    val organizations = Map("jap-company" -> Organization("jap-company"))
    val projects      = Map(("jap-company", "fields") -> Project("jap-company", "fields", List("0lejk4")))
    def findOrganization(organization: String): Task[Option[Organization]] =
      Task(organizations.get(organization))

    def findProject(organization: String, name: String): Task[Option[Project]] =
      Task(projects.get((organization, name)))
  }

  implicit val policy: Policy[AddStarCmd] = AddStarCmd.policy(api)

  final def program = {
    val noOrgCmd          = AddStarCmd("a", "b", "c")
    val noProjectCmd      = AddStarCmd("jap-company", "spring4s", "0lejk4")
    val alreadyStarredCmd = AddStarCmd("jap-company", "fields", "0lejk4")

    for {
      _ <- showErrors("NO-ORG-CMD-ERRORS")(Field.from(noOrgCmd).validate)
      _ <- showErrors("NO-PROJECT-CMD-ERRORS")(Field.from(noProjectCmd).validate)
      _ <- showErrors("ALREADY-STARRED-CMD-ERRORS")(Field.from(alreadyStarredCmd).validate)
    } yield ()
  }

  def run(args: List[String]): URIO[ZEnv, ExitCode] = program.exitCode
}
