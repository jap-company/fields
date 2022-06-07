# FieldPath

Stores `Field` _path_, that at the end of the day is used to know where some ValidationError was raised.

Various ways to create and transform `FieldPath` is described in [Syntax](#syntax) section

## Syntax

```scala
FieldPath.root
FieldPath("request", "name")
FieldPath(List("request", "name"))
FieldPath.raw("request.name")

//Implicit Conversion
val path: FieldPath = nameF
val path: FieldPath = "name"
val path: FieldPath = List("request", "name")
```
