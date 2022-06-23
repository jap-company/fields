package jap.fields
package fail

import scala.annotation.implicitNotFound

@implicitNotFound("To use this operation you need to have FailWithNonEmpty[${E}] in scope")
trait FailWithNonEmpty[E, +P] { def nonEmpty[PP >: P](field: Field[PP]): E }
