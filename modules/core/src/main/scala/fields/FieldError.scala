package jap.fields

case class FieldError[E](
    path: FieldPath,
    error: E,
) {
  override def toString: String = s"$path -> $error"
}
