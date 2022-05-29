package jap.fields

import zio._

object ZIOInterop {
  implicit def fromZIO[R, E]: ValidationEffect[ZIO[R, E, _]] = new ValidationEffect[ZIO[R, E, _]] {
    def pure[A](a: A): ZIO[R, E, A]                                         = UIO(a)
    def suspend[A](a: => A): ZIO[R, E, A]                                   = UIO(a)
    def defer[A](a: => ZIO[R, E, A]): ZIO[R, E, A]                          = UIO.unit.flatMap(_ => a)
    def flatMap[A, B](fa: ZIO[R, E, A])(f: A => ZIO[R, E, B]): ZIO[R, E, B] = fa.flatMap(f)
    def map[A, B](fa: ZIO[R, E, A])(f: A => B): ZIO[R, E, B]                = fa.map(f)
  }
}
