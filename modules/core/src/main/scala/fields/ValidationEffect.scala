package jap.fields

import scala.concurrent._

trait ValidationEffect[F[_]] {
  def pure[A](a: A): F[A]
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
  def suspend[A](a: => A): F[A]
  def defer[A](a: => F[A]): F[A]
  def map[A, B](fa: F[A])(f: A => B): F[B]
  def map2[A, B, C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C] = flatMap(fa)(a => map(fb)(b => f(a, b)))
}

object ValidationEffect {
  type Id[A] = A

  def apply[F[_]](implicit lm: ValidationEffect[F]): ValidationEffect[F] = lm

  implicit object IdInstance extends ValidationEffect[Id] {
    def pure[A](a: A): A                   = a
    def flatMap[A, B](fa: A)(f: A => B): B = f(fa)
    def map[A, B](fa: A)(f: A => B): B     = f(fa)
    def suspend[A](a: => A): A             = a
    def defer[A](a: => A): A               = a
  }

  object future {
    implicit def toFutureInstance(implicit ec: ExecutionContext): FutureInstance = new FutureInstance

    class FutureInstance(implicit ec: ExecutionContext) extends ValidationEffect[Future] {
      def pure[A](a: A): Future[A]                                   = Future.successful(a)
      def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa.flatMap(f)
      def map[A, B](fa: Future[A])(f: A => B): Future[B]             = fa.map(f)
      def defer[A](a: => Future[A]): Future[A]                       = a
      def suspend[A](a: => A): Future[A]                             = Future(a)
    }
  }
}
