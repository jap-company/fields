package jap.fields

final case class ValidationContext[E, A](path: FieldPath, value: A)(implicit VEM: ValidationErrorMapper[E]) {
  def failPath[EE](error: EE)(implicit ev: E =:= FieldError[EE]): E = FieldError(path, error).asInstanceOf[E]
  def fail(error: E): E                                             = error

  def custom(error: String, description: Option[String] = None): E = VEM.custom(this, error, description)
  def invalid: E                                                   = VEM.invalid(this)
  def empty: E                                                     = VEM.empty(this)
  def nonEmpty: E                                                  = VEM.nonEmpty(this)
  def greater(compared: String): E                                 = VEM.greater(this, compared)
  def greaterEqual(compared: String): E                            =
    VEM.greaterEqual(this, compared)
  def less(compared: String): E                                    = VEM.less(this, compared)
  def lessEqual(compared: String): E                               = VEM.lessEqual(this, compared)
  def equal(compared: String): E                                   = VEM.equal(this, compared)
  def notEqual(compared: String): E                                = VEM.notEqual(this, compared)
  def minSize(size: Int): E                                        = VEM.minSize(this, size)
  def maxSize(size: Int): E                                        = VEM.maxSize(this, size)
  def oneOf(variants: Seq[String]): E                              = VEM.oneOf(this, variants)

  def withE[EE: ValidationErrorMapper] = copy[EE, A]()
}

object ValidationContext {
  def apply[E: ValidationErrorMapper, A](field: Field[A]): ValidationContext[E, A] =
    ValidationContext[E, A](field.path, field.value)
}
