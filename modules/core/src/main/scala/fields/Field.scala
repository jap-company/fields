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

package jap.fields

/** [[jap.fields.Field]] is heart of the library and contains [[jap.fields.FieldPath]] and its value */
final case class Field[+P](
    path: FieldPath,
    value: P,
) {

  /** Returns [[jap.fields.FieldPath.name]] of `path` */
  val name = path.name

  /** Returns [[jap.fields.FieldPath.full]] of `path` */
  val fullPath = path.full

  /** Creates new [[jap.fields.Field]] with provided `value` and `name`. Prepends current fields `path` to this field
    * `path`
    */
  def provideSub[S](name: String, value: S): Field[S] = Field(path + name, value)

  /** Creates new [[jap.fields.Field]] with provided `name` and selected `value`. Prepends current fields `path` to this
    * field `path`
    */
  def selectSub[S](name: String, selector: P => S): Field[S] = Field(path + name, selector(value))

  /** Renames this [[jap.fields.Field]]. Changes last `path` part aka name. */
  def named(name: String): Field[P] = withPath(path.named(name))

  /** Change `path` */
  def withPath(newPath: FieldPath): Field[P] = copy(path = newPath)

  /** Change `value` */
  def withValue[V](newValue: V): Field[V] = copy(value = newValue)

  /** Maps `value` */
  def map[B](f: P => B): Field[B] = withValue(f(value))

  /** Maps `path` */
  def mapPath(f: FieldPath => FieldPath): Field[P] = withPath(f(this.path))

  /** Gets first tuple element of `value`. Given `P` is Tuple */
  def first[P1, P2](implicit ev: P <:< (P1, P2)): Field[P1] = map(ev(_)._1)

  /** Gets second tuple element of `value`. Given `P` is Tuple */
  def second[P1, P2](implicit ev: P <:< (P1, P2)): Field[P2] = map(ev(_)._2)

  /** Turns Option on `value` into Option on `jap.fields.Field`. Given `P` i `Option[V]` */
  def option[V](implicit ev: P <:< Option[V]): Option[Field[V]] = ev(value).map(withValue)

  override def toString = fullPath + ":" + value
}

object Field {

  /** Create [[jap.fields.Field]] with given `value` and [[FieldPath.Root]] `path` */
  def apply[P](value: P): Field[P] = new Field(FieldPath.Root, value)
}

object FieldConversions {

  /** Converts Field[A] to Field[B] using `conversion` function */
  implicit def fieldConversion[A, B](field: Field[A])(implicit conversion: A => B): Field[B] = field.map(conversion)

  /** Converts `field` into its value */
  implicit def fieldToValue[A](field: Field[A]): A = field.value
}
