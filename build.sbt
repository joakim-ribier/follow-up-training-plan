name := "Follow-UpT.Plan"
version := "0.1-SNAPSHOT"
organization := "fr.ribierjoakim"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.1",
  "joda-time" % "joda-time" % "2.9.9",
  "com.typesafe.akka" %% "akka-actor" % "2.4.19",
  "com.itextpdf" % "itextpdf" % "5.5.13",
  "org.apache.pdfbox" % "pdfbox" % "2.0.8"
)

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % "0.9.1")


libraryDependencies ++= Seq(
  "com.google.api-client" % "google-api-client" % "1.23.0",
  "com.google.oauth-client" % "google-oauth-client-jetty" % "1.23.0",
  "com.google.apis" % "google-api-services-drive" % "v3-rev102-1.23.0"
)

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

lazy val app = (project in file("app")).
  settings(
    mainClass in assembly := Some("fr.ribierjoakim.followuptrainingplan.Main")
  )