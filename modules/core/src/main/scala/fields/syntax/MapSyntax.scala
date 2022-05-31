package jap.fields
package syntax

import scala.concurrent.Future

import ValidationResult._

trait MapSyntax[F[_], VR[_], E] { M: ValidationModule[F, VR, E] =>
  implicit final def toMapFieldOps[K, V](field: Field[Map[K, V]]): MapFieldOps[K, V, F, VR, E] =
    new MapFieldOps(field)
}

final class MapFieldOps[K, V, F[_], VR[_], E](private val field: Field[Map[K, V]]) extends AnyVal {
  def each(f: Field[(K, V)] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    M.combineAll(
      field.value.zipWithIndex.map { case (t, i) => f(field.provideSub(i.toString, t)) }.toList
    )

  def eachKey(f: Field[K] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    each(e => f(e.first))

  def eachValue(f: Field[V] => F[VR[E]])(implicit M: ValidationModule[F, VR, E]): F[VR[E]] =
    each(e => f(e.second))
}
