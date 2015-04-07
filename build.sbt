name := """minimal-akka-scala-seed"""

version := "1.0"

scalaVersion := "2.11.6"

val akkaV = "2.3.9"
val sprayV = "1.3.3"

libraryDependencies ++= Seq(
  "io.spray" %% "spray-can" % sprayV,
  "io.spray" %% "spray-routing" % sprayV,
  "io.spray" %% "spray-json" % "1.3.1",
  "com.wandoulabs.akka" %% "spray-websocket" % "0.1.4" excludeAll (
    ExclusionRule(organization = "io.spray")
  ),
  "com.typesafe.akka" %% "akka-actor" % akkaV,
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "io.spray" %% "spray-testkit" % sprayV % "test",
  "com.typesafe.akka" %% "akka-testkit" % akkaV % "test")
  .map(_.withSources.withJavadoc)

scalacOptions in Test ++= Seq("-Yrangepos")
mainClass in(Compile, run) := Some("com.example.ApplicationMain")