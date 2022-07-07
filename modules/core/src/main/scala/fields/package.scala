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

package jap

package object fields {

  /** Rule is tagged type alias for F[V[E]] If used this way we do not add any allocations while having separate syntax
    * for Rule. Also we get ability to convert back and forth.
    */
  type Rule[+F[_], +V[_], +E] <: Rule.Type[F, V, E]
}
