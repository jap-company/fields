package jap.fields

import ValidationError._
trait ValidationErrorMapper[E] { self =>
  type Ctx[A] = ValidationContext[E, A]
  def custom[A](ctx: Ctx[A], error: String, description: Option[String]): E
  def invalid[A](ctx: Ctx[A]): E
  def empty[A](ctx: Ctx[A]): E
  def nonEmpty[A](ctx: Ctx[A]): E
  def greater[A](ctx: Ctx[A], compared: String): E
  def greaterEqual[A](ctx: Ctx[A], compared: String): E
  def less[A](ctx: Ctx[A], compared: String): E
  def lessEqual[A](ctx: Ctx[A], compared: String): E
  def equal[A](ctx: Ctx[A], compared: String): E
  def notEqual[A](ctx: Ctx[A], compared: String): E
  def minSize[A](ctx: Ctx[A], size: Int): E
  def maxSize[A](ctx: Ctx[A], size: Int): E
  def oneOf[A](ctx: Ctx[A], variants: Seq[String]): E
}

object ValidationErrorMapper {
  def apply[E](implicit em: ValidationErrorMapper[E]): ValidationErrorMapper[E] = em

  implicit def fromValidationModule[F[_], VR[_], E](implicit M: ValidationModule[F, VR, E]): ValidationErrorMapper[E] =
    M.E

  implicit object ValidationErrorMapper extends ValidationErrorMapper[ValidationError] {
    def custom[A](ctx: Ctx[A], error: String, description: Option[String]): ValidationError =
      Custom(error, description)

    def invalid[A](ctx: Ctx[A]): ValidationError                        = Invalid
    def empty[A](ctx: Ctx[A]): ValidationError                          = Empty
    def nonEmpty[A](ctx: Ctx[A]): ValidationError                       = NonEmpty
    def greater[A](ctx: Ctx[A], compared: String): ValidationError      = Greater(compared)
    def greaterEqual[A](ctx: Ctx[A], compared: String): ValidationError = GreaterEqual(compared)
    def less[A](ctx: Ctx[A], compared: String): ValidationError         = Less(compared)
    def lessEqual[A](ctx: Ctx[A], compared: String): ValidationError    = LessEqual(compared)
    def equal[A](ctx: Ctx[A], compared: String): ValidationError        = Equal(compared)
    def notEqual[A](ctx: Ctx[A], compared: String): ValidationError     = NotEqual(compared)
    def minSize[A](ctx: Ctx[A], size: Int): ValidationError             = MinSize(size)
    def maxSize[A](ctx: Ctx[A], size: Int): ValidationError             = MaxSize(size)
    def oneOf[A](ctx: Ctx[A], variants: Seq[String]): ValidationError   = OneOf(variants)
  }

  implicit def fromFieldError[E](implicit EM: ValidationErrorMapper[E]): ValidationErrorMapper[FieldError[E]] =
    new ValidationErrorMapper[FieldError[E]] {
      private def pathed[A](ctx: Ctx[A], f: (ValidationErrorMapper[E], ValidationContext[E, A]) => E) =
        FieldError(ctx.path, f(EM, ctx.withE[E]))

      def invalid[A](ctx: Ctx[A]): FieldError[E]                        = pathed[A](ctx, _.invalid(_))
      def empty[A](ctx: Ctx[A]): FieldError[E]                          = pathed[A](ctx, _.empty(_))
      def nonEmpty[A](ctx: Ctx[A]): FieldError[E]                       = pathed[A](ctx, _.nonEmpty(_))
      def greater[A](ctx: Ctx[A], compared: String): FieldError[E]      = pathed[A](ctx, _.greater(_, compared))
      def greaterEqual[A](ctx: Ctx[A], compared: String): FieldError[E] = pathed[A](ctx, _.greaterEqual(_, compared))
      def less[A](ctx: Ctx[A], compared: String): FieldError[E]         = pathed[A](ctx, _.less(_, compared))
      def lessEqual[A](ctx: Ctx[A], compared: String): FieldError[E]    = pathed[A](ctx, _.lessEqual(_, compared))
      def equal[A](ctx: Ctx[A], compared: String): FieldError[E]        = pathed[A](ctx, _.equal(_, compared))
      def notEqual[A](ctx: Ctx[A], compared: String): FieldError[E]     = pathed[A](ctx, _.notEqual(_, compared))
      def minSize[A](ctx: Ctx[A], size: Int): FieldError[E]             = pathed[A](ctx, _.minSize(_, size))
      def maxSize[A](ctx: Ctx[A], size: Int): FieldError[E]             = pathed[A](ctx, _.maxSize(_, size))
      def oneOf[A](ctx: Ctx[A], variants: Seq[String]): FieldError[E]   = pathed[A](ctx, _.oneOf(_, variants))
      def custom[A](ctx: Ctx[A], error: String, description: Option[String]): FieldError[E] =
        pathed[A](ctx, _.custom(_, error, description))
    }
}
