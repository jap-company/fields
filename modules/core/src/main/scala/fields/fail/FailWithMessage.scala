package jap.fields
package fail

import scala.annotation.implicitNotFound

@implicitNotFound("To use this operation you need to have FailWithMessage[${E}] in scope")
trait FailWithMessage[E, +P] { def message[PP >: P](error: String, message: Option[String])(field: Field[PP]): E }
