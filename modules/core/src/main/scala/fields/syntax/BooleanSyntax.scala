package jap.fields
package syntax

import scala.concurrent.Future

import ValidationResult._


trait BooleanSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toBooleanFieldOps(field: Field[Boolean]): BooleanFieldOps[F, VR, E] =
    new BooleanFieldOps(field)
}

final class BooleanFieldOps[F[_], VR[_], E](private val field: Field[Boolean]) extends AnyVal {
  def isTrue(implicit M: ValidationModule[F, VR, E]) = M.assert[Boolean](field, _ == true, _.equal("true"))

  def isFalse(implicit M: ValidationModule[F, VR, E]) = M.assert[Boolean](field, _ == false, _.equal("false"))
}
