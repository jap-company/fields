# ZIO Blocks Schema

The `@zioBlocksSchemaModuleName@` module lets Fields policies participate in ZIO Blocks schema decoding without converting errors to a Fields-specific error model. It provides native `zio.blocks.schema.SchemaError` instances with native `DynamicOptic` paths and depends only on Fields Core. Applications select the Value or Lens front end and define the matching DSL locally.

```scala
libraryDependencies ++= Seq(
  "@organization@" %% "@zioBlocksSchemaModuleName@" % "@version@",
  "@organization@" %% "@valueModuleName@" % "@version@" // or @lensModuleName@
)
```

The compatibility module supports Scala 2.13 and Scala 3, matching ZIO Blocks Schema. The same artifact works with both Fields front ends without depending on either one.

## Native error DSL

For the Value front end, define a synchronous accumulating DSL and mix in `ZioBlocksSchemaErrorDsl`. Schema transforms run synchronously, and `SchemaError` can aggregate independent failures.

```scala mdoc
import fields._
import fields.ZioBlocksSchemaSyntax._
import fields.typeclass.Effect
import zio.blocks.schema.Schema
import zio.blocks.schema.SchemaError

object Validation
    extends fields.value.FieldsDsl.BaseAccumulate[Effect.Sync, SchemaError]
    with ZioBlocksSchemaErrorDsl
import Validation._

val itemPolicy: Policy[List[String]] = field =>
  Rule.andAll(
    field.value.zipWithIndex.map { case (value, index) =>
      field.down(index, value).nonEmpty
    }
  )
```

## Mounting a policy

`withPolicy` returns a schema with the policy mounted in its construction path. ZIO Blocks runs the policy while decoding or constructing from a `DynamicValue`. Valid values decode normally; failures remain native `SchemaError` values.

```scala mdoc:silent
val itemSchema = Schema[List[String]].withPolicy(itemPolicy)
```

```scala mdoc
val input = List("", "valid", "")

val result = itemSchema.fromDynamicValue(itemSchema.toDynamicValue(input))
result.left.map(_.message)
```

ZIO Blocks 0.0.33 represents a thrown transform error as one native `SchemaError.ConversionFailed`. Its details contain the accumulated policy messages and paths. This is native ZIO Blocks transform behavior.

When code needs the individual structured errors rather than the schema-transform envelope, use `validateWithPolicy`:

```scala mdoc
itemSchema.validateWithPolicy(itemPolicy)(input)
  .left.map(_.errors.map(_.source.toString))
// Left(List("[0]", "[2]"))
```

Mounting validates the schema's decoding/construction direction. Encoding uses the original value unchanged; call the policy directly when an already-constructed in-memory value must be checked before encoding.

## Lens policies

For the Lens front end, define the equivalent DSL using `fields.lens.FieldsDsl`. The schema methods have explicit lens names so importing schema syntax never creates an ambiguous `withPolicy` overload.

```scala mdoc:silent
object LensValidation
    extends fields.lens.FieldsDsl.BaseAccumulate[Effect.Sync, SchemaError]
    with ZioBlocksSchemaErrorDsl
import LensValidation._

val lensPolicy: LensValidation.LensPolicy[String] = LensPolicy.root[String](_.nonEmpty)
val lensSchema = Schema[String].withLensPolicy(lensPolicy.root)  
```
```scala mdoc

lensSchema.validateWithLensPolicy(lensPolicy.root)("")
  .left.map(_.errors.map(_.source.toString))
// Left(List(""))
```
