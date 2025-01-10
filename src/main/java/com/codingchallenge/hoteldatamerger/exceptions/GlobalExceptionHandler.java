package com.codingchallenge.hoteldatamerger.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle Resource Not Found Exception (404)
    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResponseStatusException ex, WebRequest request) {
        return new ResponseEntity<>(new ErrorDetails("Resource not found", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    // Handle Internal Server Error (500)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorDetails> handleInternalServerError(Exception ex, WebRequest request) {
        return new ResponseEntity<>(new ErrorDetails("Internal server error", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

