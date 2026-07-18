package fields

import error.ValidationError
import typeclass.Effect

package object value {
  val defaultDsl = new FieldsDsl[Effect.Sync, Accumulate, ValidationError]
}
