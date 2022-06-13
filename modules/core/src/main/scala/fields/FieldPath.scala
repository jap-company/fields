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

/** [[jap.fields.FieldPath]] contains path parts of the Field.
  */
case class FieldPath(private val parts: List[String]) extends AnyVal {

  /** Is current path root.
    */
  def isRoot = parts.isEmpty

  /** Full name of the path is dot-separated parts of this path. For root path this will be "root" (Object of discussion
    * what this should be)
    */
  def full: String = if (isRoot) FieldPath.RootName else parts.mkString(".")

  /** Name of the path is the last part of path.
    */
  def name: String = parts.lastOption.getOrElse(FieldPath.RootName)

  /** Changes name of this path
    */
  def named(name: String): FieldPath = FieldPath(parts.dropRight(1) :+ name)

  /** Append other [[jap.fields.FieldPath]] to current path
    */
  def ++(path: FieldPath): FieldPath = FieldPath(parts ++ path.parts)

  /** Append other path part to current path
    */
  def +(path: String): FieldPath = FieldPath(parts :+ path)

  override def toString: String = full
}

object FieldPath {

  /** Name of the FieldPath that has empty `parts` */
  val RootName = "root"

  /** Root FieldPath */
  val Root = FieldPath()

  /** Create [[jap.fields.FieldPath]] from `parts` */
  def apply(parts: String*): FieldPath = FieldPath(parts.toList)

  /** Parse [[jap.fields.FieldPath]] from dot-separated `path` string */
  def raw(path: String) = FieldPath(path.split('.').toList)

  /** Create [[jap.fields.FieldPath]] from `String` */
  def fromString(path: String): FieldPath = FieldPath(path :: Nil)

  /** Create [[jap.fields.FieldPath]] from `List[String]` */
  def fromList(path: List[String]): FieldPath = FieldPath(path)

  /** Create [[jap.fields.FieldPath]] from [[jap.fields.Field]] */
  def fromField[P](f: Field[P]): FieldPath = f.path
}

object FieldPathConversions {

  /** Conversion for [[jap.fields.FieldPath.fromString]] */
  implicit def fromString(path: String): FieldPath = FieldPath.fromString(path)

  /** Conversion for [[jap.fields.FieldPath.fromList]] */
  implicit def fromList(path: List[String]): FieldPath = FieldPath.fromList(path)

  /** Conversion for [[jap.fields.FieldPath.fromField]] */
  implicit def fromField[P](f: Field[P]): FieldPath = FieldPath.fromField(f)
}
