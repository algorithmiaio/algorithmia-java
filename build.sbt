
name := "algorithmia-client"

organization := "algorithmia"

version := "0.9.0-SNAPSHOT"

autoScalaLibrary := false

// More compiler warnings
scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint")  // "-verbose"

// resolvers += "Maven Central" at "http://repo1.maven.org/maven2/org/"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.google.code.gson" % "gson" % "2.3",
  "org.apache.httpcomponents" % "httpclient" % "4.4",
  "org.apache.httpcomponents" % "httpasyncclient" % "4.0.2",
  "commons-io" % "commons-io" % "2.4",
  "com.novocode" % "junit-interface" % "0.8" % "test->default",
  "junit" % "junit" % "4.12" % "test"
)

// publishMavenStyle := true

// Disable using the Scala version in published artifacts
crossPaths := false
