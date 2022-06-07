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
    override def invalidMany[E](eh: E, et: E*): FailFast[E] = Left(eh)
    def invalid[E](e: E): FailFast[E]                       = Left(e)
    def isValid[E](e: FailFast[E]): Boolean                 = e.isRight
    def and[E](a: FailFast[E], b: FailFast[E]): FailFast[E] = a.flatMap(_ => b)
    def errors[E](vr: FailFast[E]): List[E]                 = vr.left.toSeq.toList
}
```

## Syntax

Having ValidationResult for your `VR[_]` in scope you can use such syntax

```scala
val vr1 = nameF.nonEmpty
val vr2 = surnameF.nonEmpty
vr1.isValid
vr1.isInvalid
vr1.errors // list of errors
vr1 && vr2 //Logical AND
vr1 and vr2 //Logical AND
vr1 || vr2 //Logical OR
vr1 or vr2 //Logical OR
List(vr1, vr2).combineAll //combine all using and
combineAll(List(vr1, vr2)) //combine all using and
```
