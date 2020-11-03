
name := "algorithmia-client"

organization := "com.algorithmia"

version := "1.3.8"

autoScalaLibrary := false

// required work-around for faulty scala build tool
javaHome := Some(file("C:\\Users\\dale.herring\\Java\\jdk-11"))

// More compiler warnings
scalacOptions ++= Seq("-deprecation", "-unchecked", "-source 11", "-feature", "-Xlint", "-target:jvm-1.11")

libraryDependencies ++= Seq(
  "com.google.code.gson" % "gson" % "2.8.6",
  "org.apache.httpcomponents" % "httpasyncclient" % "4.1.4",
  "commons-io" % "commons-io" % "2.8.0",
  "com.novocode" % "junit-interface" % "0.11" % "test->default",
  "org.junit.jupiter" % "junit-jupiter-api" % "5.7.0" % "test",
  "org.projectlombok" % "lombok" % "1.18.2",
  "org.mockito" % "mockito-core" % "3.6.0" % Test
)

// Disable using the Scala version in published artifacts
crossPaths := false
