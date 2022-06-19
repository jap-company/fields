package jap.fields
package fail

import error.FieldError

trait CanFailWithFieldStringValidationMessage {
  implicit def failWith: FailWith.Base[FieldError[String]] = FailWithFieldStringValidationMessage
}

/** FailWithValidationMessage wrapped with [[jap.fields.error.FieldError]] in */
object FailWithFieldStringValidationMessage extends FailWithFieldError[String, Nothing](FailWithValidationMessageString)
