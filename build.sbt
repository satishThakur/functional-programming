import Dependencies._

val scala3Version = "3.1.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "functional-programming",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,
    scalacOptions ++= List("-feature", "-deprecation", "-Ykind-projector:underscores", "-source:future"),

    libraryDependencies ++= Seq(
      Libraries.cats,
      Libraries.catsEffect,
      Libraries.catsRedis,
    ),
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test
  )
