package com.tweetarchive.main.exceptions;

import com.tweetarchive.main.model.enums.ErrorCode;

public class AlreadyLikedException extends RuntimeException {

    private final ErrorCode code;
    private final Object details;

    public AlreadyLikedException(ErrorCode code) {
        this(code, null);
    }

    public AlreadyLikedException(ErrorCode code, Object details) {
        this.code = code;
        this.details = details;
    }

    public ErrorCode getCode() {
        return code;
    }

    public Object getDetails() {
        return details;
    }
}
