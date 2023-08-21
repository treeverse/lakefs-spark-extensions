lazy val projectVersion = "0.0.1"

lazy val project = Project("lakefs-spark-extension", file("./src/"))
  .settings(
    version := projectVersion,
  )

semanticdbEnabled := true
semanticdbVersion := scalafixSemanticdb.revision
scalacOptions += "-Ywarn-unused-import"

libraryDependencies ++= Seq(
  "io.lakefs" % "api-client" % "0.91.0",
  "org.apache.spark" %% "spark-sql" % "3.2.4" % "provided",

  // TODO(ariels): Wrap api-client calls in: "dev.failsafe" % "failsafe" % "3.2.4",

  "org.scalatest" %% "scalatest" % "3.2.16" % "test",
  "org.scalatestplus" %% "scalacheck-1-17" % "3.2.16.0" % "test",
)

val nexus = "https://s01.oss.sonatype.org/"
lazy val publishSettings = Seq(
  publishTo := {
    if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  // Remove all additional repository other than Maven Central from POM
  pomIncludeRepository := { _ => false },
  credentials ++= Seq(
    Credentials(Path.userHome / ".sbt" / "credentials"),
    Credentials(Path.userHome / ".sbt" / "sonatype_credentials")
  )
)

ThisBuild / isSnapshot := false
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / organization := "io.lakefs"
ThisBuild / organizationName := "Treeverse Labs"
ThisBuild / organizationHomepage := Some(url("http://treeverse.io"))
ThisBuild / description := "Playing about with defining TDFs."
ThisBuild / licenses := List(
  "Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")
)
ThisBuild / homepage := Some(url("https://lakefs.io"))
