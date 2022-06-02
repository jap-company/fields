package jap.fields

/** Predefined Error type that carries `error` type and human-readable message */
sealed trait ValidationError {
  def error: String
  def message: Option[String]
  override def toString = error + message.fold("")(":" + _)
}

object ValidationError {
  case object Invalid  extends ValidationError {
    val error   = ValidationErrors.Invalid
    val message = Some("should be valid")
  }
  case object Empty    extends ValidationError {
    val error   = ValidationErrors.Empty
    val message = Some("should not be empty")
  }
  case object NonEmpty extends ValidationError {
    val error   = ValidationErrors.NonEmpty
    val message = Some("should be empty")
  }

  case class Greater(compared: String) extends ValidationError {
    val error   = ValidationErrors.Greater
    val message = Some(Greater.message(compared))
  }
  object Greater { def message(compared: String) = s"should be greater than $compared" }

  case class GreaterEqual(compared: String) extends ValidationError {
    val error   = ValidationErrors.GreaterEqual
    val message = Some(GreaterEqual.message(compared))
  }
  object GreaterEqual { def message(compared: String) = s"should be greater than or equal to $compared" }

  case class Less(compared: String) extends ValidationError {
    val error   = ValidationErrors.Less
    val message = Some(Less.message(compared))
  }
  object Less { def message(compared: String) = s"should be less than $compared" }

  case class LessEqual(compared: String) extends ValidationError {
    val error   = ValidationErrors.LessEqual
    val message = Some(LessEqual.message(compared))
  }
  object LessEqual { def message(compared: String) = s"should be less than or equal to $compared" }

  case class Equal(compared: String) extends ValidationError {
    val error   = ValidationErrors.Equal
    val message = Some(Equal.message(compared))
  }
  object Equal { def message(compared: String) = s"should be $compared" }

  case class NotEqual(compared: String) extends ValidationError {
    val error   = ValidationErrors.NotEqual
    val message = Some(NotEqual.message(compared))
  }
  object NotEqual { def message(compared: String) = s"should not be $compared" }

  case class MinSize(size: Int) extends ValidationError {
    val error   = ValidationErrors.MinSize
    val message = Some(MinSize.message(size))
  }
  object MinSize { def message(size: Int) = s"min size should be $size" }

  case class MaxSize(size: Int) extends ValidationError {
    val error   = ValidationErrors.MaxSize
    val message = Some(MaxSize.message(size))
  }
  object MaxSize { def message(size: Int) = s"max should be $size" }

  case class OneOf(variants: Seq[String]) extends ValidationError {
    val error   = ValidationErrors.OneOf
    val message = Some(OneOf.message(variants))
  }
  object OneOf { def message(variants: Seq[String]) = s"should be one of ${variants.mkString(",")}" }

  /** If you dont need to match on errors and just want to have separate error and user message, use this rather than
    * ValidationError
    */
  case class Message(
      error: String,
      message: Option[String] = None,
  ) extends ValidationError
  object Message {
    implicit object ValidationMessageCanFail extends CanFail[Message] {
      def invalid[A](field: Field[A]): Message            = Message(ValidationErrors.Invalid, Invalid.message)
      def empty[A](field: Field[A]): Message              = Message(ValidationErrors.Empty, Empty.message)
      def nonEmpty[A](field: Field[A]): Message           = Message(ValidationErrors.NonEmpty, NonEmpty.message)
      def minSize[A](size: Int)(field: Field[A]): Message =
        Message(ValidationErrors.MinSize, Some(MinSize.message(size)))
      def maxSize[A](size: Int)(field: Field[A]): Message =
        Message(ValidationErrors.MaxSize, Some(MaxSize.message(size)))
      def compare[A](operation: CompareOperation, compared: String)(field: Field[A]): Message =
        operation match {
          case CompareOperation.Equal    => Message(ValidationErrors.Equal, Some(s"should be equal to $compared"))
          case CompareOperation.NotEqual =>
            Message(ValidationErrors.NotEqual, Some(s"should not be equal to $compared"))
          case CompareOperation.Greater  => Message(ValidationErrors.Greater, Some(s"should be greater than $compared"))
          case CompareOperation.GreaterEqual =>
            Message(ValidationErrors.GreaterEqual, Some(s"should be greater/equal to $compared"))
          case CompareOperation.Less         => Message(ValidationErrors.Less, Some(s"should be less than $compared"))
          case CompareOperation.LessEqual    =>
            Message(ValidationErrors.LessEqual, Some(s"should be less/equal $compared"))
        }
      def message[A](error: String, message: Option[String])(field: Field[A]): Message        =
        Message(error, message)
      def oneOf[A](variants: Seq[A])(field: Field[A]): Message                                =
        Message(ValidationErrors.OneOf, Some(OneOf.message(variants.map(_.toString))))
    }
  }

  implicit object CanFailValidationError extends CanFail[ValidationError] {
    def message[A](error: String, message: Option[String])(field: Field[A]): ValidationError = Message(error, message)
    def invalid[A](field: Field[A]): ValidationError                                         = Invalid
    def empty[A](field: Field[A]): ValidationError                                           = Empty
    def nonEmpty[A](field: Field[A]): ValidationError                                        = NonEmpty
    def minSize[A](size: Int)(field: Field[A]): ValidationError                              = MinSize(size)
    def maxSize[A](size: Int)(field: Field[A]): ValidationError                              = MaxSize(size)
    def oneOf[A](variants: Seq[A])(field: Field[A]): ValidationError = OneOf(variants.map(_.toString))
    def compare[A](operation: CompareOperation, compared: String)(field: Field[A]): ValidationError = {
      operation match {
        case CompareOperation.Equal        => Equal(compared)
        case CompareOperation.NotEqual     => NotEqual(compared)
        case CompareOperation.Greater      => Greater(compared)
        case CompareOperation.GreaterEqual => GreaterEqual(compared)
        case CompareOperation.Less         => Less(compared)
        case CompareOperation.LessEqual    => LessEqual(compared)
      }
    }
  }
}

object ValidationErrors {
  val Invalid      = "invalid"
  val Empty        = "empty"
  val NonEmpty     = "non-empty"
  val Greater      = "greater"
  val GreaterEqual = "greater-equal"
  val Less         = "less"
  val LessEqual    = "less-equal"
  val Equal        = "equal"
  val NotEqual     = "not-equal"
  val MinSize      = "min-size"
  val MaxSize      = "max-size"
  val OneOf        = "one-of"
}
