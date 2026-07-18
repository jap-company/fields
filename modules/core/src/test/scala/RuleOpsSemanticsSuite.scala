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

import fields.typeclass.Effect.Sync

class RuleOpsSemanticsSuite extends munit.FunSuite {
  test("sync accumulating rules evaluate once in declaration order") {
    implicit val R: RuleOps[Sync, Accumulate, String] = RuleOps.instance
    val evaluated                                     = scala.collection.mutable.ListBuffer.empty[String]

    def rule(name: String): RuleK[Sync, Accumulate, String] = R.pure {
      evaluated += name
      R.V.invalid(name)
    }

    assertEquals(R.and(rule("left"), rule("right")).unwrap, List("left", "right"))
    assertEquals(evaluated.toList, List("left", "right"))
  }

  test("sync fail-fast rules stop after the first declared failure") {
    implicit val R: RuleOps[Sync, FailFast, String] = RuleOps.instance
    val evaluated                                   = scala.collection.mutable.ListBuffer.empty[String]

    def rule(name: String, valid: Boolean): RuleK[Sync, FailFast, String] = R.pure {
      evaluated += name
      if (valid) R.V.valid else R.V.invalid(name)
    }

    assertEquals(R.and(rule("left", valid = false), rule("right", valid = false)).unwrap, Some("left"))
    assertEquals(evaluated.toList, List("left"))

    evaluated.clear()
    assertEquals(R.or(rule("left", valid = true), rule("right", valid = false)).unwrap, None)
    assertEquals(evaluated.toList, List("left"))
  }
}
