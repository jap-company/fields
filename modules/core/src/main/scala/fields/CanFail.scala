package jap.fields

import scala.annotation.implicitNotFound
import scala.annotation.varargs

import ValidationError._

@implicitNotFound("To use this operation you need to have CanFailInvalid[${E}] in scope")
trait CanFailInvalid[E] { def invalid[A](field: Field[A]): E }
object CanFailInvalid   {
  implicit def wrapFieldError[E](implicit CF: CanFailInvalid[E]): CanFailInvalid[FieldError[E]] =
    new CanFailInvalid[FieldError[E]] {
      def invalid[A](field: Field[A]): FieldError[E] = FieldError(field.path, CF.invalid(field))
    }
}

@implicitNotFound("To use this operation you need to have CanFailEmpty[${E}] in scope")
trait CanFailEmpty[E]    { def empty[A](field: Field[A]): E    }
object CanFailEmpty      {
  implicit def wrapFieldError[E](implicit CF: CanFailEmpty[E]): CanFailEmpty[FieldError[E]] =
    new CanFailEmpty[FieldError[E]] {
      def empty[A](field: Field[A]): FieldError[E] = FieldError(field.path, CF.empty(field))
    }
}
@implicitNotFound("To use this operation you need to have CanFailNonEmpty[${E}] in scope")
trait CanFailNonEmpty[E] { def nonEmpty[A](field: Field[A]): E }
object CanFailNonEmpty   {
  implicit def wrapFieldError[E](implicit CF: CanFailNonEmpty[E]): CanFailNonEmpty[FieldError[E]] =
    new CanFailNonEmpty[FieldError[E]] {
      def nonEmpty[A](field: Field[A]): FieldError[E] = FieldError(field.path, CF.nonEmpty(field))
    }
}

sealed trait CompareOperation
object CompareOperation {
  case object Equal        extends CompareOperation
  case object NotEqual     extends CompareOperation
  case object Greater      extends CompareOperation
  case object GreaterEqual extends CompareOperation
  case object Less         extends CompareOperation
  case object LessEqual    extends CompareOperation
}

@implicitNotFound("Cannot compare ${P} with ${C}")
trait FieldCompare[P, C] {
  def value(c: C): P
  def show(compared: C): String
}
object FieldCompare      {
  def apply[P, C](implicit C: FieldCompare[P, C]): FieldCompare[P, C] = C

  implicit def valueWithValueCompare[P]: FieldCompare[P, P] =
    anyDefaultCompared.asInstanceOf[FieldCompare[P, P]]

  implicit def fieldWithFieldCompare[P]: FieldCompare[P, Field[P]] =
    anyFieldCompared.asInstanceOf[FieldCompare[P, Field[P]]]

  private val anyDefaultCompared: FieldCompare[Any, Any] = new FieldCompare[Any, Any] {
    def value(c: Any): Any   = c
    def show(c: Any): String = c.toString
  }

  private val anyFieldCompared: FieldCompare[Any, Field[Any]] = new FieldCompare[Any, Field[Any]] {
    def value(c: Field[Any]): Any   = c.value
    def show(c: Field[Any]): String = c.path.full
  }
}
@implicitNotFound("To use this operation you need to have CanFailCompare[${E}] in scope")
trait CanFailCompare[E]  {
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
object CanFailCompare    {
  implicit def wrapFieldError[E](implicit CF: CanFailCompare[E]): CanFailCompare[FieldError[E]] =
    new CanFailCompare[FieldError[E]] {
      def compare[A](operation: CompareOperation, compared: String)(field: Field[A]): FieldError[E] =
        FieldError(field.path, CF.compare(operation, compared)(field))
    }
}

@implicitNotFound("To use this operation you need to have CanFailMinSize[${E}] in scope")
trait CanFailMinSize[E] { def minSize[A](size: Int)(field: Field[A]): E }
object CanFailMinSize   {
  implicit def wrapFieldError[E](implicit CF: CanFailMinSize[E]): CanFailMinSize[FieldError[E]] =
    new CanFailMinSize[FieldError[E]] {
      def minSize[A](size: Int)(field: Field[A]): FieldError[E] = FieldError(field.path, CF.minSize(size)(field))
    }
}

@implicitNotFound("To use this operation you need to have CanFailMaxSize[${E}] in scope")
trait CanFailMaxSize[E] { def maxSize[A](size: Int)(field: Field[A]): E }
object CanFailMaxSize   {
  implicit def wrapFieldError[E](implicit CF: CanFailMaxSize[E]): CanFailMaxSize[FieldError[E]] =
    new CanFailMaxSize[FieldError[E]] {
      def maxSize[A](size: Int)(field: Field[A]): FieldError[E] = FieldError(field.path, CF.maxSize(size)(field))
    }
}

@implicitNotFound("To use this operation you need to have CanFailOneOf[${E}] in scope")
trait CanFailOneOf[E] { def oneOf[A](variants: Seq[A])(field: Field[A]): E }
object CanFailOneOf   {
  implicit def wrapFieldError[E: CanFailOneOf](implicit CF: CanFailOneOf[E]): CanFailOneOf[FieldError[E]] =
    new CanFailOneOf[FieldError[E]] {
      def oneOf[A](variants: Seq[A])(field: Field[A]): FieldError[E] = FieldError(field.path, CF.oneOf(variants)(field))
    }
}

@implicitNotFound("To use this operation you need to have CanFailMessage[${E}] in scope")
trait CanFailMessage[E] {
  def message[A](error: String, message: Option[String])(field: Field[A]): E
}
object CanFailMessage   {
  implicit def wrapFieldError[E](implicit CF: CanFailMessage[E]): CanFailMessage[FieldError[E]] =
    new CanFailMessage[FieldError[E]] {
      def message[A](error: String, message: Option[String])(field: Field[A]): FieldError[E] =
        FieldError(field.path, CF.message(error, message)(field))
    }
}

/** Aggregates all possible CanFail* typeclasses shorthand if you need all of them. If are free to implemented only
  * those you will use. Best practise will be to but implicit instance of this into companion object of your error.
  */
trait CanFail[E]
    extends CanFailMessage[E]
    with CanFailCompare[E]
    with CanFailInvalid[E]
    with CanFailEmpty[E]
    with CanFailNonEmpty[E]
    with CanFailMinSize[E]
    with CanFailMaxSize[E]
    with CanFailOneOf[E]

object CanFail {

  /** Default [jap.fields.CanFail] instance if your error is just plain string
    */
  implicit object StringCanFail extends CanFail[String] {
    def invalid[A](field: Field[A]): String                 = s"should be valid"
    def empty[A](field: Field[A]): String                   = s"shoult not be empty"
    def nonEmpty[A](field: Field[A]): String                = s"should be empty"
    def minSize[A](size: Int)(field: Field[A]): String      = s"should have size greater than $size"
    def maxSize[A](size: Int)(field: Field[A]): String      = s"should have size less than $size"
    def oneOf[A](variants: Seq[A])(field: Field[A]): String = s"should be one of $variants"
    def compare[A](operation: CompareOperation, compared: String)(field: Field[A]): String =
      operation match {
        case CompareOperation.Equal        => s"should be equal to $compared"
        case CompareOperation.NotEqual     => s"should not be equal to $compared"
        case CompareOperation.Greater      => s"should be greater than $compared"
        case CompareOperation.GreaterEqual => s"should be greater/equal to $compared"
        case CompareOperation.Less         => s"should be less than $compared"
        case CompareOperation.LessEqual    => s"should be less/equal $compared"
      }
    def message[A](error: String, message: Option[String])(field: Field[A]): String        =
      error + message.fold("")(" - " + _)
  }
}
