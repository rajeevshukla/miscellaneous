package com.windstream.voip.exception.model;

import java.util.Date;

public class ErrorDetails {

	private Date timestamp;
	private String message;
	private String requestDetails;
	private Object errorDetails;
	
	
	public ErrorDetails(Date timestamp, String message, Object errorDetails, String requestDetails) {
		this.timestamp = timestamp;
		this.message = message;
		this.errorDetails = errorDetails;
		this.requestDetails = requestDetails;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public String getMessage() {
		return message;
	}
	public Object getErrorDetails() {
		return errorDetails;
	}
	public void setErrorDetails(Object errorDetails) {
		this.errorDetails = errorDetails;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getRequestDetails() {
		return requestDetails;
	}
	public void setRequestDetails(String requestDetails) {
		this.requestDetails = requestDetails;
	}
	@Override
	public String toString() {
		return "ErrorDetails [timestamp=" + timestamp + ", message=" + message + ", requestDetails=" + requestDetails
				+ ", errorDetails=" + errorDetails + "]";
	}
	
}
