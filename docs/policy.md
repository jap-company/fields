# PolicyK and Policy

Encapsulates `Field` validation logic.

```scala
type PolicyK[-A, F[_], V[_], E] = A => RuleK[F, V, E]
```

## Syntax

```scala mdoc:reset-object
import fields._
import fields.value.FieldsDsl.default._

case class Email(value: String) extends AnyVal

object Email {
  //Policy is interface with 1 validate method, so you can do so
  implicit val policy: Policy[Email] = _.map(_.value).all(_.nonEmpty, _.maxSize(40))
}

case class Request(name: String, email: Email, age: Int, hasParrot: Boolean)

object Request {
  implicit val policy: Policy[Request] =
    Policy[Request]
      .subRule(_.name)(_.minSize(4), _.maxSize(48)) //runs all validations combining using and
      .subRule(_.email)(_.validate) //Reuse Email Policy
      .subRule(_.age, _.hasParrot)((age, hasParrot) => age > 48 || (age > 22 && hasParrot.isTrue)) // 2 fields rule
}

Field(Request("", Email(""), 23, true)).validate.effect // This will use implicit policy to validate
Field(Request("1234", Email("ann@gmail.com"), 23, true)).validateEither
```
