package jap.fields
package syntax

import scala.language.experimental.macros

trait FieldSyntax {
  implicit def toFieldSubOps[P](field: Field[P]): FieldSubOps[P] = new FieldSubOps(field)
  implicit def toFieldFromOps(field: Field.type): FieldFromOps   = new FieldFromOps(field)
}

final class FieldSubOps[P](private val field: Field[P]) extends AnyVal {
  def sub[S](selector: P => S): Field[S] = macro FieldMacro.subMacro[P, S]
}

final class FieldFromOps(private val field: Field.type) extends AnyVal {
  def from[V](value: V): Field[V] = macro FieldMacro.fromMacro[V]
}
