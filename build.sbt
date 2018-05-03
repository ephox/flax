import sbt._
import Keys._

lazy val commonSettings = Seq(
  name := "flax",
  organization := "com.ephox",
  licenses := Seq(("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0"))),
  homepage := Some(new URL("https://www.ephox.com/")),
  organizationHomepage := Some(new URL("https://www.ephox.com/")),
  scalaVersion := "2.12.5",
  bintrayRepository := "flax",
  resolvers += Resolver.sonatypeRepo("releases")
)

lazy val flax = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    Defaults.itSettings
  )

scalacOptions in Compile ++= Seq(
    "-deprecation"
  , "-unchecked"
  , "-feature"
  , "-Ypartial-unification"
  , "-language:higherKinds"
  , "-language:implicitConversions"
  , "-language:postfixOps"
  , "-Yno-adapted-args"
  , "-Ywarn-value-discard"
  , "-Ywarn-unused-import"
)

parallelExecution in Test := false
fork in test := false

val scalazVersion = "7.2.20"
val specs2Version = "4.0.3"
val seleniumVersion = "3.11.0"

libraryDependencies ++= Seq(
    "org.scalaz" %% "scalaz-core"       % scalazVersion
  , "org.scalaz" %% "scalaz-iteratee"   % scalazVersion
  , "org.scalaz" %% "scalaz-concurrent" % scalazVersion
  , "org.scalaz" %% "scalaz-effect"     % scalazVersion

  , "org.specs2" %% "specs2-core"       % specs2Version

  , "org.seleniumhq.selenium" % "selenium-java"           % seleniumVersion
  , "org.seleniumhq.selenium" % "selenium-server"         % seleniumVersion
  , "org.seleniumhq.selenium" % "selenium-firefox-driver" % seleniumVersion

  , "com.codeborne" % "phantomjsdriver" % "1.4.4"

  , "org.scalacheck" %% "scalacheck" % "1.13.5" % Test
  , "org.specs2" %% "specs2-scalacheck" % specs2Version % Test
  , "org.typelevel" %% "scalaz-specs2" % "0.5.2" % Test
  , compilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6")
).map(_.withSources)

