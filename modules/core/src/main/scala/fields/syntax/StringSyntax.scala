package jap.fields
package syntax

import scala.util.Try
import scala.util.matching.Regex

trait StringSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toStringFieldOps(field: Field[String]): StringFieldOps[F, VR, E] = new StringFieldOps(field)
}

final class StringFieldOps[F[_], VR[_], E](private val field: Field[String]) extends AnyVal {
  def startsWith(value: String)(implicit M: ValidationModule[F, VR, E], CF: CanFailMessage[E]) =
    M.assert[String](field, _.startsWith(value), CF.message("starts-with", Some(s"should start with $value")))

  def endsWith(value: String)(implicit M: ValidationModule[F, VR, E], CF: CanFailMessage[E]) =
    M.assert[String](field, _.endsWith(value), CF.message("ends-with", Some(s"should end with $value")))

  def nonEmpty(implicit M: ValidationModule[F, VR, E], CF: CanFailEmpty[E]) =
    M.assert[String](field, _.nonEmpty, CF.empty)

  def nonBlank(implicit M: ValidationModule[F, VR, E], CF: CanFailEmpty[E]) =
    M.assert[String](field, _.nonEmpty, CF.empty)

  def min(min: Int)(implicit M: ValidationModule[F, VR, E], CF: CanFailMinSize[E]) =
    M.assert[String](field, _.size >= min, CF.minSize(min))

  def max(max: Int)(implicit M: ValidationModule[F, VR, E], CF: CanFailMaxSize[E]) =
    M.assert[String](field, _.size <= max, CF.maxSize(max))

  def blank(implicit M: ValidationModule[F, VR, E], CF: CanFailNonEmpty[E]) =
    M.assert[String](field, _.nonEmpty, CF.nonEmpty)

  def matches(r: String)(implicit M: ValidationModule[F, VR, E], CF: CanFailMessage[E]): F[VR[E]] =
    M.assert[String](field, _.matches(r), CF.message("match", Some(s"${field.fullPath} should match $r")))

  def matches(r: Regex)(implicit M: ValidationModule[F, VR, E], CF: CanFailMessage[E]): F[VR[E]] =
    matches(r.regex)

  def isEnum(e: Enumeration)(implicit M: ValidationModule[F, VR, E], CF: CanFailOneOf[E]) =
    M.assert[String](field, v => Try(e.withName(v)).toOption.isDefined, CF.oneOf(e.values.map(_.toString).toList))

  def isJEnum[T <: Enum[T]](values: Array[T])(implicit M: ValidationModule[F, VR, E], CF: CanFailOneOf[E]) =
    M.assert[String](field, values.map(_.name()).contains(_), CF.oneOf(values.map(_.toString)))

}
