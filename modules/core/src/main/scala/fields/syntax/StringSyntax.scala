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

trait StringSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toStringFieldOps(field: Field[String]): StringFieldOps[F, VR, E] = new StringFieldOps(field)
}

final class StringFieldOps[F[_], VR[_], E](private val field: Field[String]) extends AnyVal {

  /** Validates that [[jap.fields.Field]]#value starts with `value` */
  def startsWith(value: String)(implicit M: ValidationModule[F, VR, E], FW: FailWithMessage[E]) =
    M.fieldAssert[String](
      field,
      _.startsWith(value),
      FW.message("starts-with", Some(s"should start with $value")),
    )

  /** Validates that [[jap.fields.Field]]#value ends with `value` */
  def endsWith(value: String)(implicit M: ValidationModule[F, VR, E], FW: FailWithMessage[E]) =
    M.fieldAssert[String](
      field,
      _.endsWith(value),
      FW.message("ends-with", Some(s"should end with $value")),
    )

  /** Validates that [[jap.fields.Field]]#value is not empty */
  def nonEmpty(implicit M: ValidationModule[F, VR, E], FW: FailWithEmpty[E]) =
    M.fieldAssert[String](field, _.nonEmpty, FW.empty)

  /** Validates that [[jap.fields.Field]]#value is not blank */
  def nonBlank(implicit M: ValidationModule[F, VR, E], FW: FailWithEmpty[E]) =
    M.fieldAssert[String](field, _.nonEmpty, FW.empty)

  /** Validates that [[jap.fields.Field]]#value minimum size is `min` */
  def minSize(min: Int)(implicit M: ValidationModule[F, VR, E], FW: FailWithMinSize[E]) =
    M.fieldAssert[String](field, _.size >= min, FW.minSize(min))

  /** Validates that [[jap.fields.Field]]#value maximum size is `max` */
  def maxSize(max: Int)(implicit M: ValidationModule[F, VR, E], FW: FailWithMaxSize[E]) =
    M.fieldAssert[String](field, _.size <= max, FW.maxSize(max))

  /** Validates that [[jap.fields.Field]]#value is blank */
  def blank(implicit M: ValidationModule[F, VR, E], FW: FailWithNonEmpty[E]) =
    M.fieldAssert[String](field, _.nonEmpty, FW.nonEmpty)

  /** Validates that [[jap.fields.Field]]#value matches Regexp */
  def matches(r: String)(implicit M: ValidationModule[F, VR, E], FW: FailWithMessage[E]): F[VR[E]] =
    M.fieldAssert[String](
      field,
      _.matches(r),
      FW.message("match", Some(s"${field.fullPath} should match $r")),
    )

  /** Validates that [[jap.fields.Field]]#value is matches [[scala.util.matching.Regex]] */
  def matches(r: Regex)(implicit M: ValidationModule[F, VR, E], FW: FailWithMessage[E]): F[VR[E]] =
    matches(r.regex)

  /** Validates that [[jap.fields.Field]]#value is part of [[scala.Enumeration]] */
  def isEnum(e: Enumeration)(implicit M: ValidationModule[F, VR, E], FW: FailWithOneOf[E]) =
    M.fieldAssert[String](
      field,
      v => Try(e.withName(v)).toOption.isDefined,
      FW.oneOf(e.values.map(_.toString).toList),
    )

  /** Validates that [[jap.fields.Field]]#value is part of Java Enum */
  def isJEnum[T <: Enum[T]](values: Array[T])(implicit M: ValidationModule[F, VR, E], FW: FailWithOneOf[E]) =
    M.fieldAssert[String](field, values.map(_.name()).contains(_), FW.oneOf(values.toSeq.map(_.toString)))

}
