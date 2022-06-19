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

import typeclass.Validated

object ValidatedSyntax extends ValidatedSyntax
trait ValidatedSyntax {
  implicit final def toValidatedIdOps[E](error: E): ValidatedIdOps[E]      = new ValidatedIdOps(error)
  implicit final def toValidatedOps[V[_], E](vr: V[E]): ValidatedOps[V, E] = new ValidatedOps(vr)
  implicit final def toValidatedSeqOps[V[_], E](iterable: Iterable[V[E]]): ValidatedSeqOps[V, E] =
    new ValidatedSeqOps(iterable)
}

final class ValidatedSeqOps[V[_], E](private val iterable: Iterable[V[E]]) extends AnyVal {

  /** See [[jap.fields.typeclass.Validated.sequence]] */
  def sequence(implicit V: Validated[V]): V[E] = V.sequence(iterable.toList)

  /** See [[jap.fields.typeclass.Validated.andAll]] */
  def andAll(implicit V: Validated[V]): V[E] = V.andAll(iterable.toList)

  /** See [[jap.fields.typeclass.Validated.orAll]] */
  def orAll(implicit V: Validated[V]): V[E] = V.orAll(iterable.toList)
}

final class ValidatedIdOps[E](private val error: E) extends AnyVal {
  def invalid[V[_]](implicit V: Validated[V]): V[E] = V.invalid(error)
}

final class ValidatedOps[V[_], E](private val vr: V[E]) extends AnyVal {

  /** See [[jap.fields.typeclass.Validated.isInvalid]] */
  def isInvalid(implicit V: Validated[V]): Boolean = V.isInvalid(vr)

  /** See [[jap.fields.typeclass.Validated.isValid]] */
  def isValid(implicit V: Validated[V]): Boolean = V.isValid(vr)

  /** See [[jap.fields.typeclass.Validated.and]] */
  def and(that: V[E])(implicit V: Validated[V]): V[E] = V.and(vr, that)

  /** See [[jap.fields.typeclass.Validated.and]] */
  def &&(that: V[E])(implicit V: Validated[V]): V[E] = V.and(vr, that)

  /** See [[jap.fields.typeclass.Validated.or]] */
  def or(that: V[E])(implicit V: Validated[V]): V[E] = V.or(vr, that)

  /** See [[jap.fields.typeclass.Validated.or]] */
  def ||(that: V[E])(implicit V: Validated[V]): V[E] = V.or(vr, that)

  /** See [[jap.fields.typeclass.Validated.errors]] */
  def errors(implicit V: Validated[V]): List[E] = V.errors(vr)

  /** See [[jap.fields.typeclass.Validated.when]] */
  def when(cond: Boolean)(implicit V: Validated[V]): V[E] = V.when(cond)(vr)

  /** See [[jap.fields.typeclass.Validated.whenValid]] */
  def whenValid(b: => V[E])(implicit V: Validated[V]): V[E] = V.whenValid(vr)(b)

  /** See [[jap.fields.typeclass.Validated.whenValid]] */
  def whenInvalid(f: V[E] => V[E])(implicit V: Validated[V]): V[E] = V.whenInvalid(vr)(f)

  /** See [[jap.fields.typeclass.Validated.unless]] */
  def unless(cond: Boolean)(implicit V: Validated[V]): V[E] = V.unless(cond)(vr)

  /** See [[jap.fields.typeclass.Validated.asError]] */
  def asError(error: E)(implicit V: Validated[V]) = V.asError(vr)(error)

  /** See [[jap.fields.typeclass.Validated.asError]] */
  def asInvalid(invalid: V[E])(implicit V: Validated[V]) = V.asInvalid(vr)(invalid)

}
