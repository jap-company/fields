package jap.fields
package syntax

trait FieldSyntax {
  extension [P](field: Field[P]) {
    inline def sub[S](inline selector: P => S): Field[S] = ${ FieldMacro.subImpl('field, 'selector) }
  }

  extension (field: Field.type) {
    inline def from[V](inline value: V): Field[V] = ${ FieldMacro.fromImpl('value) }
  }
}
