package jap.fields

import typeclass._

package object data {

  /** FailFast [[jap.fields.typeclass.Validated]] implementation built on top of Option */
  type FailFast[+E] <: FailFast.Type[E]
  object FailFast extends FailFastLike[FailFast] {
    // ----TAGGED---- //
    trait Tag[+E] extends Any
    type Base[+E] = Any { type __FailFast__ }
    type Type[+E] <: Base[E] with Tag[E]

    /** Same as [[FailFast.wrap]] */
    @inline def apply[E](option: Option[E]): FailFast[E] = wrap(option)

    /** Wraps `option` into tagged type */
    @inline def wrap[E](option: Option[E]): FailFast[E] = option.asInstanceOf[FailFast[E]]

    /** Unwraps `failFast` from tagged type */
    @inline def unwrap[E](failFast: FailFast[E]): Option[E] = failFast.asInstanceOf[Option[E]]
    // ----TAGGED---- //
    val Valid                                               = FailFast(None)
    def map[E, B](a: FailFast[E])(f: E => B): FailFast[B]   = FailFast(a.unwrap.map(f))
    def isValid[E](a: FailFast[E]): Boolean                 = a.unwrap.isEmpty
    def and[E](a: FailFast[E], b: FailFast[E]): FailFast[E] = FailFast(a.unwrap.orElse(b.unwrap))
    def errors[E](a: FailFast[E]): List[E]                  = a.unwrap.toList
    def valid[E]: FailFast[E]                               = Valid
    def invalid[E](e: E): FailFast[E]                       = FailFast(Some(e))

    implicit val failFastValidated: Validated[FailFast] = this
    implicit final class FailFastOps[E](private val vr: FailFast[E]) extends AnyVal {
      def unwrap: Option[E]                 = FailFast.unwrap(vr)
      def option: Option[E]                 = unwrap
      def either: Either[E, Unit]           = unwrap.toLeft(())
      def either[R](right: R): Either[E, R] = unwrap.toLeft(right)
    }
  }
}
