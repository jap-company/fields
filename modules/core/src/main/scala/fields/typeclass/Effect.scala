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

import scala.concurrent._

/** [[jap.fields.typeclass.Effect]] is a typeclass that provides ValidationModule with Monad/Defer capabilities. */
trait Effect[F[_]] {

  /** Lifts value into Effect */
  def pure[A](a: A): F[A]

  /** Lazily lifts value into Effect */
  def suspend[A](a: => A): F[A]

  /** Makes effect lazy */
  def defer[A](a: => F[A]): F[A]

  /** FlatMap one effect into another using `f` function */
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

  /** Map effect into another using `f` function */
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

object Effect {

  def apply[F[_]](implicit lm: Effect[F]): Effect[F] = lm

  /** Sync [[jap.fields.typeclass.Effect]]. Short-circuit wont work with it. */
  type Sync[A] = A
  implicit object SyncInstance extends Effect[Sync] {
    def pure[A](a: A): A                   = a
    def flatMap[A, B](fa: A)(f: A => B): B = f(fa)
    def map[A, B](fa: A)(f: A => B): B     = f(fa)
    def suspend[A](a: => A): A             = a
    def defer[A](a: => A): A               = a
  }

  object future {

    /** Requires implicit [[scala.concurrent.ExecutionContext]] in scope */
    implicit def toFutureInstance(implicit ec: ExecutionContext): FutureInstance = new FutureInstance

    /** Future [[jap.fields.typeclass.Effect]]. Sadly Future is not lazy so short-circuit wont work with it, too.
      */
    class FutureInstance(implicit ec: ExecutionContext) extends Effect[Future] {
      def pure[A](a: A): Future[A]                                   = Future.successful(a)
      def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa.flatMap(f)
      def map[A, B](fa: Future[A])(f: A => B): Future[B]             = fa.map(f)
      def defer[A](a: => Future[A]): Future[A]                       = a
      def suspend[A](a: => A): Future[A]                             = Future(a)
    }
  }
}
