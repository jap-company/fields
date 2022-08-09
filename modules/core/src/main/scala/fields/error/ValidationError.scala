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
package error

/** Predefined Error type that carries `error` type and human-readable message */
sealed trait ValidationError {
  def path: FieldPath
  def error: String
  def message: Option[String]
  override def toString = s"${path.full} ${message.getOrElse(error)}"
}

object ValidationError {
  case class Invalid(path: FieldPath)  extends ValidationError {
    val error   = ValidationTypes.Invalid
    val message = Some(ValidationMessages.Invalid)
  }
  case class Empty(path: FieldPath)    extends ValidationError {
    val error   = ValidationTypes.Empty
    val message = Some(ValidationMessages.Empty)
  }
  case class NonEmpty(path: FieldPath) extends ValidationError {
    val error   = ValidationTypes.NonEmpty
    val message = Some(ValidationMessages.NonEmpty)
  }

  case class Greater(path: FieldPath, compared: String) extends ValidationError {
    val error   = ValidationTypes.Greater
    val message = Some(ValidationMessages.Greater(compared))
  }

  case class GreaterEqual(path: FieldPath, compared: String) extends ValidationError {
    val error   = ValidationTypes.GreaterEqual
    val message = Some(ValidationMessages.GreaterEqual(compared))
  }

  case class Less(path: FieldPath, compared: String) extends ValidationError {
    val error   = ValidationTypes.Less
    val message = Some(ValidationMessages.Less(compared))
  }

  case class LessEqual(path: FieldPath, compared: String) extends ValidationError {
    val error   = ValidationTypes.LessEqual
    val message = Some(ValidationMessages.LessEqual(compared))
  }

  case class Equal(path: FieldPath, compared: String) extends ValidationError {
    val error   = ValidationTypes.Equal
    val message = Some(ValidationMessages.Equal(compared))
  }

  case class NotEqual(path: FieldPath, compared: String) extends ValidationError {
    val error   = ValidationTypes.NotEqual
    val message = Some(ValidationMessages.NotEqual(compared))
  }

  case class MinSize(path: FieldPath, size: Int) extends ValidationError {
    val error   = ValidationTypes.MinSize
    val message = Some(ValidationMessages.MinSize(size))
  }

  case class MaxSize(path: FieldPath, size: Int) extends ValidationError {
    val error   = ValidationTypes.MaxSize
    val message = Some(ValidationMessages.MaxSize(size))
  }

  case class OneOf(path: FieldPath, variants: Seq[String]) extends ValidationError {
    val error   = ValidationTypes.OneOf
    val message = Some(ValidationMessages.OneOf(variants))
  }

  /** If you dont need to match on errors and just want to have separate error and user message, use this rather than
    * ValidationError
    */
  case class Message(
      path: FieldPath,
      error: String,
      message: Option[String] = None,
  ) extends ValidationError
  object Message {
    def apply(path: FieldPath, error: String, message: String): Message = Message(path, error, Some(message))
  }
}
