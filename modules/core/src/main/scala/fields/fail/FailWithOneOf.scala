package jap.fields
package fail

import scala.annotation.implicitNotFound

@implicitNotFound("To use this operation you need to have FailWithOneOf[${E}] in scope")
trait FailWithOneOf[E, +P] { def oneOf[PP >: P](variants: Seq[PP])(field: Field[PP]): E }
