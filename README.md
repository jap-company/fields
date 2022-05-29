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
    "company.jap" %% "fields-core" % "0.0.1",
    "company.jap" %% "fields-zio" % "0.0.1",
    "company.jap" %% "fields-cats" % "0.0.1",
)
```
Core concept of this validation library `Field` structure that stores field path and value and has syntax to create subfields that will carry parents path info.
You start by extending ValidationModule and choosing your Effect - F[_], ValidationResult - VR[_], Error - E types:
```scala
// Note: this expects you to have such implicits:
// ValidationEffect[ValidationEffect.Id]
// ValidationResult[Accumulate]
// ValidationErrorMapper[ValidationError]
object MyValidationModule extends ValidationModule[ValidationEffect.Id, Accumulate, FieldError[ValidationError]]
object MyValidationModule extends AccumulateValidationModule[ValidationEffect.Id, FieldError[ValidationError]]
object MyValidationModule extends FailFastValidationModule[ValidationEffect.Id, FieldError[ValidationError]]
```
There are predefined typeclass instances:
* ValidationEffect for ValidationEffect.Id(sync), Future, ZIO and Cats Monad/Defer
* ValidationResult for Accumulate, FailFast, Cats ValidatedNel/ValidatedNeq
* ValidationErrorMapper for ValidationError and FieldError(requires inner error to implement typeclass)

Important note if you want library to correctly handle short-circuiting you should use lazy ValidationEffect, if you don`t need async validation stick to cats.Eval

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

resuestF.sub(_.name) // Derive subfield using field selector
resuestF.provideSub("name", request.name) // Manual subfield with provided value
resuestF.selectSub("name", _.name) // Manual subfield with value selector
```

#### VR\[E] and F\[VR\[E]]
```scala
val vr1 = nameF.nonEmpty
val vr2    surnameF.nonEmpty
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
requestF.assertTrue(false, _.invalid)
requestF.assert(_.isValid, _.invalid)

def isRequestValidApi: zio.Task[Boolean] = ???
requestF.assertF(isRequestValidApi, _.invalid)
requestF.check(c => Accumulate.invalid(c.invalid).whenNot(c.value.isValid))
requestF.checkF(c => isRequestValidApi(c.value).map(Accumulate.whenNot(_)(c.custom("err"))), _.invalid)

requestF === request
requestF equalTo request

requestF !== request
requestF notEqualTo request

requestF === requestF
requestF equalTo requestF

requestF !== requestF
requestF notEqualTo requestF

requestF in List(request)

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
