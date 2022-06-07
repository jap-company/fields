package jap.fields

import scala.annotation.implicitNotFound

/** Typeclass that is used to allow using compare syntax with both Field[P] and P itself. Not needed in case Scala 2
  * support will be dropped in future.
  */
@implicitNotFound("Cannot compare ${P} with ${C}")
trait FieldCompare[P, C] {
  def value(c: C): P
  def show(compared: C): String
}
object FieldCompare      {
  def apply[P, C](implicit C: FieldCompare[P, C]): FieldCompare[P, C] = C

  implicit def valueWithValueCompare[P]: FieldCompare[P, P] =
    anyDefaultCompared.asInstanceOf[FieldCompare[P, P]]

  implicit def fieldWithFieldCompare[P]: FieldCompare[P, Field[P]] =
    anyFieldCompared.asInstanceOf[FieldCompare[P, Field[P]]]

  private val anyDefaultCompared: FieldCompare[Any, Any] = new FieldCompare[Any, Any] {
    def value(c: Any): Any   = c
    def show(c: Any): String = c.toString
  }

  private val anyFieldCompared: FieldCompare[Any, Field[Any]] = new FieldCompare[Any, Field[Any]] {
    def value(c: Field[Any]): Any   = c.value
    def show(c: Field[Any]): String = c.path.full
  }
}
