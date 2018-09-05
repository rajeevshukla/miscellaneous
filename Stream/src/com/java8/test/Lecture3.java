package com.java8.test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

public class Lecture3 {

	
	
	@Test
	public void min() throws Exception { 
		List<Integer> intList = Arrays.asList(2,5,7,9,5,1,4,32,86,03,85,32);
		
		int number = intList.stream().min((number1, number2)-> number1> number2 ? 1:-1).get();
		System.out.println("Min Number is : ");
		System.out.println(number);
	}
	
	
	@Test
	public void max() throws Exception { 
		List<Integer> intList = Arrays.asList(2,5,7,9,5,1,4,32,86,03,85,32);
	   int number = intList.stream().max(Comparator.naturalOrder()).get();
	   System.out.println("Max Numer Is : ");
	   System.out.println(number);
		
		
		
	}
}
