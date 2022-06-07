# ValidationPolicy

Encapsulates `Field` validation logic. Also there is `ValidationPolicyBuilder` which provides convenient syntax to define `Field` validation logic

```scala
trait ValidationPolicy[P, F[_], VR[_], E] { self =>
  def validate(field: Field[P]): F[VR[E]]
}
```

## Syntax

```scala
case class Email(value: String) extends AnyVal
object Email {
    //Policy is interface with 1 validate method, so you can do so
    implicit val policy: Policy[Email] = _.map(_.value).all(_.nonEmpty, _.max(40))
}
case class Request(name: String, email: Email, age: Int, hasParrot: Boolean)
object Request {
  implicit val policy: Policy[Request] =
      Policy
        .builder[Request]
        .subRule(_.name)(_.min(4), _.max(48)) //runs all validations combining using and
        .subRule(_.email)(_.validate) //Reuse Email Policy
        .subRule2(_.age, _.hasParrot)((age, hasParrot) => age > 48 || (age > 22 && hasParrot.isTrue)) // 2 fields rule
        .build
}
val request: Request = ???
val requestF = Field.from(request)
requestF.validate // This will use implicit policy to validate
```
