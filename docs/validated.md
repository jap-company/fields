# Validated

Defines Validated capabilities for `V[_]`, so that Fields know how to use it when validating.
Also this typeclass has `strategy` field that will give a hint for short-circuiting while validating.

## Instances

Predefined instances:

- `List` alised as `jap.fields.Accumulate` - accumulates errors. `Nil` is valid, `List[E]` is invalid holding errors
- `Option` aliased as `jap.fields.FailFast` - holds first error that occured. `None` is valid, `Some[E]` is invalid holding error
- `cats.data.ValidatedNel[_, Unit]` - accumulates. Part of cats module
- `cats.data.ValidatedNec[_, Unit]` - accumulates. Part of cats module
- `Either[_, Unit]` - fail fast error. `Right[Unit]` is valid, `Left[E]` is invalid holding error.

If you need you can use your own Validated data type by creating typeclass instance for it.
Extend `AccumulateLike` if your type should accumulate errors or if it should fail with first error occured use `FailFastLike`
You will need to implement a hew methods, here is example for `List`

```scala mdoc
import fields.typeclass.AccumulateLike
implicit object ListValidated extends AccumulateLike[List] {
    def valid[E]: List[E]                         = Nil
    def invalid[E](e: E): List[E]                 = List(e)
    def and[E](va: List[E], vb: List[E]): List[E] = va ::: vb
    def isValid[E](v: List[E]): Boolean           = v.isEmpty
    def map[E](v: List[E])(f: E => E): List[E]    = v.map(f)
}
```

## Syntax

Having Validated for your `V[_]` in scope you can use such syntax

### Create

```scala mdoc
import fields._
import fields.typeclass.Validated
import fields.syntax.ValidatedSyntax._

val V = Validated.Accumulate
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
import fields.value.FieldsDsl.default._
V.traverse(Field(FieldPath("1"), 1), Field(FieldPath("2"), 2))(_.failMessage("ERROR"))
```

### HasErrors
Helper typeclass that allows to extract errors from your `V[_]` type.

```scala mdoc
trait HasErrors[V[_]] {
  def errors[E](v: V[E]): List[E]
}
```
### HasFieldPath

Helper typeclass that allows to extract field path from your `E` error type.

```scala mdoc
trait HasFieldPath[E] {
  def getPath(e: E): FieldPath
}
```

### PrependPath

Helper typeclass that allows to prepend field path to your `E` error type. Mainly used for lens module

```scala mdoc
trait PrependPath[E] {
  def prependPath(path: FieldPath, e: E): E
}
```

### MapKeyToPart
Helper typeclass that allows to map key with index to FieldPart of your `E` error type. Used for MapSyntax for both value and lens modules

```scala mdoc
trait MapKeyToPart[K] {
  def toPart(key: K, index: Int): FieldPart
}
```