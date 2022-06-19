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

import cats._
import jap.fields._
import jap.fields.error._
import jap.fields.fail._
import jap.fields.typeclass.Effect.future._
import jap.fields.typeclass.Validated
import zio._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

// Try changing Module delclaration to see how easy it is to swap Error type
// object FutureValidationModule extends AccumulateVM[Future, ValidationError]
// object FutureValidationModule extends AccumulateVM[Future, FieldError[String]]
object FutureValidationModule extends AccumulateVM[Future, ValidationMessage] with CanFailWithValidationMessage
import FutureValidationModule._

case class Email(value: String) extends AnyVal
object Email {
  val EmailRegex                     =
    "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$".r
  implicit val policy: Policy[Email] = _.map(_.value).matches(EmailRegex)
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
    .subRule(_.email)(_.map(_.value).matches(Email.EmailRegex))
    .subRule(_.password)(_.nonEmpty, _.minSize(4), _.maxSize(100))
    .subRule(_.password, _.passwordRepeat)(_ equalTo _)
    .build
}

trait UserService {
  def emailIsAvailable(email: Email): Future[Boolean]
  def usernameIsAvailable(username: String): Future[Boolean]
}

case class RegisterRequestValidator(userService: UserService) {
  implicit val policy: Policy[RegisterRequest] =
    Policy
      .builder[RegisterRequest]
      // Include default validation
      .rule(RegisterRequest.policy.validate)
      // Effectful validations
      .fieldRule(_.sub(_.username).map(_.value))(
        _.ensureF(userService.usernameIsAvailable, _.failMessage("Username is not available"))
      )
      .subRule(_.email)(_.ensureF(userService.emailIsAvailable, _.failMessage("Email is not available")))
      .build
}

object FieldsExample {
  final def main(args: Array[String]) = {
    def Divider                   = "---------------"
    def await[T](f: Future[T]): T = Await.result(f, Duration.Inf)
    def printErrors[V[_]: Validated, E](title: String, res: Future[V[E]], enabled: Boolean) =
      if (enabled) {
        println(Divider + title + Divider)
        println(Await.result(res, Duration.Inf).errors.mkString("\n"))
      }

    val request     =
      RegisterRequest(username = Username(""), email = Email(""), password = "1", passwordRepeat = "2", age = 2)
    val userService = new UserService {
      def emailIsAvailable(email: Email)        = Future(false)
      def usernameIsAvailable(username: String) = Future(false)
    }

    val registerRequestValidator = RegisterRequestValidator(userService)
    import registerRequestValidator.policy

    // How it looks without using Policy
    val requestF        = Field.from(request)
    val usernameF       = requestF.sub(_.username).map(_.value)
    val ageF            = requestF.sub(_.age)
    val emailF          = requestF.sub(_.email)
    val passwordF       = requestF.sub(_.password)
    val passwordRepeatF = requestF.sub(_.passwordRepeat)

    println("Fields Build Info: " + jap.fields.BuildInfo)

    val pureValidation =
      for {
        _ <- usernameF.minSize(1)
        _ <- usernameF.maxSize(10)
        _ <- usernameF.ensureF(userService.usernameIsAvailable, _.failMessage("Username is not available"))
        _ <- ageF >= 18
        _ <- MRule.whenF(Future.successful(true))(ageF <= 110)
        _ <- passwordF.nonEmpty
        _ <- passwordF.minSize(4)
        _ <- passwordF.maxSize(100)
        _ <- passwordF === passwordRepeatF
        _ <- emailF.map(_.value).matches(Email.EmailRegex)
        _ <- emailF.ensureF(userService.emailIsAvailable, _.failMessage("Email is not available"))
      } yield V.valid

    println(await(requestF.validate.errors))
    printErrors("Policy", requestF.validate.unwrap, enabled = true)
    printErrors("Pure", pureValidation.unwrap, enabled = true)
    println(Divider)
  }
}
