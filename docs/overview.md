import Traits from './traits.md'

# Overview

Fields is a zero-dependency Scala validation library.

<Traits />

## Getting started

To get started with [sbt](https://scala-sbt.org), simply add the following line to your `build.sbt` file.

```scala
libraryDependencies ++= List(
  "@organization@" %% "@coreModuleName@" % "@version@",
  "@organization@" %% "@zioModuleName@" % "@version@",
  "@organization@" %% "@catsModuleName@" % "@version@",
)
```

## Code teaser

```scala
import jap.fields._
import jap.fields.DefaultAccumulateVM._

case class User(username: String, password: String)
case class Request(user: User)
val request = Request(User("", ""))

val userF = Field.from(request.user)
val usernameF = userF.sub(_.username)
val passwordF = userF.sub(_.password)

usernameF.nonBlank && passwordF.nonBlank
```

This is just the basics of Fields, but there is still plenty of syntax to learn, see other Documentation sections.

## Adopters

Is your company using Fields and want to be listed here?

We will be happy to feature your company here, but in order to do that, we'll need written permission to avoid any legal misunderstandings.

Please open new [Github Issue](https://github.com/jap-company/fields/issues/new) and provide us with your company name, logo and legal permission to add your company as.

## Sponsors

Development and maintenance of Fields is sponsored by [Jap](http://jap.company)

[![](https://raw.githubusercontent.com/jap-company/fields/master/assets/jap-logo.png "Jap")](http://jap.company)

## License

Licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html). Refer to the [license file](https://github.com/jap-company/fields/blob/master/LICENSE).
