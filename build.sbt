name := "DtLabIngest"
fork := true
javaOptions in test ++= Seq(
  "-Xms128M",
  "-Xmx256M",
  "-XX:+CMSClassUnloadingEnabled"
)

version := "0.1.0"

parallelExecution in test := false

scalaVersion := "2.13.13"
version := "1.0"

val akkaHttpVersion = "10.5.3"
val akkaVersion = "2.7.0"
val swaggerVersion = "2.0.8"

inThisBuild(
  List(
    organization := "org.somind",
    homepage := Some(
      url("https://github.com/navicore/dtlab-ingest-scala-alligator")
    ),
    licenses := List(
      "MIT" -> url(
        "https://github.com/navicore/dtlab-ingest-scala-alligator/blob/master/LICENSE"
      )
    ),
    developers := List(
      Developer(
        "navicore",
        "Ed Sweeney",
        "ed@onextent.com",
        url("https://navicore.tech")
      )
    )
  )
)

libraryDependencies ++=
  Seq(
    "io.altoo" %% "akka-kryo-serialization" % "2.5.2",
    "org.postgresql" % "postgresql" % "42.6.0",
    "com.lightbend.akka" %% "akka-persistence-jdbc" % "5.2.1",
    "tech.navicore" %% "navipath" % "4.1.3",
    "ch.megard" %% "akka-http-cors" % "1.2.0",
    "ch.qos.logback" % "logback-classic" % "1.5.6",
    "com.typesafe" % "config" % "1.4.3",
    "com.typesafe" %% "ssl-config-core" % "0.6.1",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
    "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "org.scalatest" %% "scalatest" % "3.2.18" % "test",
    "org.json4s" %% "json4s-native" % "4.0.7",
    "com.github.nscala-time" %% "nscala-time" % "2.32.0"
  )

assemblyJarName in assembly := s"${name.value}.jar"

assemblyMergeStrategy in assembly := {
  case PathList("reference.conf")                      => MergeStrategy.concat
  case x if x.endsWith("io.netty.versions.properties") => MergeStrategy.first
  case PathList("META-INF", _ @_*)                     => MergeStrategy.discard
  case _                                               => MergeStrategy.first
}
