# Syntax

Here is complete list of predefined validation syntax.

## Generic

```scala mdoc:width=100
import jap.fields._
import jap.fields.DefaultAccumulateVM._
import jap.fields.FieldPathConversions._

val field: Field[Int] = Field("1", 1)
field.ensure(_ == 3, _.failInvalid)
field.assert(_ == 3, _.invalidError)
field.check(f => if(false) f.failMessage("A") else f.failMessage("B"))
field equalTo 2
field notEqualTo 1
field equalTo Field("2", 2)
field notEqualTo Field("1", 1)
field in List(2, 3)
field.all(_ === 2, _ !== 1)
field.any(_ === 2, _ !== 3)
field.when(false)(_ !== field.value)
field.unless(true)(_ !== field.value)
implicit val policy: Policy[Int] = _ < 0
field.validate
```

## Boolean

```scala mdoc:width=100
Field("false", false).isTrue
Field("true", true).isFalse
```

## Ordering

```scala mdoc:width=100
import java.time.LocalDateTime
val now = LocalDateTime.now
val nowF = Field.from(now)
val tomorrow = now.plusDays(1)
val yesterday = now.minusDays(1)

nowF.isBetween(tomorrow, yesterday)
nowF < yesterday
nowF <= yesterday
nowF >= tomorrow
nowF > tomorrow
```

## Option

```scala mdoc:width=100
Field(None).isDefined
val someF: Field[Option[Int]] = Field(FieldPath("a"), Some(5))
someF.isEmpty
someF.some(_ > 10)

someOrValid {
    for {
        option <- someF.option
        other <- Field(FieldPath("b"), Some(2)).option
    } yield option < other
}
```

## String

```scala mdoc:width=100
val stringF: Field[String] = Field("Ann")
stringF.startsWith("sca")
stringF.endsWith("la")
Field("").nonEmpty
Field("").nonBlank
stringF.minSize(5)
stringF.maxSize(1)
stringF.blank
stringF.matches("scala".r)
stringF.matches("scala")
```

## Iterable

```scala mdoc:width=100
val listF: Field[List[Int]] = Field(List(1, 12))
listF.each(_ > 10)
listF.any(_ === 10)
Field(List()).nonEmpty
listF.minSize(3)
listF.maxSize(1)
```

## Map

```scala mdoc:width=100
val mapF: Field[Map[String, Int]] = Field(Map("" -> 2, "2" -> 2))
mapF.minSize(4)
mapF.maxSize(1)
mapF.each(_.second > 4)
mapF.eachKey(_.nonEmpty)
mapF.eachValue(_ > 4)
mapF.any(_.second > 4)
mapF.anyKey(_ === "4")
mapF.anyValue(_ > 4)
```

## Effectful

```scala mdoc:reset:width=100
import jap.fields._
import jap.fields.ZioInterop._
import jap.fields.fail._
import jap.fields.error._
import zio._

object Validation extends AccumulateVM[Task, ValidationError] with CanFailWithValidationError
import Validation._

def unsafeRun[A](task: Task[A]) = Runtime.global.unsafeRun(task)
def isPositiveApi(number: Int): zio.Task[Boolean] = zio.UIO(number > 0)

val field = Field(FieldPath("size"), -1)
unsafeRun(field.ensureF(isPositiveApi, _.failMessage("API: NOT POSITIVE")).effect)
unsafeRun(field.assertF(isPositiveApi, _.messageError("API: NOT POSITIVE")).effect)
```
