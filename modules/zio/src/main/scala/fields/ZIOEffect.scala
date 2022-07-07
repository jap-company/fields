package jap.fields

import zio._
import typeclass._

private class ZIOEffect[R, E] extends Effect[ZIO[R, E, _]] {
  type F[A] = ZIO[R, E, A]
  def pure[A](a: A): F[A]                         = UIO(a)
  def suspend[A](a: => A): F[A]                   = UIO(a)
  def defer[A](a: => F[A]): F[A]                  = ZIO.effectSuspendTotal(a)
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B] = fa.flatMap(f)
  def map[A, B](fa: F[A])(f: A => B): F[B]        = fa.map(f)
}
