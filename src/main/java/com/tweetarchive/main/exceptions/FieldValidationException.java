package com.tweetarchive.main.exceptions;

import java.util.Map;

public class FieldValidationException extends RuntimeException {


    public FieldValidationException() {
        super("Validation failed");
    }

}
