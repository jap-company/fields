# ValidationPolicy

Encapsulates `Field` validation logic. Also there is `ValidationPolicyBuilder` which provides convenient syntax to define `Field` validation logic

```scala
trait ValidationPolicy[P, F[_], VR[_], E] { self =>
  def validate(field: Field[P]): F[VR[E]]
}
```

## Syntax

```scala mdoc:reset-object
import jap.fields._
import jap.fields.DefaultAccumulateVM._

case class Email(value: String) extends AnyVal
object Email {
    //Policy is interface with 1 validate method, so you can do so
    implicit val policy: Policy[Email] = _.map(_.value).all(_.nonEmpty, _.maxSize(40))
}
case class Request(name: String, email: Email, age: Int, hasParrot: Boolean)
object Request {
  implicit val policy: Policy[Request] =
      Policy
        .builder[Request]
        .subRule(_.name)(_.minSize(4), _.maxSize(48)) //runs all validations combining using and
        .subRule(_.email)(_.validate) //Reuse Email Policy
        .subRule(_.age, _.hasParrot)((age, hasParrot) => age > 48 || (age > 22 && hasParrot.isTrue)) // 2 fields rule
        .build
}
Field(Request("", Email(""), 23, true)).validate // This will use implicit policy to validate
```
