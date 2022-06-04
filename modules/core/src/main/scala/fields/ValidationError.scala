package jap.fields

/** Predefined Error type that carries `error` type and human-readable message */
sealed trait ValidationError {
  def path: FieldPath
  def error: String
  def message: Option[String]
  override def toString = s"$path -> ${message.getOrElse(error)}"
}
object ValidationError       {
  case class Invalid(path: FieldPath)  extends ValidationError {
    val error   = ValidationTypes.Invalid
    val message = Some(ValidationMessages.Invalid)
  }
  case class Empty(path: FieldPath)    extends ValidationError {
    val error   = ValidationTypes.Empty
    val message = Some(ValidationMessages.Empty)
  }
  case class NonEmpty(path: FieldPath) extends ValidationError {
    val error   = ValidationTypes.NonEmpty
    val message = Some(ValidationMessages.NonEmpty)
  }

  case class Greater(path: FieldPath, compared: String) extends ValidationError {
    val error   = ValidationTypes.Greater
    val message = Some(ValidationMessages.Greater(compared))
  }

  case class GreaterEqual(path: FieldPath, compared: String) extends ValidationError {
    val error   = ValidationTypes.GreaterEqual
    val message = Some(ValidationMessages.GreaterEqual(compared))
  }

  case class Less(path: FieldPath, compared: String) extends ValidationError {
    val error   = ValidationTypes.Less
    val message = Some(ValidationMessages.Less(compared))
  }

  case class LessEqual(path: FieldPath, compared: String) extends ValidationError {
    val error   = ValidationTypes.LessEqual
    val message = Some(ValidationMessages.LessEqual(compared))
  }

  case class Equal(path: FieldPath, compared: String) extends ValidationError {
    val error   = ValidationTypes.Equal
    val message = Some(ValidationMessages.Equal(compared))
  }

  case class NotEqual(path: FieldPath, compared: String) extends ValidationError {
    val error   = ValidationTypes.NotEqual
    val message = Some(ValidationMessages.NotEqual(compared))
  }

  case class MinSize(path: FieldPath, size: Int) extends ValidationError {
    val error   = ValidationTypes.MinSize
    val message = Some(ValidationMessages.MinSize(size))
  }

  case class MaxSize(path: FieldPath, size: Int) extends ValidationError {
    val error   = ValidationTypes.MaxSize
    val message = Some(ValidationMessages.MaxSize(size))
  }

  case class OneOf(path: FieldPath, variants: Seq[String]) extends ValidationError {
    val error   = ValidationTypes.OneOf
    val message = Some(ValidationMessages.OneOf(variants))
  }

  /** If you dont need to match on errors and just want to have separate error and user message, use this rather than
    * ValidationError
    */
  case class Message(
      path: FieldPath,
      error: String,
      message: Option[String] = None,
  ) extends ValidationError
  object Message {
    def apply(path: FieldPath, error: String, message: String): Message = Message(path, error, Some(message))
    implicit object ValidationMessageFailWith extends FailWith[Message] {
      def invalid[A](field: Field[A]): Message  = Message(field, ValidationTypes.Invalid, ValidationMessages.Invalid)
      def empty[A](field: Field[A]): Message    = Message(field, ValidationTypes.Empty, ValidationMessages.Empty)
      def nonEmpty[A](field: Field[A]): Message = Message(field, ValidationTypes.NonEmpty, ValidationMessages.NonEmpty)
      def minSize[A](size: Int)(field: Field[A]): Message                                     =
        Message(field, ValidationTypes.MinSize, ValidationMessages.MinSize(size))
      def maxSize[A](size: Int)(field: Field[A]): Message                                     =
        Message(field, ValidationTypes.MaxSize, ValidationMessages.MaxSize(size))
      def compare[A](operation: CompareOperation, compared: String)(field: Field[A]): Message =
        operation match {
          case CompareOperation.Equal        =>
            Message(field, ValidationTypes.Equal, ValidationMessages.Equal(compared))
          case CompareOperation.NotEqual     =>
            Message(field, ValidationTypes.NotEqual, ValidationMessages.NotEqual(compared))
          case CompareOperation.Greater      =>
            Message(field, ValidationTypes.Greater, ValidationMessages.Greater(compared))
          case CompareOperation.GreaterEqual =>
            Message(field, ValidationTypes.GreaterEqual, ValidationMessages.GreaterEqual(compared))
          case CompareOperation.Less         =>
            Message(field, ValidationTypes.Less, ValidationMessages.Less(compared))
          case CompareOperation.LessEqual    =>
            Message(field, ValidationTypes.LessEqual, ValidationMessages.LessEqual(compared))
        }
      def message[A](error: String, message: Option[String])(field: Field[A]): Message        =
        Message(field, error, message)
      def oneOf[A](variants: Seq[A])(field: Field[A]): Message                                =
        Message(field, ValidationTypes.OneOf, Some(ValidationMessages.OneOf(variants.map(_.toString))))
    }
  }

  implicit object FailWithValidationError extends FailWith[ValidationError] {
    def invalid[A](field: Field[A]): ValidationError                 = Invalid(field)
    def empty[A](field: Field[A]): ValidationError                   = Empty(field)
    def nonEmpty[A](field: Field[A]): ValidationError                = NonEmpty(field)
    def minSize[A](size: Int)(field: Field[A]): ValidationError      = MinSize(field, size)
    def maxSize[A](size: Int)(field: Field[A]): ValidationError      = MaxSize(field, size)
    def oneOf[A](variants: Seq[A])(field: Field[A]): ValidationError = OneOf(field, variants.map(_.toString))
    def message[A](error: String, message: Option[String])(field: Field[A]): ValidationError        =
      Message(field, error, message)
    def compare[A](operation: CompareOperation, compared: String)(field: Field[A]): ValidationError = {
      operation match {
        case CompareOperation.Equal        => Equal(field, compared)
        case CompareOperation.NotEqual     => NotEqual(field, compared)
        case CompareOperation.Greater      => Greater(field, compared)
        case CompareOperation.GreaterEqual => GreaterEqual(field, compared)
        case CompareOperation.Less         => Less(field, compared)
        case CompareOperation.LessEqual    => LessEqual(field, compared)
      }
    }
  }
}

/** This corresponds to `error` field of ValidationError with given names */
object ValidationTypes {
  val Invalid      = "INVALID_ERROR"
  val Empty        = "EMPTY_ERROR"
  val NonEmpty     = "NON_EMPTY_ERROR"
  val Greater      = "GREATER_ERROR"
  val GreaterEqual = "GREATER_EQUAL_ERROR"
  val Less         = "LESS_ERROR"
  val LessEqual    = "LESS_EQUAL_ERROR"
  val Equal        = "EQUAL_ERROR"
  val NotEqual     = "NOT_EQUAL_ERROR"
  val MinSize      = "MIN_SIZE_ERROR"
  val MaxSize      = "MAX_SIZE_ERROR"
  val OneOf        = "ONE_OF_ERROR"
}

/** ValidationError error messages */
object ValidationMessages {
  val Invalid                        = s"should be valid"
  val NonEmpty                       = s"should not be empty"
  val Empty                          = s"should be empty"
  def Greater(compared: String)      = s"should be greater than $compared"
  def GreaterEqual(compared: String) = s"should be greater than or equal to $compared"
  def Less(compared: String)         = s"should be less than $compared"
  def LessEqual(compared: String)    = s"should be less than or equal to $compared"
  def Equal(compared: String)        = s"should be equal to $compared"
  def NotEqual(compared: String)     = s"should not be equal to $compared"
  def MinSize(size: Int)             = s"min size should be $size"
  def MaxSize(size: Int)             = s"max size should be $size"
  def OneOf(variants: Seq[String])   = s"should be one of ${variants.mkString(",")}"
}
