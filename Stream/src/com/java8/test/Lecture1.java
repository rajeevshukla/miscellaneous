package com.java8.test;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.java8.util.MockData;
import com.stream.model.Person;

public class Lecture1 {

	@Ignore
	@Test
	public void imperativeApproachUsingCollections() throws Exception {

		List<Person> people = MockData.getPeople();

		List<Person> youngAge = Lists.newArrayList();

		for (Person person : people) {
			if (person.getAge() <= 18) {
				youngAge.add(person);
			}

			if (youngAge.size() == 10)
				break;
		}

		for (Person person : youngAge) {
			System.out.println(person.getAge());
		}
	}

	@Test
	public void decrativeApproachUsingStream() throws Exception {

		List<Person> people = MockData.getPeople();

		people.stream()
		.filter(person -> person.getAge() <= 18)
		.limit(10)
		.collect(Collectors.toList())
		.forEach(person -> System.out.println(person.getAge()));

	}
}
