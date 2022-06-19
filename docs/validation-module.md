# Module

All syntax comes from ValidationModule and if default ones do not suit your needs you can define custom.

User can use any Effect, Validated and Error types that has corresponding typeclass instances.

```scala mdoc
import jap.fields._
import jap.fields.data._
import jap.fields.error._
import jap.fields.typeclass._
// Note: this expects you to have such implicits:
// Effect[Effect.Sync]
// Validated[Accumulate]
object AccumulateMessage extends ValidationModule[Effect.Sync, Accumulate, ValidationMessage]
object AccumulateError extends AccumulateVM[Effect.Sync, ValidationError]
object FailFastError extends FailFastVM[Effect.Sync, ValidationError]
```

Then you just import all from ValidationModule

```scala
import AccumulateMessage._
```

Now you got all set up and can validate your Fields
