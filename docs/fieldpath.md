# FieldPath

Stores `Field` _path_, that at the end of the day is used to know where some ValidationError was raised.

Various ways to create and transform `FieldPath` is described in [Syntax](#syntax) section

## Syntax

### Create

```scala mdoc
import jap.fields._

FieldPath.Root
FieldPath("request", "name")
FieldPath(List("request", "name"))
FieldPath.raw("request.name")
```

### Operations

```scala mdoc
val path = FieldPath("a", "b")
path.isRoot
path.full
path.name
path.named("c")
path ++ FieldPath("d")
path + "d"
```

### Conversions

```scala mdoc
import jap.fields.FieldPathConversions._

Field(FieldPath("name"), ""): FieldPath
"name": FieldPath
List("request", "name"): FieldPath
```
