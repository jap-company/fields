# FailWith

Fields has capability for user to use his own error type and this raises question how should library know for standart validation which error to use? `FailWith*` typeclasses gives this library ability to fail with specific errors.
This way you do not need to define all mappings to start using library if you do not intend to use some validation syntax.
First parameter `E` for each `FailWith*` is error for which you define capability to fail with and second parameter `P` stands for Field type, so that for different Field types there can be different `FailWith*` instances.

Here is list of available typeclasses:

```scala
trait FailWith[E, +P]
    extends FailWithMessage[E, P]
    with FailWithCompare[E, P]
    with FailWithInvalid[E, P]
    with FailWithEmpty[E, P]
    with FailWithNonEmpty[E, P]
    with FailWithMinSize[E, P]
    with FailWithMaxSize[E, P]
    with FailWithOneOf[E, P]
```

## Predefined

There are predefined FailWith instances for:

- ValidationMessage.failWith
- ValidationError.failWith
- FailWith.string.errorType - String with error type
- FailWith.string.errorMessage - String with error message
- FailWith.string.pathAndMessage - String with path and message
- FailWithFieldStringValidationType - FieldError\[String\] with validation type
- FailWithFieldStringValidationMessage - FieldError\[String\] with validation message
- FailWith.Builder - for building FailWith instances
- FailWith.Mapped - for mapping one FailWith to another

## ValidationDsl

Recommended place for FailWith instance is inside ValidationDsl for default FailWith instances there is trait with instance name prefixed with Can that you can mix into your ValidationDsl. For custom FailWith instances you can follow same practise.

```scala
object FutureValidation extends AccumulateDsl[Future, ValidationMessage] with CanFailWithValidationMessage
```

## Definition

You can can implement only required `FailWith*` types or extends `FailWith` and implement all of them.
Here is example FailWith for String:

```scala
implicit object FailWithValidationType extends FailWith.Base[String] {
  def invalid[P](field: Field[P]): String                                                = ValidationTypes.Invalid
  def empty[P](field: Field[P]): String                                                  = ValidationTypes.Empty
  def nonEmpty[P](field: Field[P]): String                                               = ValidationTypes.NonEmpty
  def minSize[P](size: Int)(field: Field[P]): String                                     = ValidationTypes.MinSize
  def maxSize[P](size: Int)(field: Field[P]): String                                     = ValidationTypes.MaxSize
  def oneOf[P](variants: Seq[P])(field: Field[P]): String                                = ValidationTypes.OneOf
  def message[P](error: String, message: Option[String])(field: Field[P]): String        = error
  def compare[P](operation: CompareOperation, compared: String)(field: Field[P]): String = operation.constraint
}
```

## Property specific

Some sunny day you may find that you want to have custom logic for failing `Field[P]`.
The same day you can define field type specific `FailWith*` instance:

```scala mdoc
import fields._
import fields.fail._
import fields.error._
import fields.value._

object Validation extends FieldsDsl.accumulate {
  implicit object IntFailWith
      extends FailWithInvalid[ValidationError, Int]
      with FailWithEmpty[ValidationError, Int] {
    def invalid[P >: Int](path: FieldPath, value: P): ValidationError = ValidationError.Message(path, "Invalid int")
    def empty[P >: Int](path: FieldPath, value: P): ValidationError   = ValidationError.Message(path, "Empty int")
  }
}
import Validation._

val intF    = Field(1)
val stringF = Field("1")
intF.failInvalid
intF.failEmpty
stringF.failInvalid
stringF.failEmpty
```
