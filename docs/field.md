# Field

Library is called Fields solely because it is built around `Field` data type.

`Field[P]` has _path_ of type `FieldPath` and _value_ of type `P`.

All validations are defined throught syntax available for `Field`

Various ways to create and transform `Field` is described in [Syntax](#syntax) section

## Syntax

### Create

```scala mdoc:width=100
import jap.fields._
import jap.fields.DefaultAccumulateVM._

case class Request(name: String)
val request = Request("Ann")
Field(request.name)
Field(FieldPath("request", "name"),request.name)
Field.from(request.name)
Field.sub(request.name)
```

### Transform

```scala mdoc:width=100
case class B()
case class A(b: B)
val a = Field(FieldPath("a"), A(B()))
a.sub(_.b)
a.provideSub("b", a.value.b)
a.selectSub("b", _.b)
a.map(_.b)
a.mapPath(_ + "A")
a.named("A")
a.withPath(FieldPath("b"))
a.withValue(3)
```

### Special

```scala mdoc:width=100
Field(1 -> "2").first
Field(1 -> "2").second
Field(Option(1)).option
```
