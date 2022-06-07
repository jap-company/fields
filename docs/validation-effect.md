# ValidationEffect

Defines Monad/Defer like capabilities for your `F[_]`.

This is here just to not rely on cats library if you dont need it.

If you want Fields to correctly handle short-circuiting you should use lazy ValidationEffect like `zio.ZIO` or `cats.effect.IO`

If you don\`t need async validation, but want short-circuiting stick to `cats.Eval`

```scala
trait ValidationEffect[F[_]] {
  def pure[A](a: A): F[A]
  def suspend[A](a: => A): F[A]
  def defer[A](a: => F[A]): F[A]
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
  def map[A, B](fa: F[A])(f: A => B): F[B]
}
```

## Instances

Predefined instances:

- Anything that has `cats.Monad`/`cats.Defer` instances
- `zio.ZIO[R, E, _]`
- `scala.concurrent.Future`, requires `ExectionContext`
- `ValidationEffect.Sync` same as `cats.Id`
