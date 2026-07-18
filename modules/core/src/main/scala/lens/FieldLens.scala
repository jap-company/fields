/*
 * Copyright 2022 Jap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fields
package lens

import scala.annotation.implicitNotFound

sealed trait FieldLens[P, +V] {
  def path: FieldPath

  def foldSelect[B](value: P)(onDefined: V => B, onEmpty: => B): B

  def rule[F[_], VS[_], E](value: P)(f: PolicyK[V, F, VS, E])(implicit R: RuleOps[F, VS, E]): RuleK[F, VS, E]

  /** Change `path` */
  def withPath(p: FieldPath): FieldLens[P, V]

  /** Change `value` */
  def withSelector[S](s: P => S): FieldLens[P, S]

  def toPrism[S](f: V => Option[S]): FieldLens[P, S]

  /** Creates new `Field` with provided `value` and `subPath`. Appends `subPath` to this field `path` */
  def down[S](p: FieldPath, s: V => S): FieldLens[P, S]

  /** Returns [[fields.FieldPath.name]] of `path` */
  def name: String = path.name

  /** Returns [[fields.FieldPath.full]] of `path` */
  def fullPath: String = path.full

  /** Creates new `Field` with provided `value` and `subPath`. Appends `subPath` to this field `path` */
  def down[S](p: FieldPart, s: V => S): FieldLens[P, S] = down(FieldPath(p), s)

  /** Creates new `Field` with provided `value` and `subPath`. Appends `subPath` to this field `path` */
  def down[S](p: String, s: V => S): FieldLens[P, S] = down(FieldPath.fromPath(p), s)

  /** Creates new `Field` with provided `value` and `index`. Appends `subIindex` to this field `path` */
  def down[S](p: Int, s: V => S): FieldLens[P, S] = down(FieldPath.fromIndex(p), s)

  /** Renames this `Field`. Changes last `path` part aka name. */
  def named(name: String): FieldLens[P, V] = withPath(path.named(name))

  /** Maps `value` */
  def map[B](f: V => B): FieldLens[P, B] = down(FieldPath.Root, f)

  //  def zip[B](value: B): Field[(P, B)] = Field(path, (this.selector, value))

  /** Maps `path` */
  def mapPath(f: FieldPath => FieldPath): FieldLens[P, V] = withPath(f(this.path))

  /** Gets first tuple element of `value`. Given `P` is Tuple */
  def first[V1, V2](implicit ev: V <:< (V1, V2)): FieldLens[P, V1] = map(ev(_)._1)

  /** Gets second tuple element of `value`. Given `P` is Tuple */
  def second[V1, V2](implicit ev: V <:< (V1, V2)): FieldLens[P, V2] = map(ev(_)._2)

  override def toString = "field(" + fullPath + ")"
}

object FieldLens {

  /** `Field` is heart of the library and contains [[fields.FieldPath]] and its value */
  final case class Strict[P, +V](path: FieldPath, selector: P => V) extends FieldLens[P, V] {
    def toPrism[S](f: V => Option[S]): FieldLens[P, S] =
      Optional(path, selector.andThen(f))

    def withPath(p: FieldPath): FieldLens[P, V]           = copy(path = p)
    def withSelector[S](s: P => S): FieldLens[P, S]       = copy(selector = s)
    def down[S](p: FieldPath, s: V => S): FieldLens[P, S] = Strict(path ++ p, selector andThen s)

    def foldSelect[B](value: P)(onDefined: V => B, onEmpty: => B): B =
      onDefined(selector(value))

    def rule[F[_], VS[_], E](value: P)(f: PolicyK[V, F, VS, E])(implicit R: RuleOps[F, VS, E]): RuleK[F, VS, E] =
      f(selector(value))
  }

  final case class Optional[P, +V](path: FieldPath, selector: P => Option[V]) extends FieldLens[P, V] {
    def toPrism[S](f: V => Option[S]): FieldLens[P, S] =
      Optional(path, selector.andThen(_.flatMap(f)))

    def rule[F[_], VS[_], E](value: P)(f: PolicyK[V, F, VS, E])(implicit R: RuleOps[F, VS, E]): RuleK[F, VS, E] =
      selector(value) match {
        case Some(v) => f(v)
        case None    => R.valid
      }

    def foldSelect[B](value: P)(onDefined: V => B, onEmpty: => B): B =
      selector(value) match {
        case Some(v) => onDefined(v)
        case None    => onEmpty
      }

    def withPath(p: FieldPath): FieldLens[P, V]           = copy(path = p)
    def withSelector[S](s: P => S): FieldLens[P, S]       = copy(selector = s.andThen(Some(_)))
    def down[S](p: FieldPath, s: V => S): FieldLens[P, S] = Optional(path ++ p, selector.andThen(_.map(s)))
  }

  private val _root: FieldLens[Any, Any] = new Strict(FieldPath.Root, identity)

  /** Create `FieldLens` for type P and [[FieldPath.Root]] `path` */
  implicit def apply[P]: FieldLens[P, P] = _root.asInstanceOf[FieldLens[P, P]]

  /** Create `FieldLens` for type P with provided `path` */
  def apply[P](path: FieldPath): FieldLens[P, P] = new Strict[P, P](path, identity)

  /** Typeclass that is used to allow using compare syntax with both Field[P] and P itself. */
  @implicitNotFound("Cannot get ${P} from ${C}")
  trait GetValue[P, C, CC] {
    def value[R](p: P, c: CC)(onResult: C => R, onEmpty: => R): R
  }

  object GetValue extends GetValueInstances0 with GetValueInstances1

  trait GetValueInstances1 {
    implicit def valueGetValue[P, C, CC <: C]: GetValue[P, C, CC] = _value.asInstanceOf[GetValue[P, C, CC]]
    private val _value: GetValue[Any, Any, Any]                   =
      new GetValue[Any, Any, Any] {
        override def value[R](p: Any, c: Any)(onResult: Any => R, onEmpty: => R): R = onResult(c)
      }
  }

  trait GetValueInstances0 {
    implicit def fieldGetValue[P, C, CC <: C]: GetValue[P, C, FieldLens[P, CC]] =
      _field.asInstanceOf[GetValue[P, C, FieldLens[P, CC]]]
    private val _field: GetValue[Any, Any, FieldLens[Any, Any]]                 =
      new GetValue[Any, Any, FieldLens[Any, Any]] {
        override def value[R](p: Any, c: FieldLens[Any, Any])(onResult: Any => R, onEmpty: => R): R =
          c.foldSelect(p)(onResult, onEmpty)
      }
  }
}
