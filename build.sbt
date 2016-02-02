
name := "algorithmia-client"

organization := "com.algorithmia"

version := "1.0.3-SNAPSHOT"

autoScalaLibrary := false

// More compiler warnings
scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint")

libraryDependencies ++= Seq(
  "com.google.code.gson" % "gson" % "2.5",
  "org.apache.httpcomponents" % "httpasyncclient" % "4.1.1",
  "commons-io" % "commons-io" % "2.4",
  "com.novocode" % "junit-interface" % "0.8" % "test->default",
  "junit" % "junit" % "4.12" % "test"
)

// Disable using the Scala version in published artifacts
crossPaths := false
