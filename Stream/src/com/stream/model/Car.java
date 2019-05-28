package com.stream.model;

public class Car {

	private  Integer id;
	private  String make;
	private  String model;
	private  String color;
	private  Integer year;
	private   Double price;

	public Integer getId() {
		return id;
	}

	public String getMake() {
		return make;
	}

	public String getModel() {
		return model;
	}

	public String getColor() {
		return color;
	}

	public Integer getYear() {
		return year;
	}

	public Double getPrice() {
		return price;
	}

	@Override
	public String toString() {
		return "Car [id=" + id + ", make=" + make + ", model=" + model + ", color=" + color + ", year=" + year
				+ ", price=" + price + "]";
	}

}
