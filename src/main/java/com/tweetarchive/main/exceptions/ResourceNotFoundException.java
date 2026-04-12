package com.tweetarchive.main.exceptions;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

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
