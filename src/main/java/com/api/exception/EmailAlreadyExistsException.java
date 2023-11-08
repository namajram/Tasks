package com.api.exception;

public class EmailAlreadyExistsException extends RuntimeException{
	 public EmailAlreadyExistsException(String message) {
	        super(message);
	    }
	 public EmailAlreadyExistsException() {
	        
	    }
}
