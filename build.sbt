lazy val commonSettings = Seq(
  organization := "dekkR projects",
  version := "0.1",
  scalaVersion := "2.11.6"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "pagefetcher"
  )



libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.scalaj"          %% "scalaj-http"    % "0.3.16",
    "org.specs2"          %%  "specs2-core"   % "2.3.13" % "test"
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
