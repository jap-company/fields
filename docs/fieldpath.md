# FieldPath

Stores `Field` _path_, that at the end of the day is used to know where some ValidationError was raised.

Various ways to create and transform `FieldPath` is described in [Syntax](#syntax) section

## Syntax

### Create

```scala mdoc
import jap.fields._

FieldPath.Root
FieldPath(FieldPart.Path("request"), FieldPart.Index(2))
FieldPath.fromPaths("request", "name")
FieldPath.fromPath("request")
FieldPath.fromIndex(12)
FieldPath.parse("request.name[1]")
```

### Operations

```scala mdoc
val path = FieldPath.parse("a.b")
path.isRoot
path.full
path.name
path.named("c")
path ++ FieldPath.fromPath("d")
path + "d"
path + FieldPart.Path("d")
path + 2
path.down(2)
path.down("d")
path.down(FieldPart.Path("d"))
```

### Conversions

```scala mdoc
import jap.fields.FieldPathConversions._

Field(FieldPath.fromPath("name"), ""): FieldPath
"name": FieldPath
2: FieldPath
List("request", "name"): FieldPath
```
