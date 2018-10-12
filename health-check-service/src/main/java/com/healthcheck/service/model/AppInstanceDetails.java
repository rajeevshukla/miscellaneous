package com.healthcheck.service.model;

public class AppInstanceDetails {

	private String instanceId;
	private String appName;
	private int noOfInstances;
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public int getNoOfInstances() {
		return noOfInstances;
	}
	public void setNoOfInstances(int noOfInstances) {
		this.noOfInstances = noOfInstances;
	}
	@Override
	public String toString() {
		return "AppInstanceDetails [instanceId=" + instanceId + ", appName=" + appName + ", noOfInstances="
				+ noOfInstances + "]";
	}
	
	
}
