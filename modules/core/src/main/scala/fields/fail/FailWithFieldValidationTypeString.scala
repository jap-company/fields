package jap.fields
package fail

import error.FieldError

trait CanFailWithFieldValidationTypeString {
  implicit def failWith: FailWith.Base[FieldError[String]] = FailWithFieldValidationTypeString
}

/** FailWithValidationType wrapper with [[jap.fields.error.FieldError]] in */
object FailWithFieldValidationTypeString extends FailWithFieldError[String, Nothing](FailWithValidationTypeString)
