package com.tweetarchive.main.model.DTO;

public record ApiError<T> (
    String code,
    String message,
    T details
){}
