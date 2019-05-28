package com.java8.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.stream.model.Car;
import com.stream.model.Employee;
import com.stream.model.Person;

public class MockData {

	public static ImmutableList<Person> getPeople() throws IOException {
		//Resources from Guava library 
		InputStream inputStream = Resources.getResource("people.json").openStream();
		String jsonData = IOUtils.toString(inputStream, "UTF-8");

		java.lang.reflect.Type listType = new TypeToken<ArrayList<Person>>() {
			private static final long serialVersionUID = -2769410970603534670L;
		}.getType();
		
		List<Person> persons = new Gson().fromJson(jsonData, listType);
		return ImmutableList.copyOf(persons);

	}
	
	
	public static ImmutableList<Car> getCars() throws IOException { 
		InputStream inputStream = Resources.getResource("cars.json").openStream();
		String jsonData = IOUtils.toString(inputStream, "UTF-8");
		Type listType = new TypeToken<ArrayList<Car>>() {
			private static final long serialVersionUID = -6666490026343525445L;
			
		}.getType();
		
		List<Car> carList = new Gson().fromJson(jsonData, listType);
		
		return ImmutableList.copyOf(carList);
	}
	
	
	public static ImmutableList<Employee> getEmployees() throws IOException { 
		
		    InputStream inputStream =  Resources.getResource("employees").openStream();
		    
		    String jsonData = IOUtils.toString(inputStream, "UTF-8");
		    Type listType = new TypeToken<ArrayList<Employee>>() {
				private static final long serialVersionUID = -6666490026343525445L;
				
			}.getType();
			List<Employee> employees = new Gson().fromJson(jsonData, listType);
			return ImmutableList.copyOf(employees);
		     
	}
	
	

}
