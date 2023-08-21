package io.lakefs.iceberg.extension

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should

import org.apache.spark.sql.Row

class ExtensionSpec extends AnyFunSpec
    with SparkSessionWithExtensionSetup[LakeFSSparkSessionExtensions]
    with should.Matchers {

  val _ = new LakeFSSparkSessionExtensions()

  describe("Extension") {
    it("should SELECT") {
      withSparkSession(spark => {
        import spark.implicits._

        spark.sql("CREATE DATABASE first")
        val df = Seq(("a", 1), ("b", 2), ("c", 3)).toDF
        df.writeTo("spark_catalog.first.table").create()

        spark.sql("CREATE DATABASE second")
        val df2 = Seq(("a", 1), ("xyzzy", 2), ("c", 3), ("d", 4)).toDF
        df2.writeTo("spark_catalog.second.table").create()

        val diff = spark.sql("SELECT * FROM schema_diff('spark_catalog', 'first', 'second', 'table')")
          .collect()
          .toSet
        diff should equal(Set(Row("-", "b", 2), Row("+", "xyzzy", 2), Row("+", "d", 4)))
      })
    }

    // TODO(ariels): Test SQL identifier quotation.
  }
}
