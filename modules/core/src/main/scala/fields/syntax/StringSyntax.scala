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
package syntax

import scala.util.Try
import scala.util.matching.Regex

import typeclass._
import fail._
import jap.fields.error.{ValidationMessages => M, ValidationTypes => T}

trait ModuleStringSyntax[F[_], V[_], E] {
  implicit final def toStringFieldOps(field: Field[String]): StringFieldOps[F, V, E] = new StringFieldOps(field)
}

object StringSyntax extends StringSyntax
trait StringSyntax {
  implicit final def toStringFieldOps[F[_], V[_], E](field: Field[String]): StringFieldOps[F, V, E] =
    new StringFieldOps(field)
}

final class StringFieldOps[F[_], V[_], E](private val field: Field[String]) extends AnyVal {

  /** Validates that [[jap.fields.Field]]#value starts with `value` */
  def startsWith(
      value: => String
  )(implicit F: Effect[F], V: Validated[V], FW: FailWithMessage[E, String]): Rule[F, V, E] =
    Rule.ensure(field.failMessage(T.StringStartsWith, M.StringStartsWith(value)))(field.value.startsWith(value))

  /** Validates that [[jap.fields.Field]]#value ends with `value` */
  def endsWith(
      value: => String
  )(implicit F: Effect[F], V: Validated[V], FW: FailWithMessage[E, String]): Rule[F, V, E] =
    Rule.ensure(field.failMessage(T.StringEndsWith, M.StringEndsWith(value)))(field.value.endsWith(value))

  /** Validates that [[jap.fields.Field]]#value is not empty */
  def nonEmpty(implicit F: Effect[F], V: Validated[V], FW: FailWithNonEmpty[E, String]): Rule[F, V, E] =
    Rule.ensure(field.failNonEmpty)(field.value.nonEmpty)

  /** Validates that [[jap.fields.Field]]#value is not blank */
  def nonBlank(implicit F: Effect[F], V: Validated[V], FW: FailWithNonEmpty[E, String]): Rule[F, V, E] =
    Rule.ensure(field.failNonEmpty)(field.value.nonEmpty)

  /** Validates that [[jap.fields.Field]]#value is blank */
  def blank(implicit F: Effect[F], V: Validated[V], FW: FailWithEmpty[E, String]): Rule[F, V, E] =
    Rule.ensure(field.failEmpty)(field.value.isEmpty)

  /** Validates that [[jap.fields.Field]]#value minimum size is `min` */
  def minSize(min: => Int)(implicit F: Effect[F], V: Validated[V], FW: FailWithMinSize[E, String]): Rule[F, V, E] =
    Rule.ensure(field.failMinSize(min))(field.value.size >= min)

  /** Validates that [[jap.fields.Field]]#value maximum size is `max` */
  def maxSize(max: => Int)(implicit F: Effect[F], V: Validated[V], FW: FailWithMaxSize[E, String]): Rule[F, V, E] =
    Rule.ensure(field.failMaxSize(max))(field.value.size <= max)

  /** Validates that [[jap.fields.Field]]#value matches Regexp */
  def matches(r: => String)(implicit F: Effect[F], V: Validated[V], FW: FailWithMessage[E, String]): Rule[F, V, E] =
    Rule.ensure(field.failMessage(T.StringMatch, M.StringMatch(r)))(field.value.matches(r))

  /** Validates that [[jap.fields.Field]]#value is matches [[scala.util.matching.Regex]] */
  def matchesRegex(r: => Regex)(implicit F: Effect[F], V: Validated[V], FW: FailWithMessage[E, String]): Rule[F, V, E] =
    matches(r.regex)

  /** Validates that [[jap.fields.Field]]#value is part of [[scala.Enumeration]] */
  def isEnum(e: Enumeration)(implicit F: Effect[F], V: Validated[V], FW: FailWithOneOf[E, String]): Rule[F, V, E] =
    Rule.ensure(field.failOneOf(e.values.map(_.toString).toList)) {
      Try(e.withName(field.value)).toOption.isDefined
    }

  /** Validates that [[jap.fields.Field]]#value is part of Java Enum */
  def isJEnum[T <: Enum[T]](
      values: Array[T]
  )(implicit F: Effect[F], V: Validated[V], FW: FailWithOneOf[E, String]): Rule[F, V, E] =
    Rule.ensure(field.failOneOf(values.toSeq.map(_.toString))) {
      values.map(_.name()).contains(field.value)
    }

}
