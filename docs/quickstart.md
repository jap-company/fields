
# Quickstart

Fields is a Scala validation library featuring:
* DSL syntax for defining validation rules
* Possibility to use Effects like ZIO, Cats, Future, etc.
* Ability to use your own error types
* You can use validation structure of choice
* Interop with Circe
* fields-value module for complex validation cases
* fields-lens module using lenses for validation

## Getting started

To get started with [sbt](https://scala-sbt.org), simply add the following line to your `build.sbt` file.

```scala
libraryDependencies ++= List(
  "@organization@" %% "@lensModuleName@" % "@version@", // Lens based validation
  "@organization@" %% "@valueModuleName@" % "@version@", // Value based validation
  "@organization@" %% "@zioModuleName@" % "@version@", // Optional: ZIO interop
  "@organization@" %% "@catsModuleName@" % "@version@",  // Optional: Cats interop
)
```

## Example

Define model
```scala mdoc
case class User(username: String, password: String, passwordRepeat: Option[String])
case class Request(user: User)
```

```scala mdoc:nest
import fields.value.FieldsDsl.default._

val policy: Policy[Request] =
  Policy[Request]
    .subRule(_.user.username)(_.nonBlank, _.minSize(4))
    .subRule(_.user.password)(_.nonBlank, _.minSize(8), _.maxSize(30))
    .subRule(_.user.password, _.user.passwordRepeat)((p, pr) => pr.some(_ === p))

policy.validate(Request(User("Ann", "1234", Some(""))))
```


```scala mdoc:nest
import fields.lens.FieldsDsl.default._

val policy: Policy[Request] =
  Policy[Request]
    .subRule(_.user.username)(_.nonBlank, _.minSize(4))
    .subRule(_.user.password)(_.nonBlank, _.minSize(8), _.maxSize(30))
    .subRule(_.user.password, _.user.passwordRepeat)((p, pr) => pr.some(_ === p))

policy(Request(User("Ann", "1234", Some(""))))
```

For a complete module comparison, see [Modules and compatibility](modules.md).


## Adopters

Is your company using Fields and want to be listed here?

We will be happy to feature your company here, but in order to do that, we'll need written permission to avoid any legal misunderstandings.

Please open new [Github Issue](https://github.com/jap-company/fields/issues/new) and provide us with your company name, logo and legal permission to add your company as.

## Sponsors

Development and maintenance of Fields is sponsored by [Jap](http://jap.company)

[![](https://raw.githubusercontent.com/jap-company/fields/master/assets/jap-logo.png 'Jap')](http://jap.company)

## License

Licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html). Refer to the [license file](https://github.com/jap-company/fields/blob/master/LICENSE).
