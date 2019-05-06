
name := "algorithmia-client"

organization := "com.algorithmia"

version := "2.0.0"

autoScalaLibrary := false

// More compiler warnings
scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint")

javacOptions ++= Seq("-source", "8", "-target", "8")

javacOptions in doc := Seq("-source", "8")

libraryDependencies ++= Seq(
  "com.google.code.gson" % "gson" % "2.6.2",
  "org.apache.httpcomponents" % "httpasyncclient" % "4.1.1",
  "commons-io" % "commons-io" % "2.5",
  "com.novocode" % "junit-interface" % "0.11" % "test->default",
  "junit" % "junit" % "4.12" % "test"
)

// Disable using the Scala version in published artifacts
crossPaths := false

testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a"))
