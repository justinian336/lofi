name := "lofi"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.13",
  "com.typesafe.akka" %% "akka-http"   % "10.1.3",
  "com.typesafe.akka" %% "akka-stream" % "2.5.13",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.3",
  "org.apache.commons" % "commons-math3" % "3.3"
)

dependsOn(RootProject(uri("git://github.com/justinian336/trees.git")))
