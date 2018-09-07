package com.java8.exercise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import com.java8.util.MockData;
import com.stream.model.Car;
import com.stream.model.Person;

public class Exercises {



	@Test
	public void find10YoungPeople() throws Exception { 

		List<Person> people = MockData.getPeople();

		people.stream()
		.filter(person->person.getAge()<=18)
		.limit(10)
		.collect(Collectors.toList())
		.forEach(System.out::println);
	}


	@Test
	public void printEvenNumberUsingStreamIterator() throws Exception { 

		IntStream.iterate(0, a->a+1).
		filter(a->a%2==0).
		limit(20).sorted().
		forEach(System.out::println);



		List<Car> cars = MockData.getCars();

		Optional<Car> car = cars.stream().reduce((car1, car2)-> car1.getPrice()>car2.getPrice()? car1:car2);

		if(car.isPresent()) {
			System.out.println("Car Having Highest price : "+ car);
		}
		else {
			System.out.println("Not found");
		}

		double carPrice = 	cars.stream().mapToDouble(Car::getPrice).max().orElseGet(()-> 0);
		System.out.println(carPrice);
		
		
		List<String> stringList = Arrays.asList("abc","xabc","syzdfafdabc","1abc","5abc","6bc");
		
		stringList
				.stream()
				.reduce((word1,word2)-> word1.length()>word2.length() ?word1:word2)
				.ifPresent(System.out::println);
		
		
	}


	@Test
	public void findMaxSalary() throws Exception { 

	}

	@Test 

	public void findMinSalary() throws Exception { 

	}

	@Test
	public void collectCarsGroupByMakers() throws Exception { 

	}

	@Test
	public void countCarsGroupByMakers() throws Exception { 

	}


	@Test
	public void increaseCarPriceBy10P()  throws Exception { 

	}


	@Test 
	public void averageSalary()  throws Exception { 

	}
}
