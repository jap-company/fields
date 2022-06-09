# Field

Library is called Fields solely because it is built around `Field` data type.

`Field[P]` has _path_ of type `FieldPath` and _value_ of type `P`.

All validations are defined throught syntax available for `Field`

Various ways to create and transform `Field` is described in [Syntax](#syntax) section

## Syntax

```scala
val path: FieldPath = ???
Field(path, request.name) //Field(path, request.name) Using path and value
Field(request.name) //Field(FieldPath.root, request.name) Using value without path
Field.from(request.name) //Field(FieldPath("request", "name"), request.name) Innherit path from field selects
Field.sub(request.name) //Field(FieldPath("name"), request.name) Innherit path from field selects and drops first path

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
