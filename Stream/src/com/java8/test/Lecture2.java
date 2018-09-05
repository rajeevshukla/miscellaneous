package com.java8.test;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.Ignore;
import org.junit.Test;

import com.java8.util.MockData;
import com.stream.model.Person;

public class Lecture2 {

	@Ignore
	@Test
	public void range() throws Exception { 
		System.out.println("Exclusive");
		IntStream.range(0, 20).forEach(System.out::println);

		System.out.println("Inclusive");
		IntStream.rangeClosed(0, 20).forEach(System.out::println);
	}


	@Ignore
	@Test
	public void iteratePeople() throws Exception { 
		List<Person> people = MockData.getPeople();
		IntStream.range(0, people.size()).forEach(i -> System.out.println(people.get(i)));
	}


	@Test
	public void intStreamIterate()  throws Exception { 
		IntStream
		.iterate(0, operand ->  operand + 1 )
		.filter(number -> number %2==0)
		.limit(20)
		.forEach(System.out::println);
	}
}
