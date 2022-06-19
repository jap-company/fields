# Rule

When using validation syntax result type of validation will be Rule\[F, V, E\] where F, V, E is your Effect, Validated and Error respectively.

No need to worry about additional allocations as Rule is tagged type for F\[V\[E\]\]

You can easily convert between Rule\[F, V, E\] and F\[V\[E\]\] back and forth for free

## Syntax

### Create

```scala mdoc
import cats.Eval
import jap.fields._
import jap.fields.error._
import jap.fields.fail._
import jap.fields.CatsInterop.fromCatsMonadDefer

object Validation extends AccumulateVM[Eval, ValidationError] with CanFailWithValidationError
import Validation._

def error(path: String) = ValidationError.Invalid(FieldPath(path))

List[MRule](
    Rule.valid,
    Rule.invalid(error("Rule.invalid")),
    Rule.pure(V.invalid(error("Rule.pure"))),
    Rule.effect(Eval.now(V.invalid(error("Rule.effect")))),
    Rule.defer(Rule.invalid(error("Rule.defer"))),
    Rule(Eval.later(V.invalid(error("Rule.apply"))))
).map(_.effect.value)
```

### Operations

```scala mdoc
Rule.invalid("Rule.unwrap").unwrap.value
Rule.invalid("Rule.effect").effect.value
Rule.and(Rule.invalid("Rule.and.1"), Rule.invalid("Rule.and.2")).effect.value
Rule.or(Rule.invalid("Rule.or"), Rule.valid).effect.value
Rule.fold(Rule.invalid("Rule.fold"))(_.toString, "Valid").value
Rule.when(true)(Rule.invalid("Rule.when")).effect.value
Rule.whenF(Eval.later(true))(Rule.invalid("Rule.whenF")).effect.value
Rule.ensure(V.invalid("Rule.ensure"))(false).effect.value
Rule.ensureF(V.invalid("Rule.ensure"))(Eval.later(false)).effect.value
Rule.andAll(List(Rule.invalid("Rule.andAll.1"), Rule.invalid("Rule.andAll.2"))).effect.value
Rule.orAll(List(Rule.invalid("Rule.andAll.1"), Rule.valid)).effect.value
Rule.modify(Rule.invalid(""))(_ => V.invalid("Rule.modify")).effect.value
Rule.modifyM(Rule.invalid(""))(_ => Rule.invalid("Rule.modifyM")).effect.value
```

### For-comprehension

Because Rule has custom map and flatMap you can also define validations like this:

```scala mdoc
val intF = Field(4)
val rule =
    for {
        _ <- intF > 4
        _ <- intF < 4
        _ <- intF !== 4
    } yield V.valid
```

Be aware this is experimental and requires yielding V.valid.
