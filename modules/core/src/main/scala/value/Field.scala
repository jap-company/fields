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
package value

import scala.annotation.implicitNotFound

/** `Field` is heart of the library and contains [[fields.FieldPath]] and its value */
final case class Field[+P](path: FieldPath, value: P) {

  /** Returns [[fields.FieldPath.name]] of `path` */
  def name: String = path.name

  /** Returns [[fields.FieldPath.full]] of `path` */
  def fullPath: String = path.full

  /** Creates new `Field` with provided `value` and `subPath`. Appends `subPath` to this field `path` */
  def down[S](subPath: FieldPath, value: S): Field[S] = Field(path ++ subPath, value)

  /** Creates new `Field` with provided `value` and `subPath`. Appends `subPath` to this field `path` */
  def down[S](part: FieldPart, value: S): Field[S] = Field(path.down(part), value)

  /** Creates new `Field` with provided `value` and `subPath`. Appends `subPath` to this field `path` */
  def down[S](subPath: String, value: S): Field[S] = Field(path.down(subPath), value)

  /** Creates new `Field` with provided `value` and `index`. Appends `subIindex` to this field `path` */
  def down[S](subIndex: Int, value: S): Field[S] = Field(path.down(subIndex), value)

  /** Creates new `Field` with selected `value` and `subPath`. Appends `subPath` to this field `path` */
  def downS[S](subPath: String, selector: P => S): Field[S] = down(subPath, selector(value))

  /** Creates new `Field` with provided `value` and `subPath`. Appends `subPath` to this field `path` */
  def downS[S](part: FieldPart, selector: P => S): Field[S] = down(part, selector(value))

  /** Creates new `Field` with provided `value` and `subPath`. Appends `subPath` to this field `path` */
  def downS[S](subPath: FieldPath, selector: P => S): Field[S] = down(subPath, selector(value))

  /** Creates new `Field` with selected `value` and `subPath`. Appends `subPath` to this field `path` */
  def downS[S](subIndex: Int, selector: P => S): Field[S] = down(subIndex, selector(value))

  /** Renames this `Field`. Changes last `path` part aka name. */
  def named(name: String): Field[P] = withPath(path.named(name))

  /** Change `path` */
  def withPath(newPath: FieldPath): Field[P] = copy(path = newPath)

  /** Change `value` */
  def withValue[V](newValue: V): Field[V] = copy(value = newValue)

  /** Maps `value` */
  def map[B](f: P => B): Field[B] = withValue(f(value))

  def zip[B](value: B): Field[(P, B)] = Field(path, (this.value, value))

  /** Maps `path` */
  def mapPath(f: FieldPath => FieldPath): Field[P] = withPath(f(this.path))

  /** Gets first tuple element of `value`. Given `P` is Tuple */
  def first[P1, P2](implicit ev: P <:< (P1, P2)): Field[P1] = map(ev(_)._1)

  /** Gets second tuple element of `value`. Given `P` is Tuple */
  def second[P1, P2](implicit ev: P <:< (P1, P2)): Field[P2] = map(ev(_)._2)

  /** Turns Option on `value` into Option on `fields.Field`. Given `P` i `Option[V]` */
  def option[V](implicit ev: P <:< Option[V]): Option[Field[V]] = ev(value).map(withValue)

  override def toString = fullPath + ":" + value
}

object Field {

  /** Create `Field` with given `value` and [[FieldPath.Root]] `path` */
  def apply[P](value: P): Field[P] = new Field(FieldPath.Root, value)

  implicit def pathedFnToFieldFn[A, B](fn: PathedFn[A, B]): Field[A] => B = field => fn(field.path, field.value)

  /** Typeclass that is used to allow using compare syntax with both Field[P] and P itself. */
  @implicitNotFound("Cannot get ${P} from ${C}")
  trait GetValue[P, C] {
    def value(c: C): P
  }

  object GetValue extends GetValueInstances0 with GetValueInstances1

  trait GetValueInstances1 {
    implicit def valueGetValue[P, C <: P]: GetValue[P, C] = _value.asInstanceOf[GetValue[P, C]]
    private val _value: GetValue[Any, Any]                = c => c
  }

  trait GetValueInstances0 {
    implicit def fieldGetValue[P, C <: P]: GetValue[P, Field[C]] = _field.asInstanceOf[GetValue[P, Field[C]]]
    private val _field: GetValue[Any, Field[Any]]                = c => c.value
  }
}

object FieldConversions {

  /** Converts Field[A] to Field[B] using `conversion` function */
  implicit def fieldConversion[A, B](field: Field[A])(implicit conversion: A => B): Field[B] = field.map(conversion)

  /** Converts `field` into its value */
  implicit def fieldToValue[A](field: Field[A]): A = field.value
}
