# ValidationResult

Defines ValidationResult capabilities for `VR[_]`, so that Fields know how to use it when validating.

## Instances

Predefined instances:

- `jap.fields.ValidationResult.Accumulate` - accumulates errors.
- `jap.fields.ValidationResult.FailFast` - holds first error that occured. Built using `Either`
- `cats.data.ValidatedNel[_, Unit]` - part of cats module
- `cats.data.ValidatedNec[_, Unit]` - part of cats module

If you need you can use your own ValidationResult data type by creating typeclass instance for it.
Here is how it is implemented for FailFast

```scala
type FailFast[+E] = Either[E, Unit]
implicit object FailFast extends FailFastLike[FailFast] {
    def map[E, B](a: FailFast[E])(f: E => B): FailFast[B]   = a.left.map(f)
    def valid[E]: FailFast[E]                               = Right(())
    def invalid[E](e: E): FailFast[E]                       = Left(e)
    def isValid[E](e: FailFast[E]): Boolean                 = e.isRight
    def and[E](a: FailFast[E], b: FailFast[E]): FailFast[E] = a.flatMap(_ => b)
    def errors[E](vr: FailFast[E]): List[E]                 = vr.left.toSeq.toList
    override def invalidMany[E](eh: E, et: E*): FailFast[E] = Left(eh)
}
```

## Syntax

Having ValidationResult for your `VR[_]` in scope you can use such syntax

### Create

```scala mdoc
import jap.fields._
import jap.fields.ValidationResult._
import jap.fields.syntax.ValidationResultSyntax._

val VR: ValidationResult[Accumulate] = Accumulate
val vr1 = VR.valid
val vr2 = VR.invalid("ERR01")
val vr3 = "ERR02".invalid[Accumulate]
val vr4 = VR.invalidMany("ERR03", "ERR04")
```

### Operations

```scala mdoc
vr1.isValid
vr2.when(false)
vr2.unless(true)
vr2.asError("ERROR02")
vr2.asInvalid(vr4)
vr2.isInvalid
vr2.errors
vr1 && vr2
vr2.and(vr3)
vr1 || vr2
vr2.or(vr3)
List(vr1, vr2, vr3).sequence
List(vr1, vr1).sequence
```

### Fail Multiple Fields

VR.traverse is very useful when you want to fail multiple Field`s with same error

```scala
import jap.fields.DefaultAccumulateVM._
VR.traverse(Field(FieldPath("1"), 1), Field(FieldPath("2"), 2))(_.failMessage("ERROR"))
```
