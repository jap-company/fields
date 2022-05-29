package jap.fields
package examples

object Common {
  implicit object StringErrorMapper extends ValidationErrorMapper[String] {
    def custom[A](ctx: Ctx[A], error: String, description: Option[String]): String =
      s"$error:${description.getOrElse("-")}"
    def invalid[A](ctx: Ctx[A]): String                                            = s"invalid"
    def empty[A](ctx: Ctx[A]): String                                              = s"empty"
    def nonEmpty[A](ctx: Ctx[A]): String                                           = s"nonEmpty"
    def greater[A](ctx: Ctx[A], compared: String): String                          = s"should be greater than $compared"
    def greaterEqual[A](ctx: Ctx[A], compared: String): String                     =
      s"should be greater/equal to $compared"
    def less[A](ctx: Ctx[A], compared: String): String                             = s"should be less than $compared"
    def lessEqual[A](ctx: Ctx[A], compared: String): String                        = s"should be less/equal $compared"
    def equal[A](ctx: Ctx[A], compared: String): String                            = s"should be equal to $compared"
    def notEqual[A](ctx: Ctx[A], compared: String): String                         = s"should not be equal to $compared"
    def minSize[A](ctx: Ctx[A], size: Int): String           = s"should have size greater than $size"
    def maxSize[A](ctx: Ctx[A], size: Int): String           = s"should have size less than $size"
    def oneOf[A](ctx: Ctx[A], variants: Seq[String]): String = s"should be one of $variants"
  }
}
