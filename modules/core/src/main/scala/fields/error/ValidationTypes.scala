package jap.fields
package error

/** This corresponds to `error` field of ValidationError with given names */
object ValidationTypes {
  val Invalid          = "invalid_error"
  val Empty            = "empty_error"
  val NonEmpty         = "non_empty_error"
  val Greater          = "greater_error"
  val GreaterEqual     = "greater_equal_error"
  val Less             = "less_error"
  val LessEqual        = "less_equal_error"
  val Equal            = "equal_error"
  val NotEqual         = "not_equal_error"
  val MinSize          = "min_size_error"
  val MaxSize          = "max_size_error"
  val OneOf            = "one_of_error"
  val StringStartsWith = "string/starts_with"
  val StringEndsWith   = "string/ends_with"
  val StringMatch      = "string/match"
}
