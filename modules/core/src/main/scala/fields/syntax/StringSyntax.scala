package jap.fields
package syntax

import scala.util.Try
import scala.util.matching.Regex

trait StringSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toStringFieldOps(field: Field[String]): StringFieldOps[F, VR, E] = new StringFieldOps(field)
}

final class StringFieldOps[F[_], VR[_], E](private val field: Field[String]) extends AnyVal {

  /** Validates that [[Field]]#value starts with `value` */
  def startsWith(value: String)(implicit M: ValidationModule[F, VR, E], FW: FailWithMessage[E]) =
    M.assert[String](field, _.startsWith(value), FW.message("starts-with", Some(s"should start with $value")))

  /** Validates that [[Field]]#value ends with `value` */
  def endsWith(value: String)(implicit M: ValidationModule[F, VR, E], FW: FailWithMessage[E]) =
    M.assert[String](field, _.endsWith(value), FW.message("ends-with", Some(s"should end with $value")))

  /** Validates that [[Field]]#value is not empty */
  def nonEmpty(implicit M: ValidationModule[F, VR, E], FW: FailWithEmpty[E]) =
    M.assert[String](field, _.nonEmpty, FW.empty)

  /** Validates that [[Field]]#value is not blank */
  def nonBlank(implicit M: ValidationModule[F, VR, E], FW: FailWithEmpty[E]) =
    M.assert[String](field, _.nonEmpty, FW.empty)

  /** Validates that [[Field]]#value minimum size is `min` */
  def minSize(min: Int)(implicit M: ValidationModule[F, VR, E], FW: FailWithMinSize[E]) =
    M.assert[String](field, _.size >= min, FW.minSize(min))

  /** Validates that [[Field]]#value maximum size is `max` */
  def maxSize(max: Int)(implicit M: ValidationModule[F, VR, E], FW: FailWithMaxSize[E]) =
    M.assert[String](field, _.size <= max, FW.maxSize(max))

  /** Validates that [[Field]]#value is blank */
  def blank(implicit M: ValidationModule[F, VR, E], FW: FailWithNonEmpty[E]) =
    M.assert[String](field, _.nonEmpty, FW.nonEmpty)

  /** Validates that [[Field]]#value matches Regexp */
  def matches(r: String)(implicit M: ValidationModule[F, VR, E], FW: FailWithMessage[E]): F[VR[E]] =
    M.assert[String](field, _.matches(r), FW.message("match", Some(s"${field.fullPath} should match $r")))

  /** Validates that [[Field]]#value is matches [[scala.util.matching.Regex]] */
  def matches(r: Regex)(implicit M: ValidationModule[F, VR, E], FW: FailWithMessage[E]): F[VR[E]] =
    matches(r.regex)

  /** Validates that [[Field]]#value is part of [[scala.Enumeration]] */
  def isEnum(e: Enumeration)(implicit M: ValidationModule[F, VR, E], FW: FailWithOneOf[E]) =
    M.assert[String](field, v => Try(e.withName(v)).toOption.isDefined, FW.oneOf(e.values.map(_.toString).toList))

  /** Validates that [[Field]]#value is part of Java Enum */
  def isJEnum[T <: Enum[T]](values: Array[T])(implicit M: ValidationModule[F, VR, E], FW: FailWithOneOf[E]) =
    M.assert[String](field, values.map(_.name()).contains(_), FW.oneOf(values.map(_.toString)))

}
