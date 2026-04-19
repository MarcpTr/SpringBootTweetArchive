package com.tweetarchive.main.exceptions;

public class FieldValidationException extends RuntimeException {

    public FieldValidationException() {
        super("Validation failed");
    }
}
