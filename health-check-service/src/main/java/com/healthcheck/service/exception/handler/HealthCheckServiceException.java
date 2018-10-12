package com.healthcheck.service.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class HealthCheckServiceException extends ResponseEntityExceptionHandler{
	
	
	// if any excpetion occurred on server that throws exception will automatically be  arive 
	//at this method and approprivated 
	@ExceptionHandler(value= {Exception.class})
	public ResponseEntity<Object> handleGlobalException() { 
		
		
		return new ResponseEntity<Object>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
