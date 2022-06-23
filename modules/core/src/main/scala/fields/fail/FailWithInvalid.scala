package jap.fields
package fail

import scala.annotation.implicitNotFound

@implicitNotFound("To use this operation you need to have FailWithInvalid[${E}] in scope")
trait FailWithInvalid[E, +P] { def invalid[PP >: P](field: Field[PP]): E }
