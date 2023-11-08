package com.api.exception;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public  class ErrorDto {

    private final int status;
    private final String error;
    private final String message;
    private List<String> detailedMessages;

    public ErrorDto(HttpStatus httpStatus, String message) {
        status = httpStatus.value();
        error = httpStatus.getReasonPhrase();
        this.message = message;
    }

}

