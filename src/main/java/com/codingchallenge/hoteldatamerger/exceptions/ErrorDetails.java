package com.codingchallenge.hoteldatamerger.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorDetails {
    // Getters and Setters
    private String error;
    private String message;

    public ErrorDetails(String error, String message) {
        this.error = error;
        this.message = message;
    }
}

