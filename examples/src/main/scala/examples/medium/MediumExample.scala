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

package examples
package medium

import examples.medium.MediumValidation.*
import zio.*

import java.time.LocalDateTime

trait UserRepo {
  def userExists(userId: UserId): Task[Boolean]
}

trait PostRepo {
  def postDoesNotExist(postId: PostId): Task[Boolean]
  def titleExists(title: String): Task[Boolean]
}

case class PostValidationService(postRepo: PostRepo, userRepo: UserRepo) {
  implicit val policy: Policy[MediumPost] =
    Policy[MediumPost]
      .and(MediumPost.policy)
      .subRule(_.authorId)(_.assertF(userRepo.userExists, _.failCode(33)))
      .subRule(_.postId)(_.assertF(postRepo.postDoesNotExist, _.failCode(44)))
      .subRule(_.title)(_.assertF(postRepo.titleExists(_).negate, _.failCode(55)))
}

object MediumExample extends ZIOAppDefault {
  showBuildInfo()

  val post = MediumPost(
    postId = PostId(-1),
    title = "",
    description = Some(""),
    tags = List.fill(1000)("1234") :+ "",
    creationDate = PostDate(LocalDateTime.now.plusYears(10)),
    updateDate = PostDate(LocalDateTime.now.plusYears(5)),
    authorId = UserId(-5),
    paragraphs = Map(
      ""  -> Link(""),
      "a" -> Text(List.fill(51)(".").mkString("")),
    ),
  )

  val postF = Field.from(post)

  showTitle("FIELD-EXAMPLES-START")
  println(postF.path)
  println(Field(post.title).path)
  println(Field.from(post.title).path)
  println(postF.sub(_.title).path)
  println(Field.sub(post.title).path)
  println(Field.from(post.paragraphs("a")).path)
  println(postF.sub(_.paragraphs("a")).path)
  println(Field.from(post.tags.apply(3)).path)
  showTitle("FIELD-EXAMPLES-END")

  val userRepo: UserRepo = new UserRepo {
    def userExists(userId: UserId): Task[Boolean] = ZIO.from(false)
  }

  val postRepo: PostRepo = new PostRepo {
    def postDoesNotExist(postId: PostId): Task[Boolean] = ZIO.from(false)
    def titleExists(title: String): Task[Boolean]       = ZIO.from(true)
  }

  val validationService: PostValidationService = PostValidationService(postRepo, userRepo)

  import validationService.policy
  def run: UIO[Unit] =
    postF.validate.effect.flatMap { result =>
      if (result.isValid) Console.printLine("Saved post to DB")
      else Console.printLine(s"Responding with ValidationErrors:\n\n${result.errors.mkString("\n")}")
    }.ignore
}
