package com.java8.test;

import java.util.stream.IntStream;

import org.junit.Test;

public class MyTheadLambda {
	
	@Test
	public void testThead() throws Exception { 
		
		System.out.println("Started");
		Thread thread = new Thread(()-> IntStream.range(0, 20).forEach((i)->System.out.println(Thread.currentThread().getName()+" "+i)));
		
		thread.start();
		
		thread.join();
		System.out.println("Ended");
	}

}
