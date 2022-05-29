package jap.fields
package examples

import jap.fields.ValidationResult._
import jap.fields._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

import Common._
import ValidationEffect.future._

object FutureValidationModule extends AccumulateValidationModule[Future, FieldError[String]]
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
      _.min(1),
      _.max(10),
    )
    .subRule(_.age)(_ >= 18, _ <= 110)
    .subRule(_.password)(_.nonEmpty, _.min(4), _.max(100))
    .subRule2(_.password, _.passwordRepeat)(_ equalTo _)
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
        _.assertF(userService.usernameIsAvailable, _.failPath("Username is not available"))
      )
      .subRule(_.email)(_.assertF(userService.emailIsAvailable, _.failPath("Email is not available")))
      .build
}

object FutureErrorExampleApp {
  final def main(args: Array[String]) = {
    def Divider                   = "---------------"
    def await[T](f: Future[T]): T = Await.result(f, Duration.Inf)
    def printErrors(title: String, res: Future[Accumulate[FieldError[String]]], enabled: Boolean) =
      if (enabled) {
        println(Divider + title + Divider)
        println(Await.result(res, Duration.Inf).errors.mkString("\n"))
      }

    val request     =
      RegisterRequest(username = Username(""), email = Email(""), password = "1", passwordRepeat = "2", age = 2)
    val requestF    = Field.from(request)
    val userService = new UserService {
      def emailIsAvailable(email: Email)        = Future(false)
      def usernameIsAvailable(username: String) = Future(false)
    }

    val registerRequestValidator = RegisterRequestValidator(userService)
    import registerRequestValidator.policy

    printErrors("Policy", requestF.validate, enabled = true)

    // How it looks without using Policy
    val usernameF       = requestF.sub(_.username).map(_.value)
    val ageF            = requestF.sub(_.age)
    val emailF          = requestF.sub(_.email)
    val passwordF       = requestF.sub(_.password)
    val passwordRepeatF = requestF.sub(_.passwordRepeat)

    val pureValidation =
      List(
        usernameF.min(1),
        usernameF.max(10),
        usernameF.assertF(userService.usernameIsAvailable, _.failPath("Username is not available")),
        ageF >= 18,
        ageF <= 110,
        passwordF.nonEmpty,
        passwordF.min(4),
        passwordF.max(100),
        passwordF === passwordRepeatF,
        emailF.map(_.value).matches(Email.EmailRegex),
        emailF.assertF(userService.emailIsAvailable, _.failPath("Email is not available")),
      ).combineAll

    printErrors("Pure", pureValidation, enabled = true)
    println(Divider)
  }
}
