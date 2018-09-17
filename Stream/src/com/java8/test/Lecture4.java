package com.java8.test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import com.java8.util.MockData;
import com.stream.model.Car;

public class Lecture4 {
	
	@Test
	@Ignore
	public void groupCars() throws Exception { 
		
		List<Car> cars = MockData.getCars();
		
		 Map<String, List<Car>> collect = cars.stream().sorted((car1,car2)-> car1.getId()>car2.getId()?1:-1).collect(Collectors.groupingBy(car -> car.getMake()));
		 
		 System.out.println(collect);
		 
		 collect.forEach((make, carList)-> {
			 System.out.println(make);
			 carList.forEach(System.out::println);
		 });
		 
		 
		 
		 
		 
		 /*
		 collect.forEach((make, carList)->{ System.out.println(make+"::");
		 cars.forEach(System.out::println);
		 });
		 */
	}

	
	public void average() throws Exception{ 
		List<Car> cars = MockData.getCars();
		double average = cars.stream().mapToDouble(Car::getPrice).average().getAsDouble();
		
		System.out.println(average);
	}
	
	
	
	@Test 
	public void countGroupByMake() throws Exception { 
		
		Map<String, Long> collect = MockData.getCars()
		.stream()
		.collect(Collectors.groupingBy(Car::getMake, Collectors.counting()));
			
		collect.forEach((make,count) -> System.out.println(make +" > "+ count));
		
	}
	
	
	
	
}
