package com.tweetarchive.main.exceptions;

public class CollectionNotFoundException extends RuntimeException {
    public CollectionNotFoundException() {
        super("Collection not found");
    }
}