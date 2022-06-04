![Result image](assets/jap-logo.png)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/company.jap/fields-core_2.13/badge.svg)](https://maven-badges.herokuapp.com/maven-central/company.jap/fields-core_2.13)
[![CI](https://github.com/jap-company/fields/workflows/CI/badge.svg)](https://github.com/jap-company/fields/actions?query=workflow%3A%22CI%22)

# What is this?
This is zero-dependency Scala(2.12/2.13/3) validation library featuring:

* Configurable Effect, ValidationResult, Error types
* Accumulate and FailFast validation strategies 
* Rich extendable validation syntax
* Error paths
* ZIO and Cats interop

## Getting Started
To start using this library add this `build.sbt`:

```scala
libraryDependencies ++= List(
    "company.jap" %% "fields-core" % "0.2.0",
    "company.jap" %% "fields-zio" % "0.2.0",
    "company.jap" %% "fields-cats" % "0.2.0",
)
```
Core concept of this validation library `Field` structure that stores field path and value and has syntax to create subfields that will carry parents path info.
You start by extending ValidationModule and choosing your Effect - F[_], ValidationResult - VR[_], Error - E types:
```scala
// Note: this expects you to have such implicits:
// ValidationEffect[ValidationEffect.Id]
// ValidationResult[Accumulate]
object MyValidationModule extends ValidationModule[ValidationEffect.Id, Accumulate, FieldError[ValidationError]]
object MyValidationModule extends AccumulateVM[ValidationEffect.Id, FieldError[ValidationError]]
object MyValidationModule extends FailFastVM[ValidationEffect.Id, FieldError[ValidationError]]
```
There are predefined typeclass instances:
* ValidationEffect for ValidationEffect.Id(sync), Future, ZIO and Cats Monad/Defer
* ValidationResult for Accumulate, FailFast, Cats ValidatedNel/ValidatedNeq

Important note if you want library to correctly handle short-circuiting you should use lazy ValidationEffect, if you don`t need async validation stick to cats.Eval

All predefined syntax requires your error to have specific FailWith* capabilities those are predefined for ValidationError, ValidationError.Message and String types. 
For your own error you can choose which mappings you want to implement and use and which not. To handle all FailWith* algebras extend FailWith trait.
Check jap.fields.ValidationError.FailWithValidationError for example implementation. 

Then you import all from ValidationModule:
```scala
import MyValidationModule._
```
Create your first Field and check it is not empty
```scala
case class RegisterRequest(name: String, surname: String)
val request = RegisterRequest("", "")
val requestF = Field.from(request)
val nameF = requestF.sub(_.name)
val surnameF = requestF.sub(_.surname)
println(nameF.nonEmpty && surnameF.nonEmpty) // Invalid(List(request.name -> empty, request.name -> empty))
```

Now you got the basics of fields, but there is still plenty of syntax to learn that will be described below. 
Also dont forget to check examples.

## Syntax
#### FieldPath
```scala
FieldPath.root
FieldPath("request", "name")
FieldPath(List("request", "name"))
FieldPath.raw("request.name")

//Implicit Conversion
val path: FieldPath = nameF
val path: FieldPath = "name"
val path: FieldPath = List("request", "name")
```

#### Field
```scala
val path: FieldPath = ???
Field(path, request.name) //Field(path, request.name) Using path and value
Field(request.name) //Field(FieldPath.root, request.name) Using value without path
Field.from(request.name) //Field(FieldPath("request", "name"), request.name) Innherit path from field selects

val requestF = Field.from(request)
requestF.sub(_.name) // Derive subfield using field selector
requestF.provideSub("name", request.name) // Manual subfield with provided value
requestF.selectSub("name", _.name) // Manual subfield with value selector
requestF.map(_.name) //Map only field value
requestF.mapPath(_.toUpperCase) //Map only field path
requestF.named("apiRequest")//Changes name of field - last FieldPath part
requestF.withPath(???)//Set Field path
requestF.withValue(???)//Set Field value

val tupleF = Field(1 -> "2")
tupleF.first//Field(tupleF.path, 1)
tupleF.second//Field(tupleF.path, "2")
```

#### VR\[E] and F\[VR\[E]]
```scala
val vr1 = nameF.nonEmpty
val vr2 = surnameF.nonEmpty
vr1.isValid
vr1.isInvalid
vr1.errors // list of errors
vr1 && vr2 //Logical AND
vr1 and vr2 //Logical AND
vr1 || vr2 //Logical OR
vr1 or vr2 //Logical OR
List(vr1, vr2).combineAll //combine all using and
combineAll(List(vr1, vr2)) //combine all using and
```

#### Validation syntax
```scala
//ANY FIELD
val request: Request = ???
val requestF: Field[Request] = Field(request)
requestF.assertTrue(false, _.invalidError)
requestF.assert(_.isValid, _.invalidError)
requestF.check(f => Accumulate.invalid(f.invalidError).unless(f.value.isValid))

def isRequestValidApi: zio.Task[Boolean] = ???
requestF.assertF(isRequestValidApi, _.invalid)
requestF.checkF(f => isRequestValidApi(f.value).map(Accumulate.unless(_)(c.custom("err"))))
requestF === request
requestF equalTo request
requestF !== request
requestF notEqualTo request
requestF === requestF
requestF equalTo requestF
requestF !== requestF
requestF notEqualTo requestF
requestF in List(request)
requestF.all(_ === request, !== request) // runs all validations and combines them using and
requestF.any(_ === request, !== request) // runs all validations and combines them using or
requestF.when(true)(_ === request) // runs if cond is true
requestF.unless(false)(_ === request) // runs if cond is false
requestF validate //uses ValidationPolicy

//BOOL FIELD
val boolF: Field[Boolean] = ???
boolF.isTrue
boolF.isFalse

//NUMERIC FIELD
val intF: Field[Int] = ???
infF.isBetween(0, 5)
infF < 10
infF lt 10
infF <= 10
infF lte 10
infF >= 10
infF gte 10
infF > 10
infF gt 10

//OPTION FIELD
val optionF: Field[Option[Int]] = ???
optionF.isDefined
optionF.isEmpty
optionF.some(_ > 10)

//STRING FIELD
val stringF: Field[String] = ???
stringF.startsWith("sca")
stringF.endsWith("la")
stringF.nonEmpty
stringF.nonBlank
stringF.min(5)
stringF.max(5)
stringF.blank
stringF.matches("scala".r)
stringF.matches("scala")
stringF.isEnum(ScalaEnumeration)
stringF.isJEnum(JavaEnumeration.values())

//ITERABLE FIELD
val listF: Field[List[Int]] = ???
listF.each(_ > 10)
listF.nonEmpty
listF.min(1)
listF.max(10)

//MAP FIELD
val mapF: Field[Map[String, Int]] = ???
mapF.each(_._2 > 3)
mapF.eachKey(_.nonEmpty)
mapF.eachValue(_ > 3)
```

#### Policy
```scala
case class Email(value: String) extends AnyVal
object Email {
    //Policy is interface with 1 validate method, so you can do so  
    implicit val policy: Policy[Email] = _.map(_.value).all(_.nonEmpty, _.max(40)) 
}
case class Request(name: String, email: Email, age: Int, hasParrot: Boolean)

//Policy.builder simplifies combining validation rules
implicit val policy: Policy[Request] =
      Policy
        .builder[Request]
        .subRule(_.name)(_.min(4), _.max(48)) //runs all validations combining using and
        .subRule(_.email)(_.validate) //Reuse Email Policy
        .subRule2(_.age, _.hasParrot)((age, hasParrot) => age > 48 || (age > 22 && hasParrot.isTrue)) //2 fields rule
        .build


val request: Request = ???
val requestF = Field.from(request)
requestF.validate // This will use implicit policy to validate
```