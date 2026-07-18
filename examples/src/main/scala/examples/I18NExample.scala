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

package examples
package i18n

import zio.*
import zio.Console.*

import fields.*
import fields.error.*
import fields.fail.*
import fields.value.FieldsDsl
import FieldsInteropZIO.*

import java.time.*

/** Simple ADT that we will interpret to construct localised message */
sealed trait TranslatedMessage
object TranslatedMessage {
  case class Key(value: String)                       extends TranslatedMessage
  case class Pure(value: String)                      extends TranslatedMessage
  case class Sentence(parts: List[TranslatedMessage]) extends TranslatedMessage
  object Sentence {
    def apply(parts: TranslatedMessage*): Sentence = new Sentence(parts.toList)
  }
}

/** So in nutshell we make `message` and blueprint for constructing localised message and will interpret when needed,
  * this gives us opportunity to have freedom of how to translate it
  */
final case class TranslatedError(path: FieldPath, error: String, message: TranslatedMessage)
object TranslatedError {
  import TranslatedMessage.*
  implicit object FailWithLocalisedError extends FailWith[TranslatedError, Nothing] {
    def invalid[A](path: FieldPath, value: A): TranslatedError =
      TranslatedError(path, ValidationTypes.Invalid, Key(ValidationTypes.Invalid))

    def empty[A](path: FieldPath, value: A): TranslatedError =
      TranslatedError(path, ValidationTypes.Empty, Key(ValidationTypes.Empty))

    def nonEmpty[A](path: FieldPath, value: A): TranslatedError =
      TranslatedError(path, ValidationTypes.NonEmpty, Key(ValidationTypes.NonEmpty))

    def minSize[A](size: Int)(path: FieldPath, value: A): TranslatedError =
      TranslatedError(path, ValidationTypes.MinSize, Sentence(Key(ValidationTypes.MinSize), Pure(size.toString)))

    def maxSize[A](size: Int)(path: FieldPath, value: A): TranslatedError =
      TranslatedError(path, ValidationTypes.MaxSize, Sentence(Key(ValidationTypes.MaxSize), Pure(size.toString)))

    def oneOf[A](variants: Seq[A])(path: FieldPath, value: A): TranslatedError =
      TranslatedError(
        path,
        ValidationTypes.MaxSize,
        Sentence(Key(ValidationTypes.OneOf), Pure(variants.mkString(","))),
      )

    def message[A](error: => String, message: => Option[String])(path: FieldPath, value: A): TranslatedError =
      TranslatedError(path, error, Key(message.getOrElse(error)))

    def compare[A](operation: CompareOperation, compared: String)(path: FieldPath, value: A): TranslatedError =
      TranslatedError(path, operation.constraint, Sentence(Key(operation.constraint), Pure(compared)))
  }
}

object Validation extends FieldsDsl.BaseAccumulate[Task, TranslatedError]
import Validation.*

case class Post(
    id: Long,
    title: String,
    description: Option[String],
    created: LocalDateTime,
    modified: LocalDateTime,
)
object Post {
  implicit val policy: Policy[Post] =
    Policy[Post]
      .subRule(_.id)(
        _ > 0L,
        _.assert(_ != 4L, _.failMessage("NOT_4")),
      )
      .subRule(_.title)(_.minSize(5), _.maxSize(10))
      .subRule(_.description)(_.some(_.all(_.minSize(5), _.maxSize(10))))
      .subRule(_.created, _.modified)(_ <= _)
}

case class Blog(posts: List[Post], authorId: Long)
object Blog {
  implicit val policy: Policy[Blog] =
    Policy[Blog]
      .subRule(_.authorId)(_ > 0L)
      .subRule(_.posts)(_.each(_.validate))
}

sealed trait Locale
object Locale {
  case object EN extends Locale
  case object UA extends Locale
}

final case class I18N(locales: Map[Locale, Map[String, String]]) {
  def apply(key: String)(locale: Locale): String = locales(locale)(key)

  def translateAll(locale: Locale)(errors: List[TranslatedError]): Task[List[ValidationError.Message]] =
    ZIO.collectAll(errors.map(translate(locale)))

  def translate(locale: Locale)(error: TranslatedError): Task[ValidationError.Message] = {
    def translateMessage(msg: TranslatedMessage): String =
      msg match {
        case TranslatedMessage.Key(key)        => apply(key)(locale)
        case TranslatedMessage.Pure(value)     => value
        case TranslatedMessage.Sentence(parts) => parts.map(translateMessage).mkString(" ")
      }

    ZIO.from(
      ValidationError.Message(
        path = error.path,
        error = error.error,
        message = translateMessage(error.message),
      )
    )
  }

}

object I18NExample extends ZIOAppDefault {
  showBuildInfo()

  val i18n: I18N = I18N(
    Map(
      Locale.EN -> Map(
        "NOT_4"            -> "Cannot be equal to 4",
        "greater_error"    -> "Should be greater than",
        "less_equal_error" -> "Should be less or equal to",
        "min_size_error"   -> "Cannot have size less than",
      ),
      Locale.UA -> Map(
        "NOT_4"            -> "Не може дорівнювати 4",
        "greater_error"    -> "Має бути більше за",
        "less_equal_error" -> "Має бути менше або дорівнювати",
        "min_size_error"   -> "Не може мати розмір менший за",
      ),
    )
  )

  def run: Task[Unit] = {
    val blog = Blog(
      List(
        Post(
          4,
          "",
          Some(""),
          LocalDateTime.now,
          LocalDateTime.now.minusDays(1),
        )
      ),
      -1,
    )

    for {
      locale <- ZIO.from(Locale.UA)
      errors <- Field.from(blog).validate.errors.flatMap(i18n.translateAll(locale))
      _      <- printLine(errors.mkString("\n"))
    } yield ()
  }
}
