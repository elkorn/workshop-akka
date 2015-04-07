name := """minimal-akka-scala-seed"""

version := "1.0"

scalaVersion := "2.11.6"

val akkaV = "2.3.9"
val sprayV = "1.3.3"

libraryDependencies ++= Seq(
  "io.spray" %% "spray-can" % sprayV withSources() withJavadoc,
  "io.spray" %% "spray-routing" % sprayV withSources() withJavadoc,
  "io.spray" %% "spray-testkit" % sprayV % "test",
  "com.typesafe.akka" %% "akka-actor" % akkaV,
  "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test")
