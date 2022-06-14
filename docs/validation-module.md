# ValidationModule

All syntax comes from ValidationModule and if default ones do not suit your needs you can define custom.

User can use any ValidationEffect, ValidationResult and Error types that has corresponding typeclass instances.

```scala mdoc
import jap.fields._
// Note: this expects you to have such implicits:
// ValidationEffect[ValidationEffect.Sync]
// ValidationResult[Accumulate]
object AccumulateMessage extends ValidationModule[ValidationEffect.Sync, ValidationResult.Accumulate, ValidationError.Message]
object AccumulateError extends AccumulateVM[ValidationEffect.Sync, ValidationError]
object FailFastError extends FailFastVM[ValidationEffect.Sync, ValidationError]
```

Then you just import all from ValidationModule

```scala
import AccumulateMessage._
```

Now you got all set up and can validate your Fields
