package jap.fields
package examples
package tagless

import jap.fields._
import jap.fields.typeclass._
import jap.fields.syntax.all._
import jap.fields.fail._

case class DepositRequest(accountId: String, amount: Int)
object DepositRequest {
  implicit def policy[F[_]: Effect, V[_]: Validated, E: FailWith.Base]: ValidationPolicy[DepositRequest, F, V, E] =
    ValidationPolicy
      .builder[DepositRequest, F, V, E]
      .subRule(_.amount)(_ > 0)
      .subRule(_.accountId)(_.nonBlank)
      .build
}

object TaglessFinalExample {
  showBuildInfo()
  import DefaultAccumulateVM._
  final def main(args: Array[String]): Unit = {
    val requestF = Field(DepositRequest("", -1))
    showErrors("ERRORS")(requestF.validate)
  }
}
