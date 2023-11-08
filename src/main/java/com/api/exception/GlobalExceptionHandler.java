package com.api.exception;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalExceptionHandling(Exception exception, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    private ErrorDetails createErrorDetails(String message, WebRequest request) {
        return new ErrorDetails(new Date(), message, request.getDescription(false));
    }

    @ExceptionHandler({ ResourceNotFoundException.class })
    public ResponseEntity<?> handleNotFoundExceptions(Exception exception, WebRequest request) {
        ErrorDetails errorDetails = createErrorDetails(exception.getMessage(), request);
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<?> EmailAlreadyExistsException(EmailAlreadyExistsException exception, WebRequest request) {
        ErrorDetails errorDetails =
                new ErrorDetails(new Date(), exception.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> badCredentialsException(BadCredentialsException exception, WebRequest request){
    	ErrorDetails errorDetails = 
    			new ErrorDetails(new Date(), exception.getMessage(), request.getDescription(false));
    	return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

}
