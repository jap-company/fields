# FieldLens

Library is called Fields solely because it is built around `Field` data type. 
But creating Field instance for every validation can be memory consuming, 
so the new modules fields-lens was introduced built around lenses.

`FieldLens[P, +V]` has _path_ of type `FieldPath` and _selector_ of type `P => V`.

All validations are defined through syntax available for `FieldLens`

## Syntax

### Create

```scala mdoc:width=100
import fields.lens.FieldsDsl.default._

case class Request(name: String)
val request = Request("Ann")
FieldLens[Request]
FieldLens[Request](FieldPath.fromPath("request"))
FieldLens[Request].sub(_.name)
```

### Transform

```scala mdoc:width=100
case class B()
case class A(b: B)

val a = FieldLens[A]
a.sub(_.b)
a.down("b", _.b)
a.map(_.b)
a.mapPath(_ + "A")
a.named("A")
a.withPath(FieldPath.fromPath("b"))
a.withSelector(_.b)
```
