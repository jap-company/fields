# ValidationModule

All syntax comes from ValidationModule and if default ones do not suit your needs you can define custom.

This gives user ability to choose Effect - `F[_]`, ValidationResult - `VR[_]` and Error - `E` types

```scala
// Note: this expects you to have such implicits:
// ValidationEffect[ValidationEffect.Sync]
// ValidationResult[Accumulate]
object MyValidationModule extends ValidationModule[ValidationEffect.Sync, Accumulate, ValidationError]
object MyValidationModule extends AccumulateVM[ValidationEffect.Sync, ValidationError]
object MyValidationModule extends FailFastVM[ValidationEffect.Sync, ValidationError]
```

Then you should import all from ValidationModule

```scala
import MyValidationModule._
```

Now you got all set up and can validate your Fields
