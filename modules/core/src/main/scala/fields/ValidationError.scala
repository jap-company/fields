package jap.fields

sealed trait ValidationError {
  def error: String
  def description: Option[String]

  override def toString = error + description.fold("")(":" + _)
}
object ValidationError       {

  case class Custom(
      override val error: String,
      override val description: Option[String] = None,
  ) extends ValidationError
  case object Invalid                       extends ValidationError {
    val error       = "invalid"
    val description = Some("should be valid")
  }
  case object Empty                         extends ValidationError {
    val error       = "empty"
    val description = Some("should not be empty")
  }
  case object NonEmpty                      extends ValidationError {
    val error       = "non-empty"
    val description = Some("should be empty")
  }
  case class Greater(compared: String)      extends ValidationError {
    val error       = "greater"
    val description = Some(s"should be greater than $compared")
  }
  case class GreaterEqual(compared: String) extends ValidationError {
    val error       = "greater-equal"
    val description = Some(s"should be greater than or equal to $compared")
  }
  case class Less(compared: String)         extends ValidationError {
    val error       = "less"
    val description = Some(s"should be less than $compared")
  }
  case class LessEqual(compared: String)    extends ValidationError {
    val error       = "less-equal"
    val description = Some(s"should be less than or equal to $compared")
  }
  case class Equal(compared: String)        extends ValidationError {
    val error       = "equal"
    val description = Some(s"should be $compared")
  }
  case class NotEqual(compared: String)     extends ValidationError {
    val error       = "not-equal"
    val description = Some(s"should not be $compared")
  }

  case class MinSize(size: Int)           extends ValidationError {
    val error       = "min-size"
    val description = Some(s"min size should be $size")
  }
  case class MaxSize(size: Int)           extends ValidationError {
    val error       = "max-size"
    val description = Some(s"max should be $size")
  }
  case class OneOf(variants: Seq[String]) extends ValidationError {
    val error       = "one-of"
    val description = Some(s"should be one of ${variants.mkString(",")}")
  }
}
