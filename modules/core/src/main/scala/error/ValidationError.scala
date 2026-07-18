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
package error

import fail.*
import typeclass.PrependPath

/** Predefined Error type that carries `error` type and human-readable message */
sealed trait ValidationError {
  def path: FieldPath
  def withPath(path: FieldPath): ValidationError
  def error: String
  def message: Option[String]
  override def toString = s"${path.full} ${message.getOrElse(error)}"
}

object ValidationError {
  implicit val prependPath: PrependPath[ValidationError] = (p, e) => e.withPath(path = p ++ e.path)
  implicit val failWith: FailWith.Base[ValidationError]  = new FailWith.Base[ValidationError] {
    def invalid[A](path: FieldPath, value: A): ValidationError                 = Invalid(path)
    def empty[A](path: FieldPath, value: A): ValidationError                   = Empty(path)
    def nonEmpty[A](path: FieldPath, value: A): ValidationError                = NonEmpty(path)
    def minSize[A](size: Int)(path: FieldPath, value: A): ValidationError      = MinSize(path, size)
    def maxSize[A](size: Int)(path: FieldPath, value: A): ValidationError      = MaxSize(path, size)
    def oneOf[A](variants: Seq[A])(path: FieldPath, value: A): ValidationError = OneOf(path, variants.map(_.toString))
    def message[A](error: => String, message: => Option[String])(path: FieldPath, value: A): ValidationError  =
      Message(path, error, message)
    def compare[A](operation: CompareOperation, compared: String)(path: FieldPath, value: A): ValidationError = {
      operation match {
        case CompareOperation.Equal        => Equal(path, compared)
        case CompareOperation.NotEqual     => NotEqual(path, compared)
        case CompareOperation.Greater      => Greater(path, compared)
        case CompareOperation.GreaterEqual => GreaterEqual(path, compared)
        case CompareOperation.Less         => Less(path, compared)
        case CompareOperation.LessEqual    => LessEqual(path, compared)
      }
    }
  }

  case class Invalid(path: FieldPath)  extends ValidationError {
    def withPath(path: FieldPath): ValidationError = copy(path = path)
    final val error: String                        = ValidationTypes.Invalid
    final val message: Option[String]              = Some(ValidationMessages.Invalid)
  }
  case class Empty(path: FieldPath)    extends ValidationError {
    def withPath(path: FieldPath): ValidationError = copy(path = path)
    final val error: String                        = ValidationTypes.Empty
    final val message: Option[String]              = Some(ValidationMessages.Empty)
  }
  case class NonEmpty(path: FieldPath) extends ValidationError {
    def withPath(path: FieldPath): ValidationError = copy(path = path)
    final val error: String                        = ValidationTypes.NonEmpty
    final val message: Option[String]              = Some(ValidationMessages.NonEmpty)
  }

  case class Greater(path: FieldPath, compared: String) extends ValidationError {
    def withPath(path: FieldPath): ValidationError = copy(path = path)
    final val error: String                        = ValidationTypes.Greater
    final val message: Option[String]              = Some(ValidationMessages.Greater(compared))
  }

  case class GreaterEqual(path: FieldPath, compared: String) extends ValidationError {
    def withPath(path: FieldPath): ValidationError = copy(path = path)
    final val error: String                        = ValidationTypes.GreaterEqual
    final val message: Option[String]              = Some(ValidationMessages.GreaterEqual(compared))
  }

  case class Less(path: FieldPath, compared: String) extends ValidationError {
    def withPath(path: FieldPath): ValidationError = copy(path = path)
    final val error: String                        = ValidationTypes.Less
    final val message: Option[String]              = Some(ValidationMessages.Less(compared))
  }

  case class LessEqual(path: FieldPath, compared: String) extends ValidationError {
    def withPath(path: FieldPath): ValidationError = copy(path = path)
    final val error: String                        = ValidationTypes.LessEqual
    final val message: Option[String]              = Some(ValidationMessages.LessEqual(compared))
  }

  case class Equal(path: FieldPath, compared: String) extends ValidationError {
    def withPath(path: FieldPath): ValidationError = copy(path = path)
    final val error: String                        = ValidationTypes.Equal
    final val message: Option[String]              = Some(ValidationMessages.Equal(compared))
  }

  case class NotEqual(path: FieldPath, compared: String) extends ValidationError {
    def withPath(path: FieldPath): ValidationError = copy(path = path)
    final val error: String                        = ValidationTypes.NotEqual
    final val message: Option[String]              = Some(ValidationMessages.NotEqual(compared))
  }

  case class MinSize(path: FieldPath, size: Int) extends ValidationError {
    def withPath(path: FieldPath): ValidationError = copy(path = path)
    final val error: String                        = ValidationTypes.MinSize
    final val message: Option[String]              = Some(ValidationMessages.MinSize(size))
  }

  case class MaxSize(path: FieldPath, size: Int) extends ValidationError {
    def withPath(path: FieldPath): ValidationError = copy(path = path)
    final val error: String                        = ValidationTypes.MaxSize
    final val message: Option[String]              = Some(ValidationMessages.MaxSize(size))
  }

  case class OneOf(path: FieldPath, variants: Seq[String]) extends ValidationError {
    def withPath(path: FieldPath): ValidationError = copy(path = path)
    final val error: String                        = ValidationTypes.OneOf
    final val message: Option[String]              = Some(ValidationMessages.OneOf(variants))
  }

  /** If you dont need to match on errors and just want to have separate error and user message, use this rather than
    * ValidationError
    */
  case class Message(
      path: FieldPath,
      error: String,
      message: Option[String] = None,
  ) extends ValidationError {
    def withPath(path: FieldPath): ValidationError = copy(path = path)
  }
  object Message {
    def apply(path: FieldPath, error: String, message: String): Message = Message(path, error, Some(message))
  }
}
