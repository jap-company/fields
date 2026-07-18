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

package examples
package tagless

import fields.*
import fields.fail.*
import fields.typeclass.*
import fields.value.syntax.all.*

trait AccountValidator[F[_]] {
  def validateAccountId(accountId: String): F[Boolean]
}

object AccountValidator {
  def apply[F[_]](implicit AV: AccountValidator[F]): AccountValidator[F] = AV
}

case class DepositRequest(accountId: String, amount: Int)
object DepositRequest {
  implicit def policy[F[_]: AccountValidator, V[_], E: FailWith.Base](implicit
      R: RuleOps[F, V, E]
  ): PolicyK[Field[DepositRequest], F, V, E] =
    PolicyK[Field[DepositRequest], F, V, E]
      .subRule(_.amount)(_ > 0)
      .subRule(_.accountId)(
        _.nonBlank,
        _.assertF(AccountValidator[F].validateAccountId, _.failMessage("Invalid Account Id")),
      )
}

object TaglessFinalExample {
  showBuildInfo()

  implicit val accountValidator: AccountValidator[Effect.Sync] = _ => false

  import value.FieldsDsl.default.*
  final def main(args: Array[String]): Unit = {

    val requestF = Field(DepositRequest("", -1))
    showErrors("ERRORS")(requestF.validate)
  }
}
