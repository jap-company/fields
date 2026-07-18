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

import fields.error.ValidationMessage
import fields.lens.FieldsDsl.default.*

object LensSuiteData {
  sealed trait Contact
  final case class Email(value: String) extends Contact
  final case class Phone(value: String) extends Contact
  final case class User(contact: Contact, tags: List[String])
}

class LensSuite extends munit.FunSuite {
  import LensSuiteData.*

  test("nested whenType validates the selected subtype") {
    val policy: Policy[User] =
      Policy[User].subRule(_.contact)(
        _.whenType[Email](_.assert(_.value.nonEmpty, failMessage("email must not be empty")))
      )

    assertEquals(
      policy(User(Email(""), Nil)).errors,
      List(ValidationMessage(FieldPath.fromPath("contact"), "email must not be empty")),
    )
    assertEquals(policy(User(Email("a@example.com"), Nil)).errors, Nil)
    assertEquals(policy(User(Phone("123"), Nil)).errors, Nil)
  }

  test("nested iterable rules preserve indexes and paths") {
    val policy: Policy[User] =
      Policy[User].subRule(_.tags)(
        _.each(value => Rule.assert(ValidationMessage(FieldPath.Root, "empty"))(value.nonEmpty))
      )

    assertEquals(
      policy(User(Phone("123"), List("ok", ""))).errors,
      List(ValidationMessage(FieldPath.parse("tags[1]"), "empty")),
    )
  }

  test("multiple subrules accumulate errors in declaration order") {
    val policy: Policy[User] = Policy[User]
      .subRule(_.contact)(_.assert(_ => false, failMessage("contact")))
      .subRule(_.tags)(_.nonEmpty)

    assertEquals(
      policy(User(Phone(""), Nil)).errors.map(_.path.full),
      List(".contact", ".tags"),
    )
  }
}
