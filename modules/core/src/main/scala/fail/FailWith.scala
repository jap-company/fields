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
package fail

import error.{FieldError, ValidationMessages as M, ValidationTypes as T}

/** Aggregates all possible FailWith* typeclasses shorthand if you need all of them. If are free to implemented only
  * those you will use. Best practise will be to but implicit instance of this into companion object of your error.
  */
trait FailWith[E, +P]
    extends FailWithMessage[E, P]
    with FailWithCompare[E, P]
    with FailWithInvalid[E, P]
    with FailWithEmpty[E, P]
    with FailWithNonEmpty[E, P]
    with FailWithMinSize[E, P]
    with FailWithMaxSize[E, P]
    with FailWithOneOf[E, P] { self =>
  trait Mixin {
    implicit val failWith: FailWith[E, P] = self
  }

  def map[B](f: (FieldPath, E) => B): FailWith[B, P] = FailWith.Mapped(f)(this)
}

object FailWith {
  type Base[E] = FailWith[E, Nothing]

  trait Builder[E, +P] extends FailWith[E, P] {
    def build[PP >: P](path: FieldPath, value: PP, errorType: => String, errorMessage: => Option[String]): E

    override def invalid[PP >: P](path: FieldPath, value: PP): E  = build[PP](path, value, T.Invalid, Some(M.Invalid))
    override def empty[PP >: P](path: FieldPath, value: PP): E    = build[PP](path, value, T.Empty, Some(M.Empty))
    override def nonEmpty[PP >: P](path: FieldPath, value: PP): E = build[PP](path, value, T.NonEmpty, Some(M.NonEmpty))
    override def minSize[PP >: P](size: Int)(path: FieldPath, value: PP): E                                     =
      build[PP](path, value, T.MinSize, Some(M.MinSize(size)))
    override def maxSize[PP >: P](size: Int)(path: FieldPath, value: PP): E                                     =
      build[PP](path, value, T.MaxSize, Some(M.MaxSize(size)))
    override def message[PP >: P](error: => String, message: => Option[String])(path: FieldPath, value: PP): E  =
      build[PP](path, value, error, message)
    override def oneOf[PP >: P](variants: Seq[PP])(path: FieldPath, value: PP): E                               =
      build[PP](path, value, T.OneOf, Some(M.OneOf(variants.map(_.toString))))
    override def compare[PP >: P](operation: CompareOperation, compared: String)(path: FieldPath, value: PP): E =
      build[PP](path, value, operation.constraint, Some(operation.message(compared)))
  }

  case class Mapped[E, F, +P](mapping: (FieldPath, E) => F)(implicit fw: FailWith[E, P]) extends FailWith[F, P] {
    override def invalid[PP >: P](path: FieldPath, value: PP): F  = mapping(path, fw.invalid[PP](path, value))
    override def empty[PP >: P](path: FieldPath, value: PP): F    = mapping(path, fw.empty[PP](path, value))
    override def nonEmpty[PP >: P](path: FieldPath, value: PP): F = mapping(path, fw.nonEmpty[PP](path, value))
    override def minSize[PP >: P](size: Int)(path: FieldPath, value: PP): F                                     =
      mapping(path, fw.minSize[PP](size)(path, value))
    override def maxSize[PP >: P](size: Int)(path: FieldPath, value: PP): F                                     =
      mapping(path, fw.maxSize[PP](size)(path, value))
    override def oneOf[PP >: P](variants: Seq[PP])(path: FieldPath, value: PP): F                               =
      mapping(path, fw.oneOf[PP](variants)(path, value))
    override def message[PP >: P](error: => String, message: => Option[String])(path: FieldPath, value: PP): F  =
      mapping(path, fw.message[PP](error, message)(path, value))
    override def compare[PP >: P](operation: CompareOperation, compared: String)(path: FieldPath, value: PP): F =
      mapping(path, fw.compare[PP](operation, compared)(path, value))
  }

  object string {
    val errorType: Base[String] = new FailWith.Builder[String, Nothing] {
      def build[P](path: FieldPath, value: P, errorType: => String, errorMessage: => Option[String]): String =
        errorType
    }

    val errorMessage: Base[String] = new FailWith.Builder[String, Nothing] {
      def build[P](path: FieldPath, value: P, errorType: => String, errorMessage: => Option[String]): String =
        errorMessage.getOrElse(errorType)
    }

    val pathAndMessage: Base[String] = Mapped((path, error: String) => s"${path.full} $error")(errorMessage)
  }

  def fieldError[E](implicit fw: FailWith[E, Nothing]): Base[FieldError[E]]   = fw.map(FieldError(_, _))
  def pathedError[E](implicit fw: FailWith[E, Nothing]): Base[(FieldPath, E)] = fw.map((_, _: E))
  def fullPathError[E](implicit fw: FailWith[E, Nothing]): Base[(String, E)]  =
    fw.map((path, error) => (path.full, error))
}
