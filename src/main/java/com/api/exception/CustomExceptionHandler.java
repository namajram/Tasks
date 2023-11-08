package com.api.exception;
import java.util.Date;
import java.util.stream.Collectors;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;

@ControllerAdvice
@RestControllerAdvice
public class CustomExceptionHandler  {
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleException(MethodArgumentNotValidException ex) {

        ErrorDto dto = new ErrorDto(HttpStatus.BAD_REQUEST, "Validation error");

        dto.setDetailedMessages(ex.getBindingResult().getAllErrors().stream()
            .map(err -> err.unwrap(ConstraintViolation.class))
            .map(err -> String.format("'%s' %s", err.getPropertyPath(), err.getMessage()))
            .collect(Collectors.toList()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);

    }
   
	@ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException exception, WebRequest request) {
        String errorMessage = extractErrorMessage(exception.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(new Date(), errorMessage, request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    private String extractErrorMessage(String exceptionMessage) {
       
        int startIndex = exceptionMessage.indexOf("'");
        int endIndex = exceptionMessage.lastIndexOf("'");
        if (startIndex != -1 && endIndex != -1) {
            return exceptionMessage.substring(startIndex + 1, endIndex);
        }
        
        return exceptionMessage;
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> badCredentialsException(BadCredentialsException exception, WebRequest request){
    	ErrorDetails errorDetails = 
    			new ErrorDetails(new Date(), exception.getMessage(), request.getDescription(false));
    	return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }
}
