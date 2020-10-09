
name := "algorithmia-client"

organization := "com.algorithmia"

version := "1.0.16"

autoScalaLibrary := false

// More compiler warnings
scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint", "-target:jvm-1.11")

libraryDependencies ++= Seq(
  "com.google.code.gson" % "gson" % "2.8.6",
  "org.apache.httpcomponents" % "httpasyncclient" % "4.1.4",
  "commons-io" % "commons-io" % "2.8.0",
  "com.novocode" % "junit-interface" % "0.11" % "test->default",
  "org.junit.jupiter" % "junit-jupiter-api" % "5.7.0" % "test"
)

// Disable using the Scala version in published artifacts
crossPaths := false
