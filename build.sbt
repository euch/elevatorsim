val versionScala = "3.1.3"
val versionAkka = "2.6.19"
val versionTypeSafeConfig = "1.4.2"
val versionScalaTest = "3.2.12"
val versionSlf4jApi = "1.7.36"

fork := true

lazy val root = (project in file(".")).settings(
  inThisBuild(
    List(
      organization := "org.euch.elevatorsim",
      scalaVersion := versionScala
    )
  ),
  name := "untitled",
  libraryDependencies ++= Seq(
    "com.typesafe" % "config" % versionTypeSafeConfig,
    "com.typesafe.akka" %% "akka-actor-typed" % versionAkka,
    "com.typesafe.akka" %% "akka-actor-testkit-typed" % versionAkka % Test,
    "org.scalatest" %% "scalatest" % versionScalaTest % Test,
    "org.slf4j" % "slf4j-simple" % versionSlf4jApi
  )
)
