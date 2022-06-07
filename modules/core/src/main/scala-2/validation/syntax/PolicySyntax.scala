package jap.fields
package syntax

import scala.concurrent.Future
import scala.language.experimental.macros

import ValidationResult._

trait PolicySyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] with FieldSyntax =>
  implicit final class PolicyOps[P, F[_], VR[_], E](policy: ValidationPolicyBuilder[P, F, VR, E]) {

    /** Adds new subrule to builder. Uses `selector` to create [[Field]], `rules` are applied to that field */
    def subRule[S](
        selector: P => S
    )(
        rules: Field[S] => F[VR[E]]*
    ): ValidationPolicyBuilder[P, F, VR, E] = macro FieldMacro.policySubRuleMacro[P, S, F, VR, E]

    /** Adds new subrule to builder. Same as [[subRule]] but for 2 subrules */
    def subRule2[S1, S2](
        selector1: P => S1,
        selector2: P => S2,
    )(
        rules: (Field[S1], Field[S2]) => F[VR[E]]*
    ): ValidationPolicyBuilder[P, F, VR, E] = macro FieldMacro.policySubRule2Macro[P, S1, S2, F, VR, E]
  }
}
