val scala212 = "2.12.14"
val scala213 = "2.13.6"
val scala3   = "3.0.0"

inThisBuild(
  List(
    organization := "pl.datart",
    git.useGitDescribe := true
  )
)

val publishSettings = Seq(
  sonatypeProfileName := "pl.datart",
  organization := "pl.datart",
  homepage := Some(url("https://github.com/eltherion/http4s-timer")),
  licenses := Seq("GPLv3" -> url("https://www.gnu.org/licenses/gpl-3.0")),
  scmInfo := Some(ScmInfo(url("https://github.com/eltherion/http4s-timer"), "scm:git:git@github.com:eltherion/http4s-timer.git")),
  publishTo := sonatypePublishToBundle.value,
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  pomExtra := (
    <developers>
      <developer>
        <id>eltherion</id>
        <name>Mateusz Murawski</name>
        <url>http://www.datart.pl/</url>
      </developer>
    </developers>
    )
)


val commonSettings = Seq(
  crossScalaVersions := List(scala212, scala213, scala3),
    scalacOptions ++= {
      (if (isDotty.value) Seq("-source:3.0-migration") else Nil) ++
        (if (scalaVersion.value >= "2.13.0") Seq() else Seq("-Ypartial-unification")) ++
        Seq("-language:higherKinds", "-deprecation", "-feature")
  }
)

val core = project
  .in(file("core"))
  .enablePlugins(GitVersioning)
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    name := "http4s-timer-core",
    libraryDependencies += "org.http4s" %% "http4s-core" % "1.0.0-M23",
    libraryDependencies += "com.newrelic.agent.java" % "newrelic-api" % "7.1.0",
    libraryDependencies := libraryDependencies.value.map(_.withDottyCompat(scalaVersion.value))
  )

val newrelic = project
  .in(file("newrelic"))
  .enablePlugins(GitVersioning)
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    name := "http4s-timer-newrelic",
  ) dependsOn core

val noPublishSettings = Seq(
  skip in publish := true,
  publishArtifact := false
)

val root = project.in(file("."))
  .settings(commonSettings: _*)
  .settings(noPublishSettings: _*)
  .aggregate(core, newrelic)
  .dependsOn(core, newrelic)
