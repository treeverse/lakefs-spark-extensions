lazy val projectVersion = "0.0.3-RC.0"

version := projectVersion

semanticdbEnabled := true
semanticdbVersion := scalafixSemanticdb.revision
scalacOptions += "-Ywarn-unused-import"

libraryDependencies ++= Seq(
  "io.lakefs" % "api-client" % "0.91.0",
  "org.apache.spark" %% "spark-sql" % "3.2.4" % "provided",
  "org.apache.spark" %% "spark-hive" % "3.2.4" % "test",
  "joda-time" % "joda-time" % "2.12.5" % "test",

  // TODO(ariels): Wrap api-client calls in: "dev.failsafe" % "failsafe" % "3.2.4",

  "org.scalatest" %% "scalatest" % "3.2.16" % "test",
  "org.scalatestplus" %% "scalacheck-1-17" % "3.2.16.0" % "test",
)

val nexus = "https://s01.oss.sonatype.org/"
lazy val publishSettings = Seq(
  // Remove all additional repository other than Maven Central from POM
  pomIncludeRepository := { _ => false },
  credentials ++= Seq(
    Credentials(Path.userHome / ".sbt" / "credentials"),
    Credentials(Path.userHome / ".sbt" / "sonatype_credentials")
  ),
)

ThisBuild / publishConfiguration := publishConfiguration.value.withOverwrite(true)
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
ThisBuild / publishTo := {
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/treeverse/lakefs-spark-extensions"),
    "scm:git@github.com:treeverse/lakefs-spark-extensions.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id = "ariels",
    name = "Ariel Shaqed (Scolnicov)",
    email = "ariels@treeverse.io",
    url = url("https://github.com/arielshaqed")
  ),
  Developer(
    id = "baraktr",
    name = "B. A.",
    email = "barak.amar@treeverse.io",
    url = url("https://github.com/nopcoder")
  ),
  Developer(
    id = "ozkatz",
    name = "Oz Katz",
    email = "oz.katz@treeverse.io",
    url = url("https://github.com/ozkatz")
  ),
  Developer(
    id = "johnnyaug",
    name = "J. A.",
    email = "yoni.augarten@treeverse.io",
    url = url("https://github.com/johnnyaug")
  ),
  Developer(
    id = "itai.admi",
    name = "Itai Admi",
    email = "itai.admi@treeverse.io",
    url = url("https://github.com/itaiad200")
  ),
  Developer(
    id = "niro",
    name = "Nir Ozery",
    email = "nir.ozery@treeverse.io",
    url = url("https://github.com/N-o-Z")
  )
)
