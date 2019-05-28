package com.windstream.voip.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.NOT_FOUND)
public class UserNotFoundException extends VoIPServiceException {

	private static final long serialVersionUID = 2256945652551176563L;
	
	public UserNotFoundException(String message, Throwable t) {
		super(message, t);
	}
	
	public UserNotFoundException(String message) {
		super(message);
	}
}
