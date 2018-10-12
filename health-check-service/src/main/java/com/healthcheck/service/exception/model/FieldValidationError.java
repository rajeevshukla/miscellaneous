package com.healthcheck.service.exception.model;

public class FieldValidationError {

	private String field;
	private String code;
	private Object rejectedValue;
	private String errorMsg;

	public FieldValidationError(String field, String code, Object rejectedValue, String errorMsg) {
		super();
		this.field = field;
		this.code = code;
		this.rejectedValue = rejectedValue;
		this.errorMsg = errorMsg;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Object getRejectedValue() {
		return rejectedValue;
	}

	public void setRejectedValue(Object rejectedValue) {
		this.rejectedValue = rejectedValue;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

}
