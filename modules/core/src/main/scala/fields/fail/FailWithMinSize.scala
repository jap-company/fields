package jap.fields
package fail

import scala.annotation.implicitNotFound

@implicitNotFound("To use this operation you need to have FailWithMinSize[${E}] in scope")
trait FailWithMinSize[E, +P] { def minSize[PP >: P](size: Int)(field: Field[PP]): E }
