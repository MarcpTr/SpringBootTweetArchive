package com.tweetarchive.main.exceptions;

import com.tweetarchive.main.model.enums.ErrorCode;

public class AuthenticationException  extends RuntimeException {
  private final ErrorCode code;
    private final Object details;

    public AuthenticationException(ErrorCode code) {
        this(code, null);
    }

    public AuthenticationException(ErrorCode code, Object details) {
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
