package com.tweetarchive.main.exceptions;

import java.util.Map;

public class NotLikedException extends RuntimeException {

    private final Map<String, String> errors;

    public NotLikedException(Map<String, String> errors) {
        super("Collection was not liked");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
