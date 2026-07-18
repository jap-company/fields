import BuildHelper.*
import org.typelevel.scalacoptions.ScalacOptions
import sbtbuildinfo.BuildInfoKey
import sbtbuildinfo.BuildInfoKeys.{buildInfoKeys, buildInfoPackage}

import scala.sys.process.*

organization     := "io.github.0lejk4"
organizationName := "Jap"
idePackagePrefix := Some("fields")
startYear        := Some(2022)
homepage         := Some(uri("https://github.com/jap-company/fields"))
licenses         := List("Apache-2.0" -> uri("https://www.apache.org/licenses/LICENSE-2.0.txt"))
developers       :=
  List(
    Developer(
      "0lejk4",
      "Oleh Dubynskiy",
      "",
      uri("https://github.com/0lejk4"),
    )
  )

//val editorScala = V.Scala213
val editorScala            = V.Scala3
val supportedScalaVersions = List(V.Scala212, V.Scala213, V.Scala3)
ThisBuild / scalaVersion := editorScala

lazy val scalaSettings = Seq(
  Compile / doc / scalacOptions += "-no-link-warnings",
  tpolecatExcludeOptions := Set(
    ScalacOptions.privateKindProjector,
    ScalacOptions.privateWarnUnusedNoWarn,
    ScalacOptions.lintInferAny,
    ScalacOptions.warnValueDiscard,
    ScalacOptions.privateWarnValueDiscard,
  ),
  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((3, _)) => Seq("-Ykind-projector:underscores")
      case _            => Seq("-Xsource:3", "-P:kind-projector:underscore-placeholders")
    }
  },
  libraryDependencies ++= (
    if (scalaVersion.value == V.Scala3) List()
    else List(compilerPlugin(("org.typelevel" % "kind-projector" % V.KindProjector).cross(CrossVersion.full)))
  ),
)

lazy val commonSettings = Seq(
  libraryDependencies += "org.scalameta" %% "munit" % V.MUnit % Test
) ++ scalaSettings

lazy val `fields-core` =
  (projectMatrix in file("modules/core"))
    .settings(
      commonSettings,
      buildInfoPackage := "fields",
      buildInfoKeys    := Seq[BuildInfoKey](organization, moduleName, name, version, scalaVersion, isSnapshot),
      libraryDependencies += "io.circe" %% "circe-core" % V.Circe % Optional,
      libraryDependencies ++= {
        scalaVersion.value match {
          case V.Scala3 => Nil
          case _        => List("org.scala-lang" % "scala-reflect" % scalaVersion.value)
        }
      },
    )
    .enablePlugins(BuildInfoPlugin)
    .jvmPlatform(scalaVersions = supportedScalaVersions)

lazy val `fields-lens` =
  (projectMatrix in file("modules/lens"))
    .settings(commonSettings)
    .dependsOn(`fields-core`)
    .jvmPlatform(scalaVersions = supportedScalaVersions)

lazy val `fields-value` =
  (projectMatrix in file("modules/value"))
    .settings(commonSettings)
    .dependsOn(`fields-core`)
    .jvmPlatform(scalaVersions = supportedScalaVersions)

lazy val `fields-cats` =
  (projectMatrix in file("modules/cats"))
    .settings(
      commonSettings,
      libraryDependencies += "org.typelevel" %% "cats-core" % V.Cats,
    )
    .dependsOn(`fields-core`)
    .dependsOn(`fields-lens` % Test, `fields-value` % Test)
    .jvmPlatform(scalaVersions = supportedScalaVersions)

lazy val `fields-zio` =
  (projectMatrix in file("modules/zio"))
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
    .dependsOn(`fields-lens` % Test, `fields-value` % Test)
    .jvmPlatform(scalaVersions = supportedScalaVersions)

lazy val `fields-zio-blocks-schema` =
  (projectMatrix in file("modules/zio-blocks-schema"))
    .settings(
      commonSettings,
      libraryDependencies += "dev.zio" %% "zio-blocks-schema" % V.ZioBlocks,
    )
    .dependsOn(`fields-core`, `fields-value` % Test, `fields-lens` % Test)
    .jvmPlatform(scalaVersions = List(V.Scala213, V.Scala3))

lazy val examples =
  (project in file("examples"))
    .settings(
      scalaSettings,
      scalaVersion                           := editorScala,
      idePackagePrefix                       := None,
      crossScalaVersions                     := Nil,
      publishArtifact                        := false,
      publish / skip                         := true,
      libraryDependencies += "org.typelevel" %% "cats-effect" % V.CatsEffect,
      libraryDependencies += "io.circe"      %% "circe-core"  % V.Circe,
    )
    .dependsOn(
      `fields-core`.jvm(editorScala),
      `fields-value`.jvm(editorScala),
      `fields-lens`.jvm(editorScala),
      `fields-zio`.jvm(editorScala),
      `fields-cats`.jvm(editorScala),
    )

lazy val benchmarks =
  (projectMatrix in file("benchmarks"))
    .enablePlugins(JmhPlugin)
    .settings(
      scalaSettings,
      idePackagePrefix                           := None,
      publishArtifact                            := false,
      publish / skip                             := true,
      libraryDependencies += "org.typelevel"     %% "cats-effect"      % V.CatsEffect,
      libraryDependencies += "dev.zio"           %% "zio-interop-cats" % V.ZioInteropCats,
      libraryDependencies += "io.circe"          %% "circe-core"       % V.Circe,
      libraryDependencies += "com.github.yakivy" %% "dupin-core"       % "0.6.1",
      libraryDependencies ++=
        (if (scalaVersion.value == V.Scala3) Nil
         else
           List(
             "com.github.krzemin" %% "octopus"      % "0.4.1",
             "com.github.krzemin" %% "octopus-cats" % "0.4.1",
           )),
    )
    .dependsOn(`fields-core`, `fields-value`, `fields-lens`, `fields-zio`, `fields-cats`)
    .jvmPlatform(scalaVersions = List(editorScala))

lazy val modules: Seq[ProjectMatrix] =
  List(
    `fields-core`,
    `fields-lens`,
    `fields-value`,
    `fields-cats`,
    `fields-zio`,
    `fields-zio-blocks-schema`,
    benchmarks,
  )

lazy val root = (project in file("."))
  .aggregate(modules.flatMap(_.projectRefs) *)
  .aggregate(examples)
  .settings(
    name               := "fields",
    scalaVersion       := editorScala,
    crossScalaVersions := Seq(),
    publishArtifact    := false,
    publish / skip     := true,
  )

val updateDocsVariables = taskKey[Unit]("Update docs variables")
val websiteBuild        = taskKey[Unit]("Build the production documentation website")

lazy val `fields-docs` =
  project
    .settings(scalaSettings)
    .settings(scalaVersion := editorScala, crossScalaVersions := Nil)
    .settings(
      moduleName                       := "fields-docs",
      libraryDependencies += "dev.zio" %% "zio-blocks-schema" % V.ZioBlocks,
      mdocOut                          := (LocalRootProject / baseDirectory).value / "website" / "generated-docs",
      mdocVariables                    := Map(
        "version"                   -> latestVersion.value,
        "organization"              -> (LocalRootProject / organization).value,
        "coreModuleName"            -> (`fields-core`.jvm(editorScala) / moduleName).value,
        "zioModuleName"             -> (`fields-zio`.jvm(editorScala) / moduleName).value,
        "zioBlocksSchemaModuleName" -> (`fields-zio-blocks-schema`.jvm(editorScala) / moduleName).value,
        "catsModuleName"            -> (`fields-cats`.jvm(editorScala) / moduleName).value,
        "lensModuleName"            -> (`fields-lens`.jvm(editorScala) / moduleName).value,
        "valueModuleName"           -> (`fields-value`.jvm(editorScala) / moduleName).value,
        "scalaPublishVersions"      -> {
          val minorVersions = supportedScalaVersions.map(CrossVersion.binaryScalaVersion)
          if (minorVersions.size <= 2) minorVersions.mkString(" and ")
          else minorVersions.init.mkString(", ") ++ " and " ++ minorVersions.last
        },
      ),
      updateDocsVariables              := Def.uncached {
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
      publishArtifact                  := false,
      publish / skip                   := true,
      ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(
        `fields-core`.jvm(editorScala),
        `fields-cats`.jvm(editorScala),
        `fields-zio`.jvm(editorScala),
        `fields-zio-blocks-schema`.jvm(editorScala),
        `fields-value`.jvm(editorScala),
        `fields-lens`.jvm(editorScala),
      ),
      ScalaUnidoc / unidoc / target     := (LocalRootProject / baseDirectory).value / "website" / "static" / "api",
      cleanFiles += (ScalaUnidoc / unidoc / target).value,
      docusaurusCreateSite              := Def.uncached(
        docusaurusCreateSite.dependsOn(Compile / unidoc).dependsOn(updateDocsVariables).value
      ),
      docusaurusPublishGhpages          := Def.uncached(
        docusaurusPublishGhpages
          .dependsOn(Compile / unidoc)
          .dependsOn(updateDocsVariables)
          .value
      ),
       // format: off
       ScalaUnidoc / unidoc / scalacOptions ++= Seq(
         "-doc-source-url", s"https://github.com/jap-company/fields/tree/v${latestVersion.value}€{FILE_PATH}.scala",
         "-sourcepath", (LocalRootProject / baseDirectory).value.getAbsolutePath,
         "-doc-title", "Fields",
         "-doc-version", s"v${latestVersion.value}",
         "-doc-logo", (LocalRootProject / baseDirectory).value.getAbsolutePath + "/website/static/img/logo.svg",
       ),
       // format: on
      libraryDependencies += "io.circe" %% "circe-core" % V.Circe,
    )
    .dependsOn(
      `fields-core`.jvm(editorScala),
      `fields-cats`.jvm(editorScala),
      `fields-zio`.jvm(editorScala),
      `fields-zio-blocks-schema`.jvm(editorScala),
      `fields-lens`.jvm(editorScala),
      `fields-value`.jvm(editorScala),
    )
    .enablePlugins(MdocPlugin, DocusaurusPlugin, ScalaUnidocPlugin)
    .disablePlugins(TpolecatPlugin)

root / websiteBuild := {
  (`fields-docs` / updateDocsVariables).value
  val website = (LocalRootProject / baseDirectory).value / "website"
  val install = Process(Seq("npm", "ci", "--ignore-scripts"), website).!
  if (install != 0) sys.error("npm ci failed")
  val build   = Process(Seq("npm", "run", "build"), website).!
  if (build != 0) sys.error("website build failed")
}

val latestVersion = settingKey[String]("Latest stable released version")
ThisBuild / latestVersion := {
  val snapshot       = (ThisBuild / isSnapshot).value
  val stable         = (ThisBuild / isVersionStable).value
  val currentVersion = (ThisBuild / version).value
  if (!snapshot && stable) currentVersion
  else (ThisBuild / previousStableVersion).value.getOrElse(currentVersion)
}

Global / excludeLintKeys ++= Set(ThisBuild / idePackagePrefix)

//Github Workflow
ThisBuild / githubWorkflowTargetTags            := Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches := Seq(RefPredicate.StartsWith(Ref.Tag("v")))
ThisBuild / githubWorkflowJavaVersions          := Seq(JavaSpec.temurin("17"))
ThisBuild / githubWorkflowArtifactUpload        := false
ThisBuild / githubWorkflowScalaVersions         := List(V.Scala3)
ThisBuild / githubWorkflowBuild                 := Seq(
  WorkflowStep.Sbt(List("ci"))
)

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release", "fields-docs/docusaurusPublishGhpages"),
    env = Map(
      "GIT_DEPLOY_KEY"    -> "${{ secrets.GIT_DEPLOY_KEY }}",
      "PGP_PASSPHRASE"    -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET"        -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}",
    ),
  )
)

def addCommandsAlias(name: String, commands: List[String]) = addCommandAlias(name, commands.mkString(";", ";", ""))

addCommandsAlias(
  "ci",
  List(
    "testFull",
    "scalafmtCheck",
    "scalafmtSbtCheck",
    "headerCheck",
    "fields-docs/mdoc",
    "doc",
    "root/websiteBuild",
  ),
)

addCommandsAlias(
  "ciFast",
  List("test", "scalafmtCheck", "scalafmtSbtCheck", "headerCheck"),
)

addCommandsAlias(
  "releaseCheck",
  List("ci", "publishLocal", "githubWorkflowCheck"),
)
