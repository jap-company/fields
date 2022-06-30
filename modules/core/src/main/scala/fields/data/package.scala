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
    def valid[E]: FailFast[E]                               = Valid
    def invalid[E](e: E): FailFast[E]                       = FailFast(Some(e))
    def isValid[E](v: FailFast[E]): Boolean                 = v.option.isEmpty
    def and[E](a: FailFast[E], b: FailFast[E]): FailFast[E] = FailFast(a.unwrap.orElse(b.unwrap))

    implicit val failFastValidated: Validated[FailFast] = this
    implicit final class FailFastOps[E](private val vr: FailFast[E]) extends AnyVal {
      def unwrap: Option[E]                 = FailFast.unwrap(vr)
      def option: Option[E]                 = unwrap
      def either: Either[E, Unit]           = unwrap.toLeft(())
      def either[R](right: R): Either[E, R] = unwrap.toLeft(right)
    }
  }
}
