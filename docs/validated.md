# Validated

Defines Validated capabilities for `V[_]`, so that Fields know how to use it when validating.
Also this typeclass has `strategy` field that will give a hint for short-circuiting while validating.

## Instances

Predefined instances:

- `jap.fields.data.Accumulate` - accumulates errors.
- `jap.fields.data.FailFast` - holds first error that occured. Built using `Either`
- `cats.data.ValidatedNel[_, Unit]` - accumulates. Part of cats module
- `cats.data.ValidatedNec[_, Unit]` - accumulates. Part of cats module
- `List` - accumulates errors. If list is empty means result is valid else contains errors.
- `Either[_, Unit]` - fail fast error. `Right[Unit]` is valid, `Left[E]` is invalid holding error.
- `Option` - fail fast error. `None` is valid, `Some[E]` is invalid holding error

If you need you can use your own Validated data type by creating typeclass instance for it.
Extend `AccumulateLike` if your type should accumulate errors or if it should fail with first error occured use `FailFastLike`

### Accumulate

Validated data type that accumulates errors.

```scala
sealed trait Accumulate[+E]
object Accumulate {
  case object Valid                       extends Accumulate[Nothing]
  case class Invalid[+E](errors: List[E]) extends Accumulate[E]
}
```

### FailFast

Validated data type that returns first error occured. Declared as `Option[E]` tagged type.

- `None` is valid
- `Some[E]` holds the error

## Syntax

Having Validated for your `V[_]` in scope you can use such syntax

### Create

```scala mdoc
import jap.fields._
import jap.fields.data.Accumulate
import jap.fields.typeclass.Validated
import jap.fields.syntax.ValidatedSyntax._

val V: Validated[Accumulate] = Accumulate
val vr1 = V.valid
val vr2 = V.invalid("ERR01")
val vr3 = "ERR02".invalid[Accumulate]
val vr4 = V.traverse(List("ERR01", "ERR02"))(V.invalid)
V.sequence(List(vr1, vr2, vr3))
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

V.traverse is very useful when you want to fail multiple Field`s with same error

```scala
import jap.fields.DefaultAccumulateVM._
V.traverse(Field(FieldPath("1"), 1), Field(FieldPath("2"), 2))(_.failMessage("ERROR"))
```
