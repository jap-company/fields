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
package register

import cats._
import jap.fields._
import zio._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object Validation {
  import jap.fields.typeclass.Effect.future._
  import jap.fields.fail._
  import jap.fields.error._
  // Try changing Module delclaration to see how easy it is to swap Error type
  // object all extends AccumulateVM[Future, ValidationError] with CanFailWithValidationError
  object all extends AccumulateVM[Future, FieldError[String]] with CanFailWithFieldStringValidationMessage
  // object all extends AccumulateVM[Future, ValidationMessage] with CanFailWithValidationMessage
}
import Validation.all._

case class Email(value: String) extends AnyVal
object Email {
  val EmailRegex                     =
    "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$".r
  implicit val policy: Policy[Email] = _.map(_.value).matchesRegex(EmailRegex)
}

case class Username(value: String) extends AnyVal

case class RegisterRequest(
    username: Username,
    email: Email,
    password: String,
    passwordRepeat: String,
    age: Int,
)

object RegisterRequest {
  implicit val policy: Policy[RegisterRequest] = Policy
    .builder[RegisterRequest]
    .fieldRule(_.sub(_.username).map(_.value))(
      _.minSize(1),
      _.maxSize(10),
    )
    .subRule(_.age)(_ >= 18, _ <= 110)
    .subRule(_.email)(_.map(_.value).matchesRegex(Email.EmailRegex))
    .subRule(_.password)(_.nonEmpty, _.minSize(4), _.maxSize(100))
    .subRule(_.password, _.passwordRepeat)(_ equalTo _)
    .build

  def policy(userService: UserService): Policy[RegisterRequest] =
    Policy
      .builder[RegisterRequest]
      // Include Sync validation
      .rule(RegisterRequest.policy.validate)
      // Async validations
      .subRule(_.username)(_.ensureF(userService.usernameIsAvailable, _.failMessage("Username is not available")))
      .subRule(_.email)(_.ensureF(userService.emailIsAvailable, _.failMessage("Email is not available")))
      .build
}

trait UserService {
  def emailIsAvailable(email: Email): Future[Boolean]
  def usernameIsAvailable(username: Username): Future[Boolean]
}

object RegisterExample {
  showBuildInfo()
  val userService: UserService = new UserService {
    def emailIsAvailable(email: Email)          = Future(false)
    def usernameIsAvailable(username: Username) = Future(false)
  }

  implicit val policy: Policy[RegisterRequest] = RegisterRequest.policy(userService)

  final def main(args: Array[String]): Unit = {

    val request =
      RegisterRequest(
        username = Username(""),
        email = Email(""),
        password = "1",
        passwordRepeat = "2",
        age = 2,
      )

    awaitFuture(showErrors("ERRORS")(Field.from(request).validate))
  }
}
