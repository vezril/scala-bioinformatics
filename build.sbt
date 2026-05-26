ThisBuild / scalaVersion := "2.13.18"
ThisBuild / organization := "com.improving.bio"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "scala-bioinformatics",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.5.4",
      "org.scalatest" %% "scalatest"   % "3.2.18" % Test
    ),
    Compile / run / fork := true,
    javaOptions += "-Dcats.effect.warnOnNonMainThreadDetected=false",
    scalacOptions ++= Seq(
      "-encoding", "utf8",
      "-feature",
      "-unchecked",
      "-deprecation",
      "-Xlint"
    )
  )
