package jap.fields

/** [[jap.fields.FieldError]] carries `error` with `path` where it occured. Using this can be useful when your `error`
  * type does not support carrying `path` where it occured but you actually want to know it.
  */
case class FieldError[E](
    path: FieldPath,
    error: E,
) {
  override def toString: String = s"$path -> $error"
}
