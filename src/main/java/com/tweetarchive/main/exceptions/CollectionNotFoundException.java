package com.tweetarchive.main.exceptions;

public class CollectionNotFoundException extends RuntimeException {
    public CollectionNotFoundException(String message) {
        super(message);
    }
}
