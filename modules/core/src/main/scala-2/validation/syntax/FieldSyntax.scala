package jap.fields
package syntax

import scala.language.experimental.macros

trait FieldSyntax {
  implicit def toFieldSubOps[P](field: Field[P]): FieldSubOps[P] = new FieldSubOps(field)
  implicit def toFieldFromOps(field: Field.type): FieldFromOps   = new FieldFromOps(field)
}

final class FieldSubOps[P](private val field: Field[P]) extends AnyVal {

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
  def sub[S](selector: P => S): Field[S] = macro FieldMacro.subMacro[P, S]
}

final class FieldFromOps(private val field: Field.type) extends AnyVal {

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
  def from[V](value: V): Field[V] = macro FieldMacro.fromMacro[V]

  /** Similar to [[Field.from]] but drops first selector path
    *
    * Example:
    * {{{
    * scala> val request = Request(User("ann"))
    * val request: Request = Request(User(ann))
    * scala> val field = Field.sub(request.user.name)
    * val field: jap.fields.Field[String] = user.name:ann
    * }}}
    */
  def sub[V](value: V): Field[V] = macro FieldMacro.fromSubMacro[V]
}
