package com.gl.spark;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class HiveOperations {
	
	public static void main(String[] args) {
		

		SparkSession  sparkSession = SparkSession.builder().appName("sparksql").master("local[*]").config("spark.sql.warehour.dir","file:///home/rajeev/tmp/").enableHiveSupport().getOrCreate();

		setupHiveDb(sparkSession);
		Dataset<Row> dataSet= sparkSession.read().csv("src/com/gl/spark/input.csv");
		dataSet.show();

//		dataSet.createOrReplaceTempView("mobile_call_usages");

//		Dataset<Row> rows= 	sparkSession.sql("select _c0 as id ,_c2 as date, sum(_c1) as sum,sum(_c1)/count(_c1) as average  from mobile_call_usages group by id,date order by id,date");
//		rows.show();
		dataSet.write().saveAsTable("dummy_db.mobile_call_usages");
		
		sparkSession.close();
	}
	
	
	
	private static void setupHiveDb(SparkSession session) {
		session.sql("CREATE DATABASE IF NOT EXISTS dummy_db location 'src/com/gl/spark/dummy_db.db'");
		session.sql("use dummy_db");
		session.sql("drop table if exists  dummy_db.mobile_call_usages");
//		session.sql("create table dummy_db.mobile_call_usages(_c0 string, _c2 string, _c1 int, _c4 double)");
	}

}
