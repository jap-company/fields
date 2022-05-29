package jap.fields
package syntax

import scala.concurrent.Future
import scala.language.experimental.macros

import ValidationResult._

trait PolicySyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] with FieldSyntax =>
  implicit final class PolicyOps[P, F[_], VR[_], E](policy: ValidationPolicyBuilder[P, F, VR, E]) {
    def subRule[S](
        selector: P => S
    )(
        rules: Field[S] => F[VR[E]]*
    ): ValidationPolicyBuilder[P, F, VR, E] = macro FieldMacro.policySubRuleMacro[P, S, F, VR, E]

    def subRule2[S1, S2](
        selector1: P => S1,
        selector2: P => S2,
    )(
        rules: (Field[S1], Field[S2]) => F[VR[E]]*
    ): ValidationPolicyBuilder[P, F, VR, E] = macro FieldMacro.policySubRule2Macro[P, S1, S2, F, VR, E]
  }
}
