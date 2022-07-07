[![Stand With Ukraine](https://raw.githubusercontent.com/vshymanskyy/StandWithUkraine/main/banner-direct-single.svg)](https://stand-with-ukraine.pp.ua)

<p align="center">
    <img src="assets/jap-logo.png" alt="Jap Logo"/>
</p>

# Fields

[![badge-scaladex]][link-scaladex] [![badge-maven]][link-maven] [![badge-ci]][link-ci] [![badge-scaladoc]][link-scaladoc] [![badge-scala-ukraine]][link-scala-ukraine]

Fields is a Scala validation library that you should use because it is:

- _**F**inal Tagless_. Choose any Effect, Validated, or Error types.
- _**I**nformative_. Error paths help understanding where the error occurred.
- _**E**xpressive_. Rich, extendable validation syntax.
- _**L**ightweight_. The core module has no-dependencies.
- _**D**auntless_. Have no fear of complex validations with `Rule` type.
- _**S**hort-circuit_. Avoid running undesired validation side-effects.

# Teaser

```scala
// Here we pass validation dependencies using implicits, but you could be doing this the way you prefer
def policy(implicit tokenService: TokenService, userService: UserService): Policy[RegisterRequest] = Policy
  .builder[RegisterRequest]
  .subRule(_.age)(_ >= 18, _ <= 110)
  .subRule(_.email)(_.map(_.value).matchesRegex(EmailRegex))
  .subRule(_.password)(_.nonEmpty, _.minSize(4), _.maxSize(100))
  .subRule(_.password, _.passwordRepeat)(_ equalTo _)
  .subRule(_.username)(validateUsername)
  .subRule(_.token)(_.ensureF(tokenService.validateToken, _.failMessage("invalid-token")))
  .build

// Extract complex validations to reusable methods.
def validateUsername(username: Field[String])(implicit userService: UserService): MRule =
    username.minSize(1) &&
    username.maxSize(10) &&
    MRule.flatten {
      userService.findByUsername(username.value).map {
        case Some(_) => username.failMessage("username-already-exists")
        case None    => MRule.valid
      }
    }
```

## Quicklinks

- [Microsite](https://jap-company.github.io/fields)
- [Medium](https://medium.com/@oleh.dubynskiy/fields-scala-validation-library-86ac818cd704)
- [Contributor's Guide](https://jap-company.github.io/fields/docs/contributing)
- [License](LICENSE)
- [Issues](https://github.com/jap-company/fields/issues)
- [Pull Requests](https://github.com/jap-company/fields/pulls)

# [Learn More on the Fields Microsite](https://jap-company.github.io/fields)

[link-scaladex]: https://index.scala-lang.org/jap-company/fields/fields-core "Scaladex"
[link-maven]: https://maven-badges.herokuapp.com/maven-central/company.jap/fields-core_2.13 "Maven"
[link-ci]: https://github.com/jap-company/fields/actions?query=workflow%3A%22CI%22 "CI"
[link-scala-ukraine]: https://github.com/scala-ukraine/knowledge-base "Scala Ukraine"
[link-scaladoc]: https://jap-company.github.io/fields/api "Scaladoc"
[badge-ci]: https://github.com/jap-company/fields/workflows/CI/badge.svg "CI"
[badge-maven]: https://maven-badges.herokuapp.com/maven-central/company.jap/fields-core_2.13/badge.svg "Maven"
[badge-scaladex]: https://index.scala-lang.org/jap-company/fields/fields-core/latest-by-scala-version.svg?platform=jvm "Scaladex"
[badge-scala-ukraine]: https://img.shields.io/badge/Scala-Ukraine-EBD038?labelColor=4172CC "Scala Ukraine"
[badge-scaladoc]: https://img.shields.io/badge/scaladoc-read-brightgreen "Scaladoc"
