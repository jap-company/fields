package jap.fields
package fail

import scala.annotation.implicitNotFound

@implicitNotFound("To use this operation you need to have FailWithMaxSize[${E}] in scope")
trait FailWithMaxSize[E, +P] { def maxSize[PP >: P](size: Int)(field: Field[PP]): E }
