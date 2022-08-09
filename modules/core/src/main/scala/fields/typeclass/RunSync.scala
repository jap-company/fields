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

/** [[jap.fields.typeclass.RunSync]] is a typeclass that help runnin sync like effects */
trait RunSync[F[_]] {

  /** Run `F[A]` to a value `A` */
  def run[A](effect: F[A]): A
}

object RunSync {
  def apply[F[_]](implicit instance: RunSync[F]): RunSync[F] = instance

  implicit object EffectSyncRunSync extends RunSync[Effect.Sync] {
    def run[A](effect: A): A = effect
  }
}
