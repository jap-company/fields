package jap.fields

import scala.concurrent.Future
import scala.language.experimental.macros
import scala.util.Try

/** [[Field]] is heart of the library and contains [[FieldPath]] and its value */
final case class Field[+P](
    path: FieldPath,
    value: P,
) {

  /** Returns name of the [[Field]] */
  val name = path.name

  /** Returns full path of the field. It is dot-separated path of this [[Field]] */
  val fullPath = path.full

  /** Creates new [[Field]] with provided `value` and `name`. Prepends current fields `path` to this field `path` */
  def provideSub[S](name: String, value: S): Field[S] = Field(path + name, value)

  /** Creates new [[Field]] with provided `name` and selected `value`. Prepends current fields `path` to this field
    * `path`
    */
  def selectSub[S](name: String, selector: P => S): Field[S] = Field(path + name, selector(value))

  /** Renames this [[Field]]. Changes last `path` part aka name. */
  def named(name: String): Field[P] = Field(path.named(name), value)

  /** Change [[Field.path]] */
  def withPath(newPath: FieldPath): Field[P] = Field(newPath, value)

  /** Change [[Field.value]] */
  def withValue[V](newValue: V): Field[V] = Field(path, newValue)

  /** Map [[Field.value]] */
  def map[B](f: P => B): Field[B] = withValue(f(value))

  /** Map [[Field.path]] */
  def mapPath(f: FieldPath => FieldPath): Field[P] = withPath(f(this.path))

  /** Given [[Field.value]] type is Tuple, get first tuple element */
  def first[P1, P2](implicit ev: P <:< (P1, P2)): Field[P1] = map(ev(_)._1)

  /** Given [[Field.value]] type is Tuple, get second tuple element */
  def second[P1, P2](implicit ev: P <:< (P1, P2)): Field[P2] = map(ev(_)._2)

  override def toString = fullPath + ":" + value
}

object Field {

  /** Create [[Field]] with given `value` and [[FieldPath.Root]] `path` */
  def apply[P](value: P): Field[P] = new Field(FieldPath.Root, value)
}
