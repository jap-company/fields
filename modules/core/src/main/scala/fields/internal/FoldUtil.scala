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

object FoldUtil {

  /** Combines `results` using `combine` function, returns empty if empty. This has minor optimisations that checks size
    * to handle simple cases.
    */
  @inline def fold[A](results: List[A], empty: A, combine: (A, A) => A) =
    if (results.size == 0) empty
    else if (results.size == 1) results.head
    else if (results.size == 2) combine(results(0), results(1))
    else results.reduce(combine)
}
