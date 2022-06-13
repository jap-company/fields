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

import zio._

object ZIOInterop {

  /** [[jap.fields.ValidationEffect]] instance for `zio.ZIO` */
  implicit def fromZIO[R, E]: ValidationEffect[ZIO[R, E, _]] = new ValidationEffect[ZIO[R, E, _]] {
    def pure[A](a: A): ZIO[R, E, A]                                         = UIO(a)
    def suspend[A](a: => A): ZIO[R, E, A]                                   = UIO(a)
    def defer[A](a: => ZIO[R, E, A]): ZIO[R, E, A]                          = UIO.unit.flatMap(_ => a)
    def flatMap[A, B](fa: ZIO[R, E, A])(f: A => ZIO[R, E, B]): ZIO[R, E, B] = fa.flatMap(f)
    def map[A, B](fa: ZIO[R, E, A])(f: A => B): ZIO[R, E, B]                = fa.map(f)
  }
}
