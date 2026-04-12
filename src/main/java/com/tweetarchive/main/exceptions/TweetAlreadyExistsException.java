package com.tweetarchive.main.exceptions;

public class TweetAlreadyExistsException extends RuntimeException {

    private final Long collectionId;

    public TweetAlreadyExistsException(Long collectionId) {
        this.collectionId = collectionId;
    }

    public Long getCollectionId() {
        return collectionId;
    }
}