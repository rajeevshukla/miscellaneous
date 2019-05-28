package com.windstream.voip.exception.handler;

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

import com.windstream.voip.exception.EnterpriseNotFoundException;
import com.windstream.voip.exception.UserNotFoundException;
import com.windstream.voip.exception.VoIPServiceException;
import com.windstream.voip.exception.model.ErrorDetails;
import com.windstream.voip.exception.model.FieldValidationError;
import com.windstream.voip.util.ApplicationConstants;

@ControllerAdvice
public class GenericExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({Exception.class })
	public final ResponseEntity<?> handleGenericExceptions(Exception ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), ex.getCause()!=null ? ex.getCause().getMessage():ex.getMessage(),
				request.getDescription(false));
		// logging all the exceptions here. Just in case an anything is uncaught
       logger.error(ex);
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	@ExceptionHandler({ VoIPServiceException.class})
	public final ResponseEntity<?> handleVoIPExceptions(Exception ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), ex.getCause()!=null ? ex.getCause().getMessage():ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	

	@ExceptionHandler({UserNotFoundException.class, EnterpriseNotFoundException.class})
	public final ResponseEntity<?> handleResourceNotFoundExceptions(Exception ex, WebRequest request) {

		
		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), ex.getCause()!= null ? ex.getCause().getMessage() : ex.getMessage(),
				request.getDescription(false));

		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		ErrorDetails errorDetails = new ErrorDetails(
				new Date(), ApplicationConstants.VALIDATION_FAILED, 
						ex.getBindingResult()
						.getFieldErrors()
						.stream()
						.map((field) -> new FieldValidationError(field.getField(), field.getCode(), field.getRejectedValue(),field.getDefaultMessage()))
						.collect(Collectors.toList()), request.getDescription(false));

		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}
	
	

}
