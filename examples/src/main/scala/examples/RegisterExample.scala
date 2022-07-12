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

import jap.fields._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Validation {
  import jap.fields.typeclass.Effect.future._
  import jap.fields.fail._
  import jap.fields.error._
  // Try changing Module delclaration to see how easy it is to swap Error type
  // object all extends AccumulateVM[Future, ValidationError] with CanFailWithValidationError
  object all extends AccumulateVM[Future, FieldError[String]] with CanFailWithValidationMessageFieldString
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

  def validateUsername(username: Field[Username]): MRule = {
    val usernameS = username.map(_.value)
    usernameS.minSize(1) && usernameS.maxSize(10)
  }

  implicit val basicPolicy: Policy[RegisterRequest] = Policy
    .builder[RegisterRequest]
    .subRule(_.age)(_ >= 18, _ <= 110)
    .subRule(_.email)(_.map(_.value).matchesRegex(Email.EmailRegex))
    .subRule(_.password)(_.nonEmpty, _.minSize(4), _.maxSize(100))
    .subRule(_.password, _.passwordRepeat)(_ equalTo _)
    // Commented code is the same rule rewritten in 4 different ways
    // .fieldRule(_.sub(_.username).map(_.value))(_.minSize(1), _.maxSize(10))
    // .subRule(_.username)(_.map(_.value).all(_.minSize(1), _.maxSize(10)))
    // .subRule(_.username)(validateUsername)
    .rule { request =>
      val username = request.sub(_.username).map(_.value)
      username.minSize(1) && username.maxSize(10)
    }
    .build

  /** You could extract some complex validation logic to methods, they could even be Tagless Final if you wish to reuse
    * it with different Effect, Validated, Error types
    */
  def validateNoActiveUser(username: Field[Username])(implicit userService: UserService) =
    Rule.flatten {
      userService.getUser(username.value).map {
        case Some(user) if user.active => Rule.pure(username.failMessage("Already used by active user"))
        case _                         => Rule.valid
      }
    }

  def asyncPolicy(implicit userService: UserService): Policy[RegisterRequest] =
    Policy
      .builder[RegisterRequest]
      // Below will work to but keep in mind not to fall for cyclic implicit resolution
      // .rule(RegisterRequest.basicPolicy)
      // .rule(_.validate)
      // Our Async validations
      .subRule(_.username)(
        _.ensureF(userService.usernameIsAvailable, _.failMessage("Username is not available")),
        validateNoActiveUser,
      )
      .subRule(_.email)(_.ensureF(userService.emailIsAvailable, _.failMessage("Email is not available")))
      .build
      .and(RegisterRequest.basicPolicy)
}

trait UserService {
  def emailIsAvailable(email: Email): Future[Boolean]
  def usernameIsAvailable(username: Username): Future[Boolean]
  def getUser(username: Username): Future[Option[User]]
}

case class User(usermame: Username, active: Boolean)

object RegisterExample {
  showBuildInfo()
  implicit val userService: UserService = new UserService {
    def emailIsAvailable(email: Email)                    = Future(false)
    def usernameIsAvailable(username: Username)           = Future(false)
    def getUser(username: Username): Future[Option[User]] = Future(Some(User(username, true)))
  }

  implicit val policy: Policy[RegisterRequest] = RegisterRequest.asyncPolicy

  final def main(args: Array[String]): Unit = {

    val request =
      RegisterRequest(
        username = Username(""),
        email = Email(""),
        password = "1",
        passwordRepeat = "2",
        age = 2,
      )

    awaitReady(showErrors("ERRORS")(Field.from(request).validate))
  }
}
