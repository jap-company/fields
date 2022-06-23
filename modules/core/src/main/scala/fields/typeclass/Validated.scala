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
package typeclass

/** Base trait for [[jap.fields.typeclass.Validated]] that accumulate errors */
trait AccumulateLike[V[_]] extends Validated[V] { val strategy: ValidatedStrategy = AccumulateStrategy }

/** Base trait for [[jap.fields.typeclass.Validated]] that fail-fast */
trait FailFastLike[V[_]] extends Validated[V] { val strategy: ValidatedStrategy = FailFastStrategy }

/** Typeclass that represents Validated */
trait Validated[V[_]] {
  type TypeClass[E] = V[E]

  /** Returns this Strategy */
  def strategy: ValidatedStrategy

  /** Returns valid V[E] */
  def valid[E]: V[E]

  /** Returns invalid V[E] with given error */
  def invalid[E](e: E): V[E]

  /** Checks if `v` is valid */
  def isValid[E](v: V[E]): Boolean

  /** Checks if `v` is invalid */
  def isInvalid[E](v: V[E]): Boolean = !isValid(v)

  /** Returns `v` errors as [[scala.collection.immutable.List]] */
  def errors[E](v: V[E]): List[E]

  /** Combines `a` and `b` using AND */
  def and[E](va: V[E], vb: V[E]): V[E]

  /** Combines `a` and `b` using OR */
  def or[E](va: V[E], vb: V[E]): V[E] = if (isValid(va) || isValid(vb)) valid else and(va, vb)

  /** Folds `V[E]` into `B` using provided functions for valid and invalid cases */
  def fold[E, B](v: V[E])(fe: V[E] => B, fa: => B) = if (isValid(v)) fa else fe(v)

  /** Maps `v` using `f` function */
  def map[E, B](v: V[E])(f: E => B): V[B]

  /** Returns `error` if `v` is invalid */
  def asError[E](v: V[E])(error: E): V[E] = whenInvalid[E](v)(_ => invalid(error))

  /** Returns `invalid` if `v` is invalid */
  def asInvalid[E](v: V[E])(invalid: V[E]): V[E] = whenInvalid[E](v)(_ => invalid)

  /** Returns `o` if `a` is valid else returns `a` */
  def whenValid[E](va: V[E])(vb: => V[E]): V[E] = if (isValid(va)) vb else va

  /** Returns `f` applied to `a` if `a` is invalid else returns `a` */
  def whenInvalid[E](v: V[E])(f: V[E] => V[E]): V[E] = if (isInvalid(v)) f(v) else v

  /** Returns `v` if `cond` is true else returns valid */
  def when[E](cond: Boolean)(v: => V[E]): V[E] = if (cond) v else valid[E]

  /** Returns `v` if `cond` is false else returns valid */
  def unless[E](cond: Boolean)(v: => V[E]): V[E] = if (cond) valid[E] else v

  /** Combiness all `list` using AND */
  def sequence[E](list: List[V[E]]): V[E] = andAll[E](list)

  /** Combiness all `list` using AND */
  def andAll[E](list: List[V[E]]): V[E] = FoldUtil.fold[V[E]](list, valid[E], and[E])

  /** Combiness all `list` using OR */
  def orAll[E](list: List[V[E]]): V[E] = FoldUtil.fold[V[E]](list, valid[E], or[E])

  def traverse[A, E](list: List[A])(error: A => V[E]): V[E] =
    sequence(list.map(error))

  def traverse[A, E](list: A*)(error: A => V[E]): V[E] =
    sequence(list.toList.map(error))
}

object Validated {

  /** Returns [[jap.fields.typeclass.Validated]] instance for given V */
  def apply[V[_]](implicit V: Validated[V]): Validated[V] = V

  implicit object ListValidated extends AccumulateLike[List] {
    def map[E, B](v: List[E])(f: E => B): List[B] = v.map(f)
    def isValid[E](v: List[E]): Boolean           = v.isEmpty
    def valid[E]: List[E]                         = Nil
    def errors[E](v: List[E]): List[E]            = v
    def invalid[E](e: E): List[E]                 = List(e)
    def and[E](va: List[E], vb: List[E]): List[E] = va ++ vb
  }

  implicit object EitherUnitValidated extends FailFastLike[Either[_, Unit]] {
    def valid[E]: Either[E, Unit]                                         = Right(())
    def invalid[E](e: E): Either[E, Unit]                                 = Left(e)
    def isValid[E](v: Either[E, Unit]): Boolean                           = v.isRight
    def and[E](va: Either[E, Unit], vb: Either[E, Unit]): Either[E, Unit] = va.flatMap(_ => vb)
    def errors[E](v: Either[E, Unit]): List[E]                            = v.left.toSeq.toList
    def map[E, B](v: Either[E, Unit])(f: E => B): Either[B, Unit]         = v.left.map(f)
  }

  implicit object OptionValidated extends FailFastLike[Option] {
    def map[E, B](v: Option[E])(f: E => B): Option[B]   = v.map(f)
    def isValid[E](v: Option[E]): Boolean               = v.isEmpty
    def valid[E]: Option[E]                             = None
    def invalid[E](e: E): Option[E]                     = Some(e)
    def errors[E](v: Option[E]): List[E]                = v.toList
    def and[E](va: Option[E], vb: Option[E]): Option[E] = va.orElse(vb)
  }
}

/** Will change behaviour of combining Validated's depending on strategy */
sealed trait ValidatedStrategy

/** This strategy will accumulate all errors that occur */
case object AccumulateStrategy extends ValidatedStrategy

/** When first error occur validation will short-circuit and will not execute other validations */
case object FailFastStrategy extends ValidatedStrategy
