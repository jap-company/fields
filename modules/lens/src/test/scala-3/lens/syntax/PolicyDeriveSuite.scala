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

package fields
package lens.syntax

import fields.lens.FieldsDsl.default.*
import fields.lens.syntax.PolicyDeriveSyntax.derived

object PolicyDeriveSuiteData {
  final case class Derived(age: Int, name: String)
}

class PolicyDeriveSuite extends munit.FunSuite {
  import PolicyDeriveSuiteData.*

  given LensPolicy[Int]    = LensPolicy.root(_ > 10)
  given LensPolicy[String] = LensPolicy.root(_.minSize(2))

  test("Scala 3 policy derivation applies field policies with derived paths") {
    val policy = summon[LensPolicy[Derived]].root

    assertEquals(policy(Derived(5, "x")).errors.map(_.path.full), List(".age", ".name"))
    assertEquals(policy(Derived(11, "ok")).errors, Nil)
  }
}
