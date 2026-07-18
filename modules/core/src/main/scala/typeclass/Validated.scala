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

package fields
package typeclass

/** Base trait for [[fields.typeclass.Validated]] that accumulate errors */
/** This strategy will accumulate all errors that occur */
trait AccumulateLike[V[_]] extends Validated[V]

/** Base trait for [[fields.typeclass.Validated]] that fail-fast */
/** When first error occur validation will short-circuit and will not execute other validations */
trait FailFastLike[V[_]] extends Validated[V]

/** Typeclass that represents Validated */
sealed trait Validated[V[_]] {

  /** Returns valid V[E] */
  def valid[E]: V[E]

  /** Returns invalid V[E] with given error */
  def invalid[E](e: E): V[E]

  /** Checks if `v` is valid */
  def isValid[E](v: V[E]): Boolean

  def map[E](v: V[E])(f: E => E): V[E]

  /** Combines `a` and `b` using AND */
  def and[E](va: V[E], vb: V[E]): V[E]

  /** Combines all errors in single result */
  def invalidAll[E](list: List[E]): V[E] = traverse(list)(invalid)

  /** Combines all errors in single result */
  def invalidAll[E](list: E*): V[E] = traverse(list.toList)(invalid)

  /** Checks if `v` is invalid */
  def isInvalid[E](v: V[E]): Boolean = !isValid(v)

  /** Combines `a` and `b` using OR */
  def or[E](va: V[E], vb: V[E]): V[E] = if (isValid(va) || isValid(vb)) valid else and(va, vb)

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

  /** `Validated.andAll` alias */
  def sequence[E](list: List[V[E]]): V[E] = andAll[E](list)

  /** Combines all `list` using AND from left to right. */
  def andAll[E](list: List[V[E]]): V[E] =
    list match {
      case Nil          => valid[E]
      case head :: tail => tail.foldLeft(head)(and[E])
    }

  /** Combines all `list` using OR from left to right. */
  def orAll[E](list: List[V[E]]): V[E] =
    list match {
      case Nil          => valid[E]
      case head :: tail => tail.foldLeft(head)(or[E])
    }

  /** Traverse over `list` applying `f` to each element and combining all using `Validated.sequence` */
  def traverse[A, E](list: List[A])(f: A => V[E]): V[E] = sequence(list.map(f))

  /** Traverse over `list` applying `f` to each element and combining all using `Validated.sequence` */
  def traverse[A, E](list: A*)(f: A => V[E]): V[E] = sequence(list.toList.map(f))
}

object Validated {

  /** Returns [[fields.typeclass.Validated]] instance for given V */
  def apply[V[_]](implicit V: Validated[V]): Validated[V] = V

  implicit object ListValidated extends AccumulateLike[List] {
    def valid[E]: List[E]                         = Nil
    def invalid[E](e: E): List[E]                 = List(e)
    def and[E](va: List[E], vb: List[E]): List[E] = va ::: vb
    def isValid[E](v: List[E]): Boolean           = v.isEmpty
    def map[E](v: List[E])(f: E => E): List[E]    = v.map(f)
  }
  val Accumulate: ListValidated.type = ListValidated

  implicit object EitherUnitValidated extends FailFastLike[Either[_, Unit]] {
    def valid[E]: Either[E, Unit]                                         = Right(())
    def invalid[E](e: E): Either[E, Unit]                                 = Left(e)
    def and[E](va: Either[E, Unit], vb: Either[E, Unit]): Either[E, Unit] = va.flatMap(_ => vb)
    def isValid[E](v: Either[E, Unit]): Boolean                           = v.isRight
    def map[E](v: Either[E, Unit])(f: E => E): Either[E, Unit]            = v.left.map(f)
  }

  implicit object OptionValidated extends FailFastLike[Option] {
    def valid[E]: Option[E]                             = None
    def invalid[E](e: E): Option[E]                     = Some(e)
    def and[E](va: Option[E], vb: Option[E]): Option[E] = va.orElse(vb)
    def isValid[E](v: Option[E]): Boolean               = v.isEmpty
    def map[E](v: Option[E])(f: E => E): Option[E]      = v.map(f)
  }
  val FailFast: OptionValidated.type = OptionValidated
}
