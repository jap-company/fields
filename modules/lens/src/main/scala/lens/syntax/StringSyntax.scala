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
package lens
package syntax

import error.{ValidationMessages as M, ValidationTypes as T}
import fail.*
import lens.syntax.GenericSyntax.*

import scala.util.Try
import scala.util.matching.Regex

object StringSyntax extends StringSyntax
trait StringSyntax {
  implicit final def toStringFieldOps[P, F[_], V[_], E](field: FieldLens[P, String]): StringFieldOps[P, F, V, E] =
    new StringFieldOps(field)
}

trait StringDsl[F[_], V[_], E] {
  implicit final def toStringFieldOps[P](field: FieldLens[P, String]): StringFieldOps[P, F, V, E] =
    new StringFieldOps(field)
}
final class StringFieldOps[P, F[_], V[_], E](private val field: FieldLens[P, String]) extends AnyVal {

  /** Validates that [[fields.Field]]#value starts with `value` */
  def startsWith(value: String)(implicit R: RuleOps[F, V, E], FW: FailWithMessage[E, String]): PolicyK[P, F, V, E] =
    field.assert(_.startsWith(value), FW.description(T.StringStartsWith, M.StringStartsWith(value)))

  /** Validates that [[fields.Field]]#value ends with `value` */
  def endsWith(value: String)(implicit R: RuleOps[F, V, E], FW: FailWithMessage[E, String]): PolicyK[P, F, V, E] =
    field.assert(_.endsWith(value), FW.description(T.StringEndsWith, M.StringEndsWith(value)))

  /** Validates that [[fields.Field]]#value is not empty */
  def nonEmpty(implicit R: RuleOps[F, V, E], FW: FailWithNonEmpty[E, String]): PolicyK[P, F, V, E] =
    field.assert(_.nonEmpty, FW.nonEmpty)

  /** Validates that [[fields.Field]]#value is not blank */
  def nonBlank(implicit R: RuleOps[F, V, E], FW: FailWithNonEmpty[E, String]): PolicyK[P, F, V, E] =
    field.assert(_.nonEmpty, FW.nonEmpty)

  /** Validates that [[fields.Field]]#value is blank */
  def blank(implicit R: RuleOps[F, V, E], FW: FailWithEmpty[E, String]): PolicyK[P, F, V, E] =
    field.assert(_.isEmpty, FW.empty)

  /** Validates that [[fields.Field]]#value minimum size is `min` */
  def minSize(min: Int)(implicit R: RuleOps[F, V, E], FW: FailWithMinSize[E, String]): PolicyK[P, F, V, E] =
    field.assert(_.length >= min, FW.minSize(min))

  /** Validates that [[fields.Field]]#value maximum size is `max` */
  def maxSize(max: Int)(implicit R: RuleOps[F, V, E], FW: FailWithMaxSize[E, String]): PolicyK[P, F, V, E] =
    field.assert(_.length <= max, FW.maxSize(max))

  /** Validates that [[fields.Field]]#value matches Regexp */
  def matches(r: String)(implicit R: RuleOps[F, V, E], FW: FailWithMessage[E, String]): PolicyK[P, F, V, E] =
    field.assert(_.matches(r), FW.description(T.StringMatch, M.StringMatch(r)))

  /** Validates that [[fields.Field]]#value is matches [[scala.util.matching.Regex]] */
  def matchesRegex(r: Regex)(implicit R: RuleOps[F, V, E], FW: FailWithMessage[E, String]): PolicyK[P, F, V, E] =
    matches(r.regex)

  /** Validates that [[fields.Field]]#value is part of [[scala.Enumeration]] */
  def isEnum(e: Enumeration)(implicit R: RuleOps[F, V, E], FW: FailWithOneOf[E, String]): PolicyK[P, F, V, E] =
    field.assert(
      v => Try(e.withName(v)).toOption.isDefined,
      FW.oneOf(e.values.map(_.toString).toList),
    )

  /** Validates that [[fields.Field]]#value is part of Java Enum */
  def isJEnum[T <: Enum[T]](
      values: Array[T]
  )(implicit R: RuleOps[F, V, E], FW: FailWithOneOf[E, String]): PolicyK[P, F, V, E] =
    field.assert(value => values.exists(_.name() == value), FW.oneOf(values.toSeq.map(_.toString)))

}
