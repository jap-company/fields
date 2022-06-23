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
package fail

/** Aggregates all possible FailWith* typeclasses shorthand if you need all of them. If are free to implemented only
  * those you will use. Best practise will be to but implicit instance of this into companion object of your error.
  */
trait FailWith[E, +P]
    extends FailWithMessage[E, P]
    with FailWithCompare[E, P]
    with FailWithInvalid[E, P]
    with FailWithEmpty[E, P]
    with FailWithNonEmpty[E, P]
    with FailWithMinSize[E, P]
    with FailWithMaxSize[E, P]
    with FailWithOneOf[E, P]

object FailWith {
  type Base[E] = FailWith[E, Nothing]
}
