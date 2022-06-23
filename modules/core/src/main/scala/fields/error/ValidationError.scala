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
  override def toString = s"$path -> ${message.getOrElse(error)}"
}

object ValidationError    {
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

/** This corresponds to `error` field of ValidationError with given names */
object ValidationTypes    {
  val Invalid      = "INVALID_ERROR"
  val Empty        = "EMPTY_ERROR"
  val NonEmpty     = "NON_EMPTY_ERROR"
  val Greater      = "GREATER_ERROR"
  val GreaterEqual = "GREATER_EQUAL_ERROR"
  val Less         = "LESS_ERROR"
  val LessEqual    = "LESS_EQUAL_ERROR"
  val Equal        = "EQUAL_ERROR"
  val NotEqual     = "NOT_EQUAL_ERROR"
  val MinSize      = "MIN_SIZE_ERROR"
  val MaxSize      = "MAX_SIZE_ERROR"
  val OneOf        = "ONE_OF_ERROR"
}

/** ValidationError error messages */
object ValidationMessages {
  val Invalid                        = s"should be valid"
  val NonEmpty                       = s"should not be empty"
  val Empty                          = s"should be empty"
  def Greater(compared: String)      = s"should be greater than $compared"
  def GreaterEqual(compared: String) = s"should be greater than or equal to $compared"
  def Less(compared: String)         = s"should be less than $compared"
  def LessEqual(compared: String)    = s"should be less than or equal to $compared"
  def Equal(compared: String)        = s"should be equal to $compared"
  def NotEqual(compared: String)     = s"should not be equal to $compared"
  def MinSize(size: Int)             = s"min size should be $size"
  def MaxSize(size: Int)             = s"max size should be $size"
  def OneOf(variants: Seq[String])   = s"should be one of ${variants.mkString(",")}"
}
