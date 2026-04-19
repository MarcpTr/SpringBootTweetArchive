package com.tweetarchive.main.exceptions;

import java.util.Map;

public class ResourceNotFoundException extends RuntimeException {

    private final Map<String, String> errors;

    public ResourceNotFoundException(Map<String, String> errors) {
        super("Resource not found");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
