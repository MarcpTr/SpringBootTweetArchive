package com.tweetarchive.main.exceptions;

import java.util.Map;

import com.tweetarchive.main.model.enums.ErrorCode;

public class NotLikedException extends RuntimeException {

   private final ErrorCode code;
    private final Object details;

    public NotLikedException(ErrorCode code) {
        this(code, null);
    }

    public NotLikedException(ErrorCode code, Object details) {
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
