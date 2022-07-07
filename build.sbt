val versionScala = "3.1.3"
val versionAkka = "2.6.19"
val versionTypeSafeConfig = "1.4.2"
val versionScalaTest = "3.2.12"

fork := true

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "org.euch.elevatorsim",
      scalaVersion := versionScala,
    )),
    name := "untitled",
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % versionTypeSafeConfig,
      "com.typesafe.akka" %% "akka-persistence-typed" % versionAkka,
      "com.typesafe.akka" %% "akka-persistence-testkit" % versionAkka % Test,
      "org.scalatest" %% "scalatest" % versionScalaTest % Test
    )
  )
