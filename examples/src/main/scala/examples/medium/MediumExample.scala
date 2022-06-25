package examples
package medium

import jap.fields._
import jap.fields.examples.Post.apply
import zio.Task
import zio._
import zio.console._

import java.time.LocalDateTime

import MediumValidation._

trait UserRepo {
  def userExists(userId: UserId): Task[Boolean]
}

trait PostRepo {
  def postDoesNotExist(postId: PostId): Task[Boolean]
  def titleExists(title: String): Task[Boolean]
}

case class PostValidationService(postRepo: PostRepo, userRepo: UserRepo) {
  implicit val policy: Policy[MediumPost] =
    Policy
      .builder[MediumPost]
      .rule(MediumPost.policy.validate)
      .subRule(_.authorId)(_.ensureF(userRepo.userExists, failCode(33)))
      .subRule(_.postId)(_.ensureF(postRepo.postDoesNotExist, failCode(44)))
      .subRule(_.title)(_.ensureF(postRepo.titleExists(_).negate, failCode(55)))
      .build
}

object MediumExample extends zio.App {
  val post = MediumPost(
    postId = PostId(-1),
    title = "",
    description = Some(""),
    tags = List.fill(1000)("1234") :+ "",
    creationDate = PostDate(LocalDateTime.now.plusYears(10)),
    updateDate = PostDate(LocalDateTime.now.plusYears(5)),
    authorId = UserId(-5),
    paragraphs = Map(
      ""  -> ParagraphContent.Link(""),
      "a" -> ParagraphContent.Text(List.fill(51)(".").mkString("")),
    ),
  )

  val postF = Field.from(post)

  println("//---- Field Examples ----//")
  println(postF.path)
  println(Field(post.title).path)
  println(Field.from(post.title).path)
  println(postF.sub(_.title).path)
  println(Field.sub(post.title).path)
  println(Field.from(post.paragraphs("a")).path)
  println(postF.sub(_.paragraphs("a")).path)
  println(Field.from(post.tags.apply(3)).path)
  println("//---- Field Examples ----//")

  val userRepo = new UserRepo {
    def userExists(userId: UserId): Task[Boolean] = UIO(false)
  }

  val postRepo = new PostRepo {
    def postDoesNotExist(postId: PostId): Task[Boolean] = UIO(false)
    def titleExists(title: String): Task[Boolean]       = UIO(true)
  }

  val validationService = PostValidationService(postRepo, userRepo)

  import validationService.policy
  def run(args: List[String]): URIO[ZEnv, ExitCode] =
    postF.validate.effect
      .flatMap { result =>
        if (result.isValid) putStrLn("Saved post to DB")
        else putStrLn(s"Responding with ValidationErrors:\n\n${result.errors.mkString("\n")}")
      }
      .ignore
      .exitCode
}
