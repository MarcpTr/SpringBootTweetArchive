package com.tweetarchive.main.exceptions;

import java.util.Map;

public class AlreadyLikedException extends RuntimeException {

    private final Map<String, String> errors;

    public AlreadyLikedException(Map<String, String> errors) {
        super("Collection already liked");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
