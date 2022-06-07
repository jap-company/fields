# FailWith

Fields has capability for user to use his own error type and this raises question how should library know for standart validation which error to use? `FailWith*` typeclasses gives this library ability to fail with specific errors.
This way you do not need to define all mappings to start using library if you do not intend to use some validation syntax.

Here is list of available typeclasses:

```scala
trait FailWith[E]
    extends FailWithMessage[E]
    with FailWithCompare[E]
    with FailWithInvalid[E]
    with FailWithEmpty[E]
    with FailWithNonEmpty[E]
    with FailWithMinSize[E]
    with FailWithMaxSize[E]
    with FailWithOneOf[E]
```

If you want to have all syntax working you can extends `FailWith` and not specific traits and implement the mapping.
Here is example FailWith[String]

```scala
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
```
