package jap.fields
package syntax

import scala.collection.generic.IsIterable
import scala.concurrent.Future

import ValidationResult._

trait PolicySyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] with FieldSyntax =>
  extension [P](builder: ValidationPolicyBuilder[P, F, VR, E]) {

    /** Adds new subrule to builder. Uses `selector` to create [[Field]], `rules` are applied to that field */
    inline def subRule[S](
        inline selector: P => S
    )(rules: Field[S] => F[VR[E]]*): ValidationPolicyBuilder[P, F, VR, E] =
      builder.fieldRule(_.sub(selector))(rules: _*)

      /** Adds new subrule to builder. Same as [[subRule]] but for 2 subrules */
    inline def subRule2[S1, S2](
        inline selector1: P => S1,
        inline selector2: P => S2,
    )(rules: Tuple2[Field[S1], Field[S2]] => F[VR[E]]*): ValidationPolicyBuilder[P, F, VR, E] =
      builder.fieldRule2[S1, S2](_.sub(selector1), _.sub(selector2))(rules.map(r => r(_: Field[S1], _: Field[S2])): _*)
  }

}
