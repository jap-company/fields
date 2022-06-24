import Traits from './traits.md'

# Overview

Fields is a zero-dependency validation library for Scala.

<Traits />

[![Stand With Ukraine](https://raw.githubusercontent.com/vshymanskyy/StandWithUkraine/main/banner-direct-single.svg)](https://stand-with-ukraine.pp.ua)

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

```scala mdoc
import jap.fields._
import jap.fields.DefaultAccumulateVM._

case class User(username: String, password: String, passwordRepeat: Option[String])
case class UserFeatures(standsWithUkraine: Boolean)
case class Request(user: User, features: UserFeatures)
object Request {
  implicit val policy: Policy[Request] =
    Policy
      .builder[Request]
      .subRule(_.user.username)(_.nonBlank, _.minSize(4))
      .subRule(_.user.password)(_.nonBlank, _.minSize(8), _.maxSize(30))
      .subRule(_.user.password, _.user.passwordRepeat)((p, pr) => pr.some(_ === p))
      .rule { request =>
        val standsWithUkraineF = request.sub(_.features.standsWithUkraine)
        standsWithUkraineF.ensure(_ == true, _.failMessage("https://github.com/vshymanskyy/StandWithUkraine/blob/main/docs/README.md"))
      }
      .build
}

val request  = Request(User("Ann", "1234", Some("")), UserFeatures(false))
Field(request).validate
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
