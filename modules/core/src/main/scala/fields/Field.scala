package jap.fields

import scala.concurrent.Future
import scala.language.experimental.macros
import scala.util.Try

/** [[jap.fields.Field]] is Heart of the library and contains field path and its value
  *
  * @param path
  *   fields path
  * @param value
  *   fields value
  */
final case class Field[+P](
    path: FieldPath,
    value: P,
) {

  /** Name of the [[jap.fields.Field]]
    */
  val name = path.name

  /** Full path of the field is dot-separated path of this [[jap.fields.Field]]
    */
  val fullPath = path.full

  /** Creates new [[jap.fields.Field]] with provided `value` and `name`. Prepends current fields `path` to this field
    * `path`
    */
  def provideSub[S](name: String, value: S): Field[S] = Field(path + name, value)

  /** Creates new [[jap.fields.Field]] with provided `name` and selected `value`. Prepends current fields `path` to this
    * field `path`
    */
  def selectSub[S](name: String, selector: P => S): Field[S] = Field(path + name, selector(value))

  /** Renames this [[jap.fields.Field]]. Changes last `path` part aka name.
    */
  def named(name: String): Field[P] = Field(path.named(name), value)

  /** Change [[jap.fields.Field]].`path`
    */
  def withPath(newPath: FieldPath): Field[P] = Field(newPath, value)

  /** Change [[jap.fields.Field]].`value`
    */
  def withValue[V](newValue: V): Field[V] = Field(path, newValue)

  /** Map [[jap.fields.Field]].`value`
    */
  def map[B](f: P => B): Field[B] = withValue(f(value))

  /** Map [[jap.fields.Field]].`path`
    */
  def mapPath(f: FieldPath => FieldPath): Field[P] = withPath(f(this.path))

  /** Given [[jap.fields.Field]].`value` type is Tuple, get first tuple element
    */
  def first[P1, P2](implicit ev: P <:< (P1, P2)): Field[P1] = map(ev(_)._1)

  /** Given [[jap.fields.Field]].`value` type is Tuple, get second tuple element
    */
  def second[P1, P2](implicit ev: P <:< (P1, P2)): Field[P2] = map(ev(_)._2)

  override def toString = fullPath + ":" + value
}

object Field {

  /** Create Field with given `value` and FieldPath.root `path`
    */
  def apply[P](value: P): Field[P] = new Field(FieldPath.Root, value)
}
