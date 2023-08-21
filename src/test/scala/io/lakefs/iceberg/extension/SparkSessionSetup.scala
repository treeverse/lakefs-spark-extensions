package io.lakefs.iceberg.extension

import java.nio.file.Files
import org.apache.commons.io.FileUtils
import scala.reflect.ClassTag

import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkConf

trait SparkSessionWithExtensionSetup[E] {
  def withSparkSession(testMethod: (SparkSession) => Any)(implicit tag: ClassTag[E]) {
    val tmpDir = Files.createTempDirectory("sparktest").toString
    val conf = new SparkConf()
      .setMaster("local")
      .setAppName("Spark test")
      .set("spark.sql.extensions", tag.runtimeClass.getCanonicalName)
      .set("spark.sql.warehouse.dir", tmpDir)
    val spark = new SparkSession.Builder()
      .appName("extension-test")
      .config(conf)
      .enableHiveSupport()
      .getOrCreate
    try {
      testMethod(spark)
    } finally {
      // Clean up catalog dir
      FileUtils.deleteDirectory(new java.io.File(tmpDir))
      // local metastore_db always created in current working directory, and
      // current working directory cannot be changed in Java.
      FileUtils.deleteDirectory(new java.io.File("./metastore_db"))
      spark.close()
    }
  }
}
