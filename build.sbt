import com.typesafe.sbt.SbtGit.GitKeys._

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

lazy val modules: Seq[ProjectReference] = List(`fields-core`, `fields-cats`, `fields-zio`)

lazy val root = (project in file("."))
  .aggregate(modules: _*)
  .aggregate(examples)
  .settings(
    name            := "fields",
    scalaVersion    := editorScala,
    publishArtifact := false,
  )

lazy val scalaSettings = Seq(
  scalaVersion       := editorScala,
  crossScalaVersions := List(V.Scala212, V.Scala213, V.Scala3),
  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((3, _)) => Seq("-Ykind-projector:underscores")
      case _            => Seq("-Xsource:3", "-P:kind-projector:underscore-placeholders")
    }
  },
)

lazy val commonSettings = Seq(
  libraryDependencies += "org.scalameta" %% "munit" % V.MUnit % Test,
  libraryDependencies ++= (
    if (scalaVersion.value == V.Scala3) List()
    else List(compilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full))
  ),
) ++ scalaSettings

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
      scalaVersion                           := editorScala,
      publishArtifact                        := false,
      libraryDependencies += "org.typelevel" %% "cats-effect" % "2.5.5",
    )
    .dependsOn(`fields-core`, `fields-zio`, `fields-cats`)

val updateDocsVariables = taskKey[Unit]("Update docs variables")
lazy val `fields-docs`  =
  project
    .settings(scalaSettings)
    .settings(crossScalaVersions := List(V.Scala213))
    .settings(
      moduleName                                 := "fields-docs",
      mdocVariables                              := Map(
        "version"              -> latestVersion.value,
        "organization"         -> (LocalRootProject / organization).value,
        "coreModuleName"       -> (`fields-core` / moduleName).value,
        "zioModuleName"        -> (`fields-cats` / moduleName).value,
        "catsModuleName"       -> (`fields-zio` / moduleName).value,
        "scalaPublishVersions" -> {
          val minorVersions = (`fields-core` / crossScalaVersions).value.map(CrossVersion.binaryScalaVersion(_))
          if (minorVersions.size <= 2) minorVersions.mkString(" and ")
          else minorVersions.init.mkString(", ") ++ " and " ++ minorVersions.last
        },
      ),
      updateDocsVariables                        := {
        val file = (LocalRootProject / baseDirectory).value / "website" / "variables.js"

        val fileHeader =
          "// Generated by sbt. Do not edit directly."

        val fileContents =
          mdocVariables.value.toList
            .sortBy { case (key, _) => key }
            .map { case (key, value) => s"  $key: '$value'" }
            .mkString(s"$fileHeader\nmodule.exports = {\n", ",\n", "\n};\n")

        IO.write(file, fileContents)
      },
      publishArtifact                            := false,
      ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(`fields-core`, `fields-cats`, `fields-zio`),
      ScalaUnidoc / unidoc / target := (LocalRootProject / baseDirectory).value / "website" / "static" / "api",
      cleanFiles += (ScalaUnidoc / unidoc / target).value,
      docusaurusCreateSite     := docusaurusCreateSite.dependsOn(Compile / unidoc).dependsOn(updateDocsVariables).value,
      docusaurusPublishGhpages := docusaurusPublishGhpages
        .dependsOn(Compile / unidoc)
        .dependsOn(updateDocsVariables)
        .value,
       // format: off
       ScalaUnidoc / unidoc / scalacOptions ++= Seq(
         "-doc-source-url", s"https://github.com/jap-company/fields/tree/v${version.value}€{FILE_PATH}.scala",
         "-sourcepath", (LocalRootProject / baseDirectory).value.getAbsolutePath,
         "-doc-title", "Fields",
         "-doc-version", s"v${version.value}",
         "-doc-logo", (LocalRootProject / baseDirectory).value.getAbsolutePath + "/website/static/img/logo.svg",
       )
       // format: on
    )
    .dependsOn(`fields-core`, `fields-cats`, `fields-zio`)
    .enablePlugins(MdocPlugin, DocusaurusPlugin, ScalaUnidocPlugin)

val latestVersion = settingKey[String]("Latest stable released version")
ThisBuild / latestVersion := {
  val snapshot = (ThisBuild / isSnapshot).value
  val stable   = (ThisBuild / isVersionStable).value
  if (!snapshot && stable) (ThisBuild / version).value
  else (ThisBuild / previousStableVersion).value.get
}

Global / excludeLintKeys ++= Set(ThisBuild / idePackagePrefix)
