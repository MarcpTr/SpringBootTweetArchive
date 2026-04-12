package com.tweetarchive.main.exceptions;

public class UserAlreadyExistsException extends RuntimeException {

    private final String field;

    public UserAlreadyExistsException(String field, String value) {
        super(field + " already exists: " + value);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}