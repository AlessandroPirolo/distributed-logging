val AkkaVersion = "2.6.19"
libraryDependencies ++= Seq(
  "com.lightbend.akka" %% "akka-stream-alpakka-mqtt" % "3.0.4",
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion
)

ThisBuild / version       := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "subscriber"
  )
