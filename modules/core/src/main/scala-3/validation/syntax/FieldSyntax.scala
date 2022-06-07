package jap.fields
package syntax

trait FieldSyntax {
  extension [P](field: Field[P]) {

    /** Returns subfield using `selector` function to extract value and as path
      *
      * Example
      * {{{
      * scala> val request = Request(User("ann"))
      * scala> val field = Field.from(request)
      * val field: jap.fields.Field[Request] = request:Request(User(ann))
      * scala> field.sub(_.user.name)
      * val res1: jap.fields.Field[String] = request.user.name:ann
      * }}}
      */
    inline def sub[S](inline selector: P => S): Field[S] = ${ FieldMacro.subImpl('field, 'selector) }
  }

  extension (field: Field.type) {

    /** Returns [[Field]] that has provided value and infers its [[FieldPath]] from field selects
      *
      * Example:
      * {{{
      * scala> val request = Request(User("ann"))
      * val request: Request = Request(User(ann))
      * scala> val field = Field.from(request.user.name)
      * val field: jap.fields.Field[String] = request.user.name:ann
      * }}}
      */
    inline def from[V](inline value: V): Field[V] = ${ FieldMacro.fromImpl('value) }
  }
}
