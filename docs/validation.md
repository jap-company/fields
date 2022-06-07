# Validation


## Syntax

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