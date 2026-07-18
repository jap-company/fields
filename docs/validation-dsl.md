# Fields DSL

All syntax comes from FieldsDsl and if default ones do not suit your needs you can define custom.
You can use configure to use any Effect, Validated and Error types that has corresponding typeclass instances to use them with FieldsDsl.
Currently there is 2 modules value and lens both of them have FieldsDsl class which behave in similar way.

## Predefined DSL
There are some predefined DSLs that you can use out of the box.
Also some convenience methods to create your own DSLs.

- FieldsDsl.default, FieldsDsl.accumulate -> FieldsDsl[Effect.Sync, Accumulate, ValidationMessage]
- FieldsDsl.failFast -> FieldsDsl[Effect.Sync, FailFast, ValidationMessage]
- FieldsDsl.BaseFailFast[F[_], E] -> FieldsDsl[F, FailFast, E]
- FieldsDsl.BaseAccumulate[F[_], E] -> FieldsDsl[F, Accumulate, E]

## Your DSL

Just create your own DSL with your own Effect, Validated and Error types by extending FieldsDsl.
Also make sure typeclass FailWith instance is available for your error type by implicit search.
Then you just import all from ValidationDsl and you are good to go.

```scala mdoc
import fields._
import fields.error._
import fields.typeclass._
import fields.value.FieldsDsl
// Note: this expects you to have such implicits:
// Effect[Effect.Sync]
// Validated[Accumulate]
object AccumulateMessage extends FieldsDsl[Effect.Sync, Accumulate, ValidationMessage]
object AccumulateError extends FieldsDsl.BaseAccumulate[Effect.Sync, ValidationError]
object FailFastError extends FieldsDsl.BaseFailFast[Effect.Sync, ValidationError]
```
