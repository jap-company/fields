package jap.fields
package syntax

import scala.concurrent.Future

import ValidationResult._

trait MapSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toMapFieldOps[K, V](field: Field[Map[K, V]]): MapFieldOps[K, V, F, VR, E] =
    new MapFieldOps(field)
}

final class MapFieldOps[K, V, F[_], VR[_], E](private val field: Field[Map[K, V]]) extends AnyVal {

  /** Applies `check` to each Map element, each should succeed */
  def each(f: Field[(K, V)] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.and(field.value.zipWithIndex.map { case (t, i) => f(field.provideSub(i.toString, t)) }.toList)

  /** Applies `check` to each Map key, each should succeed */
  def eachKey(check: Field[K] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    each(e => check(e.first))

  /** Applies `check` to each Map value, each should succeed */
  def eachValue(check: Field[V] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    each(e => check(e.second))

  /** Applies `check` to each Map element, any should succeed */
  def any(check: Field[(K, V)] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.or(field.value.zipWithIndex.map { case (t, i) => check(field.provideSub(i.toString, t)) }.toList)

  /** Applies `check` to each Map key, any should succeed */
  def anyKey(check: Field[K] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    any(e => check(e.first))

  /** Applies `check` to each Map value, any should succeed */
  def anyValue(check: Field[V] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    any(e => check(e.second))
}
