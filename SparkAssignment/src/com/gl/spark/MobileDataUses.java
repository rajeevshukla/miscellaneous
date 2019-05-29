package com.gl.spark;



public class MobileDataUses {

	private String customerId;
	private String usages;
	private String date;
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getUsages() {
		return usages;
	}
	public void setUsages(String usages) {
		this.usages = usages;
	}
	@Override
	public String toString() {
		return "MobileDataUses [customerId=" + customerId + ", usages=" + usages + ", date=" + date + "]";
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	
	
}
