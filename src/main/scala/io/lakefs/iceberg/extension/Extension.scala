package io.lakefs.iceberg.extension

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.AnalysisException
import org.apache.spark.sql.catalyst.FunctionIdentifier
import org.apache.spark.sql.catalyst.expressions.{Expression, ExpressionInfo}
import org.apache.spark.sql.catalyst.expressions.StringLiteral


// A table-valued function that adds a column to a table.
//
// NEVER USE THIS, it allows trivial SQL injections!
object WithColumn {
  private def sql(tableName String, columnName String, columnExpression String) =
    // BUG(ariels): Dangerous, allows SQL injections!
    s"SELECT *, ${columnExpression} ${columnName} FROM ${tableName}"

  private def computeString(e Expression): String = {
    val literalValue = StringLiteral(e)
    literalValue match {
      case None => throw new AnalysisException(s"${e} not a literal string")
      case Some s => s
    }
  }

  private def tdfBuilder(e: Seq[Expression]): LogicalPlan = {
    val spark = SparkSession.getActiveSession() match {
      case None => throw new AnalysisException("Whoops: No spark session!")
      case Some spark => spark
    }
    if (e.size != 3) {
      throw new AnalysisException(s"Need exactly 3 arguments <tableName, columnName, columnExpression>, got ${e}")
    }
    val Seq(tableName, columnName, columnExpression) = e.map(computeString)
    val sqlString = sql(tableName, columnName, columnExpression)
    spark.sql(sqlString)
  }

  val function = (FunctionIdentifier("with_column"),
    new ExpressionInfo("io.lakefs.iceberg.extension.WithColumn$",
      null, "with_column", "with_column('TABLE', 'NEW_COLUMN', 'NEW_COLUMN_EXPRESSION')"),
    tdfBuilder)b
}

class FooSparkSessionExtensions extends (SparkSessionExtensions => Unit) {
  override def apply(extensions: SparkSessionExtensions): Unit = {
    extensions.injectTableFunction(function)
  }
}
