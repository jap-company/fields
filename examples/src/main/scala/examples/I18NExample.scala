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
package examples
package i18n

import cats.conversions.variance
import jap.fields.FieldPathConversions.*
import jap.fields.ZioInterop.*
import jap.fields.*
import jap.fields.error.*
import jap.fields.fail.*
import jap.fields.typeclass.*
import zio.*
import zio.console.*

import java.time.*
import java.util.UUID

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
object TranslatedError   {
  import TranslatedMessage._
  implicit object FailWithLocalisedError extends FailWith[TranslatedError, Nothing] {
    def invalid[A](field: Field[A]): TranslatedError =
      TranslatedError(field, ValidationTypes.Invalid, Key(ValidationTypes.Invalid))

    def empty[A](field: Field[A]): TranslatedError =
      TranslatedError(field, ValidationTypes.Empty, Key(ValidationTypes.Empty))

    def nonEmpty[A](field: Field[A]): TranslatedError =
      TranslatedError(field, ValidationTypes.NonEmpty, Key(ValidationTypes.NonEmpty))

    def minSize[A](size: Int)(field: Field[A]): TranslatedError =
      TranslatedError(field, ValidationTypes.MinSize, Sentence(Key(ValidationTypes.MinSize), Pure(size.toString)))

    def maxSize[A](size: Int)(field: Field[A]): TranslatedError =
      TranslatedError(field, ValidationTypes.MaxSize, Sentence(Key(ValidationTypes.MaxSize), Pure(size.toString)))

    def oneOf[A](variants: Seq[A])(field: Field[A]): TranslatedError =
      TranslatedError(
        field,
        ValidationTypes.MaxSize,
        Sentence(Key(ValidationTypes.OneOf), Pure(variants.mkString(","))),
      )

    def message[A](error: String, message: Option[String])(field: Field[A]): TranslatedError =
      TranslatedError(field, error, Key(message.getOrElse(error)))

    def compare[A](operation: CompareOperation, compared: String)(field: Field[A]): TranslatedError =
      TranslatedError(field, operation.constraint, Sentence(Key(operation.constraint), Pure(compared)))
  }
}

object Validation extends AccumulateVM[Task, TranslatedError]
import Validation.*

case class Post(
    id: Long,
    title: String,
    description: Option[String],
    created: LocalDateTime,
    modified: LocalDateTime,
)
object Post:
  given Policy[Post] =
    Policy
      .builder[Post]
      .subRule(_.id)(
        _ > 0L,
        _.ensure(_ != 4L, _.failMessage("NOT_4")),
      )
      .subRule(_.title)(_.minSize(5), _.maxSize(10))
      .subRule(_.description)(_.some(_.all(_.minSize(5), _.maxSize(10))))
      .subRule(_.created, _.modified)(_ <= _)
      .build

case class Blog(posts: List[Post], authorId: Long)
object Blog:
  given Policy[Blog] =
    Policy
      .builder[Blog]
      .subRule(_.authorId)(_ > 0L)
      .subRule(_.posts)(_.each(_.validate))
      .build

enum Locale { case EN, UA }

final case class I18N(locales: Map[Locale, Map[String, String]]) {
  def apply(key: String)(locale: Locale): String = locales(locale)(key)

  def translateAll(locale: Locale)(errors: List[TranslatedError]): Task[List[ValidationError.Message]] =
    Task.collectAll(errors.map(translate(locale)))

  def translate(locale: Locale)(error: TranslatedError): Task[ValidationError.Message] =
    def translateMessage(msg: TranslatedMessage): String =
      msg match {
        case TranslatedMessage.Key(key)        => apply(key)(locale)
        case TranslatedMessage.Pure(value)     => value
        case TranslatedMessage.Sentence(parts) => parts.map(translateMessage).mkString(" ")
      }

    Task(
      ValidationError.Message(
        path = error.path,
        error = error.error,
        message = translateMessage(error.message),
      )
    )
}

object ZIOExample extends zio.App {
  showBuildInfo()

  val i18n: I18N = I18N(
    Map(
      Locale.EN -> Map(
        "NOT_4"            -> "Cannot be equal to 4",
        "GREATER_ERROR"    -> "Should be greater than",
        "LESS_EQUAL_ERROR" -> "Should be less or equal to",
        "MIN_SIZE_ERROR"   -> "Cannot have size less than",
      ),
      Locale.UA -> Map(
        "NOT_4"            -> "Не може дорівнювати 4",
        "GREATER_ERROR"    -> "Має бути більше за",
        "LESS_EQUAL_ERROR" -> "Має бути менше або дорівнювати",
        "MIN_SIZE_ERROR"   -> "Не може мати розмір менший за",
      ),
    )
  )

  def run(args: List[String]): URIO[ZEnv, ExitCode] = {
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

    (
      for {
        locale <- UIO(Locale.UA)
        errors <- Field.from(blog).validate.errors.flatMap(i18n.translateAll(locale))
        _      <- putStrLn(errors.mkString("\n"))
      } yield ()
    ).exitCode
  }
}
