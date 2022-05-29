package jap.fields
package syntax

import scala.util.Try
import scala.util.matching.Regex

trait StringSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toStringFieldOps(field: Field[String]): StringFieldOps[F, VR, E] = new StringFieldOps(field)
}

final class StringFieldOps[F[_], VR[_], E](private val field: Field[String]) extends AnyVal {
  def startsWith(value: String)(implicit M: ValidationModule[F, VR, E]) =
    M.assert[String](field, _.startsWith(value), _.custom("starts-with", Some(s"should start with $value")))

  def endsWith(value: String)(implicit M: ValidationModule[F, VR, E]) =
    M.assert[String](field, _.endsWith(value), _.custom("ends-with", Some(s"should end with $value")))

  def nonEmpty(implicit M: ValidationModule[F, VR, E]) =
    M.assert[String](field, _.nonEmpty, _.empty)

  def nonBlank(implicit M: ValidationModule[F, VR, E]) =
    M.assert[String](field, _.nonEmpty, _.empty)

  def min(min: Int)(implicit M: ValidationModule[F, VR, E]) =
    M.assert[String](field, _.size >= min, _.minSize(min))

  def max(max: Int)(implicit M: ValidationModule[F, VR, E]) =
    M.assert[String](field, _.size <= max, _.maxSize(max))

  def blank(implicit M: ValidationModule[F, VR, E]) =
    M.assert[String](field, _.nonEmpty, _.nonEmpty)

  def matches(r: String)(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.assert[String](field, _.matches(r), _.custom("match", Some(s"${field.fullPath} should match $r")))

  def matches(r: Regex)(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    matches(r.regex)

  def isEnum(e: Enumeration)(implicit M: ValidationModule[F, VR, E]) =
    M.assert[String](field, v => Try(e.withName(v)).toOption.isDefined, _.oneOf(e.values.map(_.toString).toList))

  def isJEnum[T <: Enum[T]](values: Array[T])(implicit M: ValidationModule[F, VR, E]) =
    M.assert[String](field, values.map(_.name()).contains(_), _.oneOf(values.map(_.toString)))

}
