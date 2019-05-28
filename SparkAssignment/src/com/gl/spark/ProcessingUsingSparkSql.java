package com.gl.spark;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class ProcessingUsingSparkSql {

	public static void main(String[] args) {


		SparkSession  sparkSession = SparkSession.builder().appName("sparksql").master("local[*]").config("spark.sql.warehour.dir","file:///home/rajeev/tmp/").getOrCreate();

		Dataset<Row> dataSet= sparkSession.read().csv("src/com/gl/spark/input.csv");
		dataSet.show();

		dataSet.createOrReplaceTempView("mobile_call_usages");

		Dataset<Row> rows= 	sparkSession.sql("select _c0,_c2, sum(_c1),sum(_c1)/count(_c1)  from mobile_call_usages group by _c0,_c2 order by _c0,_c2");
		rows.show();

	}

}
