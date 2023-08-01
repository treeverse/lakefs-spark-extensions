lazy val project = Project("lakefs-spark-extension", file("./src/"))

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

ThisBuild / versionScheme := Some("early-semver")
ThisBuild / organization := "io.lakefs"
ThisBuild / organizationName := "Treeverse Labs"
ThisBuild / organizationHomepage := Some(url("http://treeverse.io"))
ThisBuild / description := "Playing about with defining TDFs."
ThisBuild / licenses := List(
  "Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")
)
ThisBuild / homepage := Some(url("https://lakefs.io"))
