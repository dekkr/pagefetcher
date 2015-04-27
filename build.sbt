lazy val commonSettings = Seq(
  organization := "dekkR projects",
  version := "0.3.1",
  scalaVersion := "2.11.6"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "pagefetcher"
  )

libraryDependencies ++= {
  val akkaV = "2.3.10"
  val akkaStreamV = "1.0-M5"
  val sprayV = "1.3.3"
  val activateVersion = "1.7"
  val scalaLoggingVersion = "3.1.0"
  val slf4jVersion = "1.7.10"
  val logbackVersion = "1.1.2"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
    "com.typesafe.akka" %% "akka-stream-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-core-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-testkit-experimental" % akkaStreamV,
    "org.jsoup" % "jsoup" % "1.7.2",
    "commons-validator" % "commons-validator" % "1.4.1",
    "org.scalaj" %% "scalaj-http" % "0.3.16",
    "org.specs2" %% "specs2-core" % "2.3.13" % "test",
    "net.fwbrasil" %% "activate-core" % activateVersion,
    "net.fwbrasil" %% "activate-prevayler" % activateVersion,
    "net.fwbrasil" %% "activate-jdbc" % activateVersion,
    "net.fwbrasil" %% "activate-mongo" % activateVersion,
    "org.postgresql" % "postgresql" % "9.2-1003-jdbc4",
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "org.slf4j" % "slf4j-api" % slf4jVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion
  )
}

Revolver.settings

scalacOptions in ThisBuild ++= Seq(Opts.compile.deprecation, Opts.compile.unchecked) ++
  Seq("-Ywarn-unused-import", "-Ywarn-unused", "-Xlint", "-feature")

Seq(buildInfoSettings:_*)

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](version)

buildInfoPackage := "pagefetcher"

publishMavenStyle := true

licenses := Seq(
  ("MIT", url(s"https://github.com/dekkr/${name.value}/blob/${version.value}/LICENSE")))

bintraySettings

bintray.Keys.bintrayOrganization in bintray.Keys.bintray := Some("dekkr")

bintray.Keys.repository in bintray.Keys.bintray := "feedr"

bintray.Keys.packageLabels in bintray.Keys.bintray := Seq("microservice", "caching")

lsSettings

LsKeys.tags in LsKeys.lsync := (bintray.Keys.packageLabels in bintray.Keys.bintray).value

externalResolvers in LsKeys.lsync := (resolvers in bintray.Keys.bintray).value

pomExtra :=
  <url>http://dekkr.nl</url>
    <licenses>
      <license>
        <name>MIT</name>
        <url>https://github.com/dekkr/${name.value}/blob/${version.value}/LICENSE</url>
      </license>
    </licenses>
    <developers>
      <developer>
        <id>plamola</id>
        <name>Matthijs Dekker</name>
        <email>projects@dekkr.nl</email>
        <organization>dekkR.nl</organization>
        <organizationUrl>http://dekkr.nl</organizationUrl>
      </developer>
    </developers>
    <scm>
      <connection>scm:git:git@github.com:dekkr/{name.value}.git</connection>
      <developerConnection>scm:git:git@github.com:dekkr/{name.value}.git</developerConnection>
      <url>git@github.com:dekkr/{name.value}.git</url>
    </scm>
