package com.gl.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import com.gl.spark.utils.ApplicationUtils;

public class SparkSerializationTest {


	public static void main(String[] args) {

		SparkConf conf = new SparkConf().setAppName("SerializationTest").setMaster("local[*]");
		conf.set("spark.kryo.registrator",  "com.gl.spark.ClassRegstrator");

		conf.registerKryoClasses(new Class[] {ApplicationUtils.class,MobileDataUses.class});
		JavaSparkContext context = new JavaSparkContext(conf);

		JavaRDD<String> javaRDD = context.textFile("src/com/gl/spark/input.csv");

//		javaRDD.foreach(System.out::println);
		ApplicationUtils applicationUtils = new ApplicationUtils();
		JavaRDD<MobileDataUses> mobileDataUses = javaRDD.map(line->applicationUtils.convert(line));

        mobileDataUses.collect();
		
		context.close();

	}


}
