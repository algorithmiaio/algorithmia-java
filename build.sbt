
name := "algorithmia-client"

organization := "com.algorithmia"

version := "1.0.6-SNAPSHOT"

autoScalaLibrary := false

// More compiler warnings
scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint")

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

javacOptions in doc := Seq("-source", "1.6")

libraryDependencies ++= Seq(
  "com.google.code.gson" % "gson" % "2.5",
  "org.apache.httpcomponents" % "httpasyncclient" % "4.1.1",
  "commons-io" % "commons-io" % "2.4",
  "com.novocode" % "junit-interface" % "0.8" % "test->default",
  "junit" % "junit" % "4.12" % "test"
)

// Disable using the Scala version in published artifacts
crossPaths := false
