package jap.fields
package error

/** Error type that holds path where error occured, error type and human-readable message
  */
case class ValidationMessage(
    path: FieldPath,
    error: String,
    message: Option[String] = None,
)
object ValidationMessage {
  def apply(path: FieldPath, error: String, message: String): ValidationMessage =
    ValidationMessage(path, error, Some(message))
}
