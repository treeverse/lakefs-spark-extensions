package io.lakefs.iceberg.extension

import org.apache.spark.sql.{SparkSession, SparkSessionExtensions}
//import org.apache.spark.sql.AnalysisException
import org.apache.spark.sql.catalyst.FunctionIdentifier
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan
import org.apache.spark.sql.catalyst.expressions.{Expression, ExpressionInfo}
import org.apache.spark.sql.catalyst.expressions.StringLiteral


// A table-valued function to compute the difference between the same table
// at two schemas.
object SchemaDiff {
  private def computeString(e: Expression): String = {
    val literalValue = StringLiteral.unapply(e)
    literalValue match {
      case None => throw new RuntimeException(s"$e not a literal string")
      case Some(s) => s
    }
  }

  private def tableAt(prefix: String, schema: String, suffix: String): String = {
    val spark = SparkSession.getActiveSession match {
      case None => throw new RuntimeException("Whoops!  No Spark session...")
      case Some(spark) => spark
    }
    val parseId = spark.sessionState.sqlParser.parseMultipartIdentifier(_)
    val parts = parseId(prefix) ++ Seq(schema) ++ parseId(suffix)
    parts.map(str => s"`$str`").mkString(".")
  }

  private def sql(prefix: String, fromSchema: String, toSchema: String, suffix: String) = {
    val fromTableName = tableAt(prefix, fromSchema, suffix)
    val toTableName = tableAt(prefix, toSchema, suffix)

    s"""
      (SELECT '+', * FROM (SELECT * FROM $toTableName EXCEPT SELECT * FROM $fromTableName))
      UNION ALL
      (SELECT '-', * FROM (SELECT * FROM $fromTableName EXCEPT SELECT * FROM $toTableName))
    """
  }

  private def tdfBuilder(e: Seq[Expression]): LogicalPlan = {
    val spark = SparkSession.getActiveSession match {
      case None => throw new RuntimeException("Whoops: No spark session!")
      case Some(spark) => spark
    }
    if (e.size != 4) {
      throw new RuntimeException(s"Need exactly 4 arguments <tablePrefix, fromSchema, toSchema, tableSuffix>, got $e")
    }
    val Seq(tablePrefix, fromSchema, toSchema, tableSuffix) = e.map(computeString)
    val sqlString = sql(tablePrefix, fromSchema, toSchema, tableSuffix)
    spark.sql(sqlString).queryExecution.logical
  }

  val function = (FunctionIdentifier("schema_diff"),
    new ExpressionInfo("io.lakefs.iceberg.extension.SchemaDiff$",
      "", "schema_diff", "schema_diff('TABLE_PREFIX', 'FROM_SCHEMA', 'TO_SCHEMA', 'TABLE_SUFFIX')",
      "schema_diff('TABLE_PREFIX', 'FROM_SCHEMA', 'TO_SCHEMA', 'TABLE_SUFFIX')"),
    tdfBuilder _)
}

class LakeFSSparkSessionExtensions extends (SparkSessionExtensions => Unit) {
  override def apply(extensions: SparkSessionExtensions): Unit = {
    extensions.injectTableFunction(SchemaDiff.function)
  }
}
