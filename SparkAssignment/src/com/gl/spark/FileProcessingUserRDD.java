package com.gl.spark;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;


public class FileProcessingUserRDD {



	public static void main(String[] args) throws InterruptedException {

		Logger.getLogger("org.apache").setLevel(Level.INFO);


		// for local
//		SparkConf conf = new SparkConf().setAppName("FileProcessing").setMaster("local[*]");
		// for standalone cluster
		SparkConf conf = new SparkConf().setAppName("FileProcessing");

		JavaSparkContext context = new JavaSparkContext(conf);

		JavaRDD<String> inputRDD = context.textFile("src/com/gl/spark/input.csv");



		JavaPairRDD<Tuple2<String,String>, Integer> pairedWithIdDate = inputRDD.mapToPair(line->{
			String arr[]= line.split(",");
			return new Tuple2<Tuple2<String, String>, Integer>(new Tuple2<String, String>(arr[0], arr[2]),Integer.parseInt(arr[1]));
		});

		//  JavaPairRDD<Tuple2<String,String>, Iterable<Integer>> groupedRDD = pairedWithIdDate.groupByKey();
		//	groupedRDD.foreach(v->System.out.println(v._1._1 +","+v._1._2+":"+v._2));

		//1. Calculating sum
		JavaPairRDD<Tuple2<String, String>, Integer> sumRdd = pairedWithIdDate.reduceByKey((v1,v2)->  v1 + v2 );

		sumRdd.foreach(t->System.out.println(t._1+" :"+t._2));

		//2.finding max 
		JavaPairRDD<Tuple2<String, String>, Integer> maxRdd = pairedWithIdDate.reduceByKey((v1,v2)->v1 >v2 ? v1 :v2);
		maxRdd.foreach(t->System.out.println(t._1+" :"+t._2));

		//3.finding min 
		JavaPairRDD<Tuple2<String, String>, Integer> minRdd = pairedWithIdDate.reduceByKey((v1,v2)->v1<v2 ? v1 :v2);
		minRdd.foreach(t->System.out.println(t._1+" :"+t._2));

		//4. Finding average

		JavaPairRDD<Tuple2<String, String>, Double> averageRDD = 
				pairedWithIdDate.mapValues(value-> new Tuple2<Integer,Long>(value, 1l))//4.1 First figure out the total count for each pair (this will become tuple, tuple
				.reduceByKey((t1,t2)-> new Tuple2<Integer, Long>(t1._1+t2._1, t1._2+ t2._2)) // 4.2 Calculating total sum and total elements by each pair
				.mapToPair((data) ->{                        // now calculating average and return as pair (this will become tuple, average)
					Tuple2<Integer, Long> sumAndTotalElement = data._2;
					return new Tuple2<Tuple2<String, String>,Double>(data._1,(double)sumAndTotalElement._1/sumAndTotalElement._2);
				});
		averageRDD.foreach(t->System.out.println(t._1+" :"+t._2));
		
		//Joining all and dumping them on file(without extracting them from tuple. I need to work on it) 
		sumRdd.join(maxRdd).join(minRdd).join(averageRDD).saveAsTextFile("src/com/gl/spark/output.csv");


		context.close();
	}

}
