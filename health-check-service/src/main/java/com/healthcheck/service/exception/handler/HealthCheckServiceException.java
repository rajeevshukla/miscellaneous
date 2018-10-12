package com.healthcheck.service.exception.handler;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.healthcheck.service.exception.model.ApiError;
import com.healthcheck.service.exception.model.FieldValidationError;

@ControllerAdvice
public class HealthCheckServiceException extends ResponseEntityExceptionHandler {

	// if any excpetion occurred on server that throws exception will automatically
	// be arive
	// at this method, appropriate message will be sent to client automatically. 
	@ExceptionHandler(value = { Exception.class })
	public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {

		ApiError error = new ApiError(ex.getMessage(), request.getDescription(false), new Date(),
				ex.getLocalizedMessage());

		return new ResponseEntity<Object>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		ApiError apiError = new ApiError("Validation failed", request.getDescription(false), new Date(),
				ex.getBindingResult().getFieldErrors().stream().map(field -> new FieldValidationError(field.getField(),
						field.getCode(), field.getRejectedValue(), field.getDefaultMessage()))
						.collect(Collectors.toList()));
		return new ResponseEntity<Object>(apiError, HttpStatus.BAD_REQUEST);

	}

}
