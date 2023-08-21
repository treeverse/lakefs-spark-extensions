# Spark SQL Extensions for lakeFS

## Usage

In order to use the Spark extensions, you will need to _load_ them and then
_add_ them to Spark.

From Maven Repository: _wait until we upload an initial version, until then
see [Development](#Development)


Add:

```
--conf spark.sql.extensions=io.lakefs.iceberg.extension.LakeFSSparkSessionExtensions \
--packages io.lakefs:spark-extensions:<VERSION>
```

to your `spark-*` command-line, or add this package to your
`spark.jars.packages` configuration.

### Development

Run `sbt package`, then add

```
--conf spark.sql.extensions=io.lakefs.iceberg.extension.LakeFSSparkSessionExtensions \
--jars ./target/scala-2.12/lakefs-spark-extensions_2.12-0.1.0-SNAPSHOT.jar`
```

## Available extensions

### Schema diff

`refs_data_diff` is a Spark SQL table-valued function.  The expression

```sql
refs_data_diff(PREFIX, FROM_SCHEMA, TO_SCHEMA, TABLE)
```

yields a relation that compares the "from" table `PREFIX.FROM_SCHEMA.TABLE`
with the "to" table `PREFIX.TO_SCHEMA.TABLE`.  Elements of "to" but not
"from" are _added_ and appear with `lakefs_change='+'`, elements of "from" but not
"to" are _deleted_ and appear with `lakefs_change='-'`.

For instance,

```sql
SELECT lakefs_change, Player, COUNT(*) FROM refs_data_diff('lakefs', 'main~', 'main', 'db.allstar_games')
GROUP BY lakefs_change, Player;
```

uses lakeFS Iceberg support to compute how many rows were changed for each
player in the last commit.

Internally this relation is exactly a `SELECT` expression.  For instance,
you can set up a view with it:

```sql
CREATE TEMPORARY VIEW diff_allstar_games_main_last_commit AS
    refs_data_diff('lakefs', 'main~', 'main', 'db.allstar_games');
```
