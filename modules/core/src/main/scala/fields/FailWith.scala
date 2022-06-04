package jap.fields

import scala.annotation.implicitNotFound
import scala.annotation.varargs

import ValidationError._

@implicitNotFound("To use this operation you need to have FailWithInvalid[${E}] in scope")
trait FailWithInvalid[E]  { def invalid[A](field: Field[A]): E                                         }
@implicitNotFound("To use this operation you need to have FailWithEmpty[${E}] in scope")
trait FailWithEmpty[E]    { def empty[A](field: Field[A]): E                                           }
@implicitNotFound("To use this operation you need to have FailWithNonEmpty[${E}] in scope")
trait FailWithNonEmpty[E] { def nonEmpty[A](field: Field[A]): E                                        }
@implicitNotFound("To use this operation you need to have FailWithMinSize[${E}] in scope")
trait FailWithMinSize[E]  { def minSize[A](size: Int)(field: Field[A]): E                              }
@implicitNotFound("To use this operation you need to have FailWithMaxSize[${E}] in scope")
trait FailWithMaxSize[E]  { def maxSize[A](size: Int)(field: Field[A]): E                              }
@implicitNotFound("To use this operation you need to have FailWithOneOf[${E}] in scope")
trait FailWithOneOf[E]    { def oneOf[A](variants: Seq[A])(field: Field[A]): E                         }
@implicitNotFound("To use this operation you need to have FailWithMessage[${E}] in scope")
trait FailWithMessage[E]  { def message[A](error: String, message: Option[String])(field: Field[A]): E }
@implicitNotFound("To use this operation you need to have FailWithCompare[${E}] in scope")
trait FailWithCompare[E]  {
  def compare[A](operation: CompareOperation, compared: String)(field: Field[A]): E

  def compare[A, C](operation: CompareOperation, compared: C)(field: Field[A])(implicit C: FieldCompare[A, C]): E =
    compare(operation, C.show(compared))(field)

  def notEqual[A, C](compared: C)(field: Field[A])(implicit C: FieldCompare[A, C]): E =
    compare(CompareOperation.NotEqual, compared)(field)

  def equal[A, C](compared: C)(field: Field[A])(implicit C: FieldCompare[A, C]): E =
    compare(CompareOperation.Equal, compared)(field)

  def less[A, C](compared: C)(field: Field[A])(implicit C: FieldCompare[A, C]): E =
    compare(CompareOperation.Less, compared)(field)

  def lessEqual[A, C](compared: C)(field: Field[A])(implicit C: FieldCompare[A, C]): E =
    compare(CompareOperation.LessEqual, compared)(field)

  def greaterEqual[A, C](compared: C)(field: Field[A])(implicit C: FieldCompare[A, C]): E =
    compare(CompareOperation.GreaterEqual, compared)(field)

  def greater[A, C](compared: C)(field: Field[A])(implicit C: FieldCompare[A, C]): E =
    compare(CompareOperation.Greater, compared)(field)
}
sealed trait CompareOperation
object CompareOperation   {
  case object Equal        extends CompareOperation
  case object NotEqual     extends CompareOperation
  case object Greater      extends CompareOperation
  case object GreaterEqual extends CompareOperation
  case object Less         extends CompareOperation
  case object LessEqual    extends CompareOperation
}

/** Aggregates all possible FailWith* typeclasses shorthand if you need all of them. If are free to implemented only
  * those you will use. Best practise will be to but implicit instance of this into companion object of your error.
  */
trait FailWith[E]
    extends FailWithMessage[E]
    with FailWithCompare[E]
    with FailWithInvalid[E]
    with FailWithEmpty[E]
    with FailWithNonEmpty[E]
    with FailWithMinSize[E]
    with FailWithMaxSize[E]
    with FailWithOneOf[E]

object FailWith {

  /** Wrap existing error into FieldError given [[FailWith]] instance for type `E`
    */
  class FailWithFieldError[E](FW: FailWith[E]) extends FailWith[FieldError[E]] {
    def invalid[A](field: Field[A]): FieldError[E]                 = FieldError(field.path, FW.invalid(field))
    def empty[A](field: Field[A]): FieldError[E]                   = FieldError(field.path, FW.empty(field))
    def nonEmpty[A](field: Field[A]): FieldError[E]                = FieldError(field.path, FW.nonEmpty(field))
    def minSize[A](size: Int)(field: Field[A]): FieldError[E]      = FieldError(field.path, FW.minSize(size)(field))
    def maxSize[A](size: Int)(field: Field[A]): FieldError[E]      = FieldError(field.path, FW.maxSize(size)(field))
    def oneOf[A](variants: Seq[A])(field: Field[A]): FieldError[E] = FieldError(field.path, FW.oneOf(variants)(field))
    def message[A](error: String, message: Option[String])(field: Field[A]): FieldError[E]        =
      FieldError(field.path, FW.message(error, message)(field))
    def compare[A](operation: CompareOperation, compared: String)(field: Field[A]): FieldError[E] =
      FieldError(field.path, FW.compare(operation, compared)(field))
  }

  /** String [[FailWith]] instance where errors are human-readable error messages */
  implicit object FailWithValidationMessage extends FailWith[String] {
    def invalid[A](field: Field[A]): String                 = ValidationMessages.Invalid
    def empty[A](field: Field[A]): String                   = ValidationMessages.Empty
    def nonEmpty[A](field: Field[A]): String                = ValidationMessages.NonEmpty
    def minSize[A](size: Int)(field: Field[A]): String      = ValidationMessages.MinSize(size)
    def maxSize[A](size: Int)(field: Field[A]): String      = ValidationMessages.MaxSize(size)
    def oneOf[A](variants: Seq[A])(field: Field[A]): String = ValidationMessages.OneOf(variants.map(_.toString))
    def message[A](error: String, message: Option[String])(field: Field[A]): String        = message.getOrElse(error)
    def compare[A](operation: CompareOperation, compared: String)(field: Field[A]): String =
      operation match {
        case CompareOperation.Equal        => ValidationMessages.Equal(compared)
        case CompareOperation.NotEqual     => ValidationMessages.NotEqual(compared)
        case CompareOperation.Greater      => ValidationMessages.Greater(compared)
        case CompareOperation.GreaterEqual => ValidationMessages.GreaterEqual(compared)
        case CompareOperation.Less         => ValidationMessages.Less(compared)
        case CompareOperation.LessEqual    => ValidationMessages.LessEqual(compared)
      }
  }

  /** String [[FailWith]] instance where errors are just error types that occured */
  implicit object FailWithValidationType extends FailWith[String] {
    def invalid[A](field: Field[A]): String                                                = ValidationTypes.Invalid
    def empty[A](field: Field[A]): String                                                  = ValidationTypes.Empty
    def nonEmpty[A](field: Field[A]): String                                               = ValidationTypes.NonEmpty
    def minSize[A](size: Int)(field: Field[A]): String                                     = ValidationTypes.MinSize
    def maxSize[A](size: Int)(field: Field[A]): String                                     = ValidationTypes.MaxSize
    def oneOf[A](variants: Seq[A])(field: Field[A]): String                                = ValidationTypes.OneOf
    def message[A](error: String, message: Option[String])(field: Field[A]): String        = error
    def compare[A](operation: CompareOperation, compared: String)(field: Field[A]): String =
      operation match {
        case CompareOperation.Equal        => ValidationTypes.Equal
        case CompareOperation.NotEqual     => ValidationTypes.NotEqual
        case CompareOperation.Greater      => ValidationTypes.Greater
        case CompareOperation.GreaterEqual => ValidationTypes.GreaterEqual
        case CompareOperation.Less         => ValidationTypes.Less
        case CompareOperation.LessEqual    => ValidationTypes.LessEqual
      }
  }

  /** FailWithValidationMessage wrapper with [[FieldError]] in */
  implicit object FailWithFieldValidationMessage extends FailWithFieldError[String](FailWithValidationMessage)

  /** FailWithValidationType wrapper with [[FieldError]] in */
  implicit object FailWithFieldValidationType extends FailWithFieldError[String](FailWithValidationType)
}
