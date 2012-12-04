name := "jukai"

organization := "com.geishatokyo"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.9.1"

resolvers += Resolver.mavenLocal

publishTo := Some(Resolver.file("localMaven",Path.userHome / ".m2" / "repository"))

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.6.4" % "provided",
  "com.amazonaws" % "aws-java-sdk" % "1.3.26" % "provided",
  "org.specs2" %% "specs2" % "1.12.3" % "test",
  "junit" % "junit" % "4.11" % "test",
  "org.mockito" % "mockito-all" % "1.9.0" % "test"
)