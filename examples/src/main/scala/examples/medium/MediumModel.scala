package jap.fields
package examples
package medium

import jap.fields.Field

import java.time.LocalDateTime

import MediumValidation._

case class PostId(value: Int)             extends AnyVal
case class UserId(value: Int)             extends AnyVal
case class PostDate(value: LocalDateTime) extends AnyVal
object PostDate         {
  implicit val policy: Policy[PostDate]     = _ > PostDate(LocalDateTime.now)
  implicit val ordering: Ordering[PostDate] = Ordering.by(_.value)
}
sealed trait ParagraphContent
object ParagraphContent {
  case class Link(value: String) extends ParagraphContent
  case class Text(value: String) extends ParagraphContent
}

case class MediumPost(
    postId: PostId,
    title: String,
    description: Option[String],
    tags: List[String],
    creationDate: PostDate,
    updateDate: PostDate,
    authorId: UserId,
    paragraphs: Map[String, ParagraphContent],
)
object MediumPost       {
  implicit val policy: Policy[MediumPost] =
    Policy
      .builder[MediumPost]
      .fieldRule(_.sub(_.postId).map(_.value))(_ > 0, _ !== Int.MaxValue)
      .fieldRule(_.sub(_.authorId).map(_.value))(_ > 0)
      .subRule(_.title)(_.minSize(6), _.maxSize(255))
      .subRule(_.description)(_.some(_.nonBlank))
      .subRule(_.tags)(_.nonEmpty, _.maxSize(12), _.each(_.all(_.minSize(3), _.notEqualTo("JAVA"))))
      .subRule(_.creationDate, _.updateDate)(
        (c, u) => c.validate && u.validate,
        _ < _,
      )
      .subRule(_.paragraphs)(_.eachKey(_.nonEmpty), _.eachValue(validateParagraphContent))
      .build

  def validateParagraphContent(content: Field[ParagraphContent]): MRule = {
    content.value match {
      case link: ParagraphContent.Link =>
        val urlF = content.withValue(link.value)
        urlF.all(_.minSize(10), _.maxSize(50))
      case text: ParagraphContent.Text =>
        val textF = content.withValue(text.value)
        textF.all(_.minSize(10), _.maxSize(50))
    }
  }
}
