package jap.fields
package fail

import scala.annotation.implicitNotFound

@implicitNotFound("To use this operation you need to have FailWithEmpty[${E}] in scope")
trait FailWithEmpty[E, +P] { def empty[PP >: P](field: Field[PP]): E }
