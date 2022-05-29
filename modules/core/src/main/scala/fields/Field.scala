package jap.fields

import scala.annotation.implicitNotFound
import scala.concurrent.Future
import scala.language.experimental.macros
import scala.util.Try

final case class Field[+P](
    path: FieldPath,
    value: P,
) {
  val name     = path.name
  val fullPath = path.full

  def provideSub[S](name: String, value: S): Field[S]        = Field(path + name, value)
  def selectSub[S](name: String, selector: P => S): Field[S] = Field(path + name, selector(value))

  def named(name: String): Field[P]                = Field(path.named(name), value)
  def withPath(newPath: FieldPath): Field[P]       = Field(newPath, value)
  def withValue[V](newValue: V): Field[V]          = Field(path, newValue)
  def map[B](f: P => B): Field[B]                  = withValue(f(value))
  def mapPath(f: FieldPath => FieldPath): Field[P] = withPath(f(this.path))
  def error[E](error: E): FieldError[E]            = FieldError[E](path, error)

  // Tuple Ops
  def first[P1, P2](implicit ev: P <:< (P1, P2)): Field[P1]  = map(ev(_)._1)
  def second[P1, P2](implicit ev: P <:< (P1, P2)): Field[P2] = map(ev(_)._2)

  override def toString = fullPath + ":" + value
}

object Field {
  def apply[P](value: P): Field[P] = new Field(FieldPath.root, value)
}
