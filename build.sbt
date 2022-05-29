ThisBuild / organization           := "company.jap"
ThisBuild / idePackagePrefix       := Some("jap.fields")
ThisBuild / homepage               := Some(url("https://github.com/jap-company/fields"))
ThisBuild / licenses               := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository     := "https://s01.oss.sonatype.org/service/local"
ThisBuild / developers             :=
  List(
    Developer(
      "0lejk4",
      "Oleh Dubynskiy",
      "",
      url("https://github.com/0lejk4"),
    )
  )

lazy val V      = new {
  val Cats     = "2.7.0"
  val Zio      = "1.0.13"
  val Scala3   = "3.1.2"
  val Scala213 = "2.13.8"
  val Scala212 = "2.12.15"
  val MUnit    = "0.7.29"
}
val editorScala = V.Scala3

lazy val root = (project in file("."))
  .settings(
    scalaVersion    := editorScala,
    name            := "fields",
    publishArtifact := false,
  )
  .aggregate(
    `fields-core`,
    `fields-cats`,
    `fields-zio`,
    examples,
  )

lazy val commonSettings = Seq(
  scalaVersion                           := editorScala,
  crossScalaVersions                     := List(V.Scala212, V.Scala213, V.Scala3),
  libraryDependencies += "org.scalameta" %% "munit" % V.MUnit % Test,
  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((3, _))       => Seq("-Ykind-projector:underscores")
      case Some((2, 12 | 13)) => Seq("-Xsource:3", "-P:kind-projector:underscore-placeholders")
    }
  },
  libraryDependencies ++= (
    if (scalaVersion.value == V.Scala3) List()
    else List(compilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full))
  ),
)

lazy val `fields-core` =
  (project in file("modules/core"))
    .settings(
      commonSettings,
      libraryDependencies ++= {
        scalaVersion.value match {
          case V.Scala3 => Nil
          case _        => List("org.scala-lang" % "scala-reflect" % scalaVersion.value)
        }
      },
    )

lazy val `fields-cats` =
  (project in file("modules/cats"))
    .settings(
      commonSettings,
      libraryDependencies += "org.typelevel" %% "cats-core" % V.Cats,
    )
    .dependsOn(`fields-core`)

lazy val `fields-zio` =
  (project in file("modules/zio"))
    .settings(
      commonSettings,
      libraryDependencies ++= Seq(
        "dev.zio" %% "zio"          % V.Zio,
        "dev.zio" %% "zio-test"     % V.Zio % Test,
        "dev.zio" %% "zio-test-sbt" % V.Zio % Test,
      ),
      testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    )
    .dependsOn(`fields-core`)

lazy val examples =
  (project in file("examples"))
    .settings(
      scalaVersion    := editorScala,
      publishArtifact := false,
    )
    .dependsOn(`fields-core`)

Global / excludeLintKeys ++= Set(ThisBuild / idePackagePrefix)
