package com.healthcheck.service.exception.model;

import java.util.Date;

public class ApiError {
	private String message;
	private String requestDetails;
	private Date timestamp;
	private Object errorDetails;

	public ApiError(String message, String requestDetails, Date timestamp, Object errorDetails) {
		super();
		this.message = message;
		this.requestDetails = requestDetails;
		this.timestamp = timestamp;
		this.errorDetails = errorDetails;
	}

	public String getMessage() {
		return message;
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

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Object getErrorDetails() {
		return errorDetails;
	}

	public void setErrorDetails(Object errorDetails) {
		this.errorDetails = errorDetails;
	}

}
