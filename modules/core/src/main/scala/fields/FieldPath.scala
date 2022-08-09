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

/** `FieldPath` contains path parts of the Field. */
final case class FieldPath(parts: List[FieldPart]) extends AnyVal {

  /** Returns list of parts names */
  def names: List[String] = parts.map(_.name)

  /** Is current path root. */
  def isRoot: Boolean = parts.isEmpty

  /** Full name of the path of this path. For root path this will be "." */
  def full: String =
    parts.foldLeft(FieldPath.RootName) {
      case (FieldPath.RootName, FieldPart.Path(path)) => FieldPath.RootName + path
      case (acc, FieldPart.Path(path))                => s"$acc.$path"
      case (acc, FieldPart.Index(index))              => s"$acc[$index]"
    }

  /** Name of the path is the last part of path. */
  def name: String = parts.lastOption.map(_.name).getOrElse(FieldPath.RootName)

  /** Changes name of this path */
  def named(name: String): FieldPath = FieldPath(parts.dropRight(1) :+ FieldPart.Path(name))

  /** Append other `FieldPath` to current path */
  def ++(path: FieldPath): FieldPath = FieldPath(parts ++ path.parts)

  /** Append other path part to current path */
  def +(path: String): FieldPath = down(path)

  /** Append other index part to current path */
  def +(index: Int): FieldPath = down(index)

  /** Append other part to current path */
  def +(part: FieldPart): FieldPath = down(part)

  /** Append other part to current path */
  def down(part: FieldPart): FieldPath = FieldPath(parts :+ part)

  /** Append other path part to current path */
  def down(path: String): FieldPath = FieldPath(parts :+ FieldPart.Path(path))

  /** Append other index part to current path */
  def down(index: Int): FieldPath = FieldPath(parts :+ FieldPart.Index(index))

  /** Shows `FieldPath.full` */
  override def toString: String = full
}

object FieldPath {

  /** Name of the FieldPath that has empty `parts` */
  val RootName = "."

  /** Root FieldPath */
  val Root = FieldPath()

  /** Create `FieldPath` from Path `parts` */
  def fromPaths(parts: List[String]): FieldPath = FieldPath(parts.map(FieldPart.Path(_)))

  /** Create `FieldPath` from Path `parts` */
  def fromPaths(parts: String*): FieldPath = fromPaths(parts.toList)

  /** Create `FieldPath` from `parts` VarArgs */
  def apply(parts: FieldPart*): FieldPath = FieldPath(parts.toList)

  /** Create `FieldPath` from `String` */
  def fromPath(path: String): FieldPath = FieldPath(FieldPart.Path(path))

  /** Create `FieldPath` from `Int` */
  def fromIndex(index: Int): FieldPath = FieldPath(FieldPart.Index(index))

  /** Create `FieldPath` from [[jap.fields.Field]] */
  def fromField[P](f: Field[P]): FieldPath = f.path

  val IndexRegex = "\\[(\\d+)\\]".r

  /** Parse `FieldPath` from dot-separated `path` string */
  def parse(path: String) = FieldPath(
    path
      .split('.')
      .flatMap { part =>
        if (part.isEmpty) Nil
        else
          FieldPart.Path(part.takeWhile(_ != '[')) +: IndexRegex
            .findAllMatchIn(part)
            .map(i => FieldPart.Index(i.group(1).toInt))
            .toList
      }
      .toList
  )
}

sealed abstract class FieldPart {
  def name: String
}
object FieldPart                {
  case class Path(value: String) extends FieldPart {
    def name = value
  }
  case class Index(value: Int)   extends FieldPart {
    def name = value.toString
  }
}

object FieldPartConversions {

  /** Conversion from `String` to [[jap.fields.FieldPart.Path]] */
  implicit def fromPath(path: String): FieldPart = FieldPart.Path(path)

  /** Conversion from `String` to [[jap.fields.FieldPart.Index]] */
  implicit def fromIndex(index: Int): FieldPart = FieldPart.Index(index)
}

object FieldPathConversions {

  /** Conversion for `FieldPath.fromPath` */
  implicit def fromPath(path: String): FieldPath = FieldPath.fromPath(path)

  /** Conversion for `FieldPath.fromIndex` */
  implicit def fromIndex(index: Int): FieldPath = FieldPath.fromIndex(index)

  /** Conversion for `FieldPath.apply` */
  implicit def fromParts(parts: List[FieldPart]): FieldPath = FieldPath(parts)

  /** Conversion for `FieldPath.fromPaths` */
  implicit def fromPaths(parts: List[String]): FieldPath = FieldPath.fromPaths(parts)

  /** Conversion for `FieldPath.fromField` */
  implicit def fromField[P](f: Field[P]): FieldPath = FieldPath.fromField(f)
}
