package jap.fields
package error

/** ValidationError error messages */
object ValidationMessages {
  val Invalid                         = s"must be valid"
  val NonEmpty                        = s"must not be empty"
  val Empty                           = s"must be empty"
  def Greater(compared: String)       = s"must be greater than $compared"
  def GreaterEqual(compared: String)  = s"must be greater than or equal to $compared"
  def Less(compared: String)          = s"must be less than $compared"
  def LessEqual(compared: String)     = s"must be less than or equal to $compared"
  def Equal(compared: String)         = s"must be equal to $compared"
  def NotEqual(compared: String)      = s"must not be equal to $compared"
  def MinSize(size: Int)              = s"must have minimum size of $size"
  def MaxSize(size: Int)              = s"must have maximum size of $size"
  def OneOf(variants: Seq[String])    = s"must be one of ${variants.mkString(",")}"
  def StringStartsWith(value: String) = s"must start with $value"
  def StringEndsWith(value: String)   = s"must end with $value"
  def StringMatch(value: String)      = s"must match $value"
}
