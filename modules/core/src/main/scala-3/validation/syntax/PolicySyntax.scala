package jap.fields
package syntax

import scala.collection.generic.IsIterable

import ValidationResult._

import scala.concurrent.Future

trait PolicySyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] with FieldSyntax =>
  extension [P](policy: ValidationPolicyBuilder[P, F, VR, E]) {
    inline def subRule[S](inline selector: P => S)(
        rules: Field[P] ?=> Field[S] => F[VR[E]]*
    ): ValidationPolicyBuilder[P, F, VR, E] =
      policy.rule { f =>
        given Field[P] = f
        M.combineAll(rules.map(_.apply(f.sub(selector))))
      }

    inline def subRule2[S1, S2](inline selector1: P => S1, inline selector2: P => S2)(
        rules: Field[P] ?=> Tuple2[Field[S1], Field[S2]] => F[VR[E]]*
    ): ValidationPolicyBuilder[P, F, VR, E] =
      policy.rule { f =>
        given Field[P] = f
        M.combineAll(rules.map(_.apply(f.sub(selector1), f.sub(selector2))))
      }

  }

  def validated[P](using f: Field[P]) = f
}
