package com.tweetarchive.main.exceptions;

public class InvalidCredentialsException  extends RuntimeException {
    public InvalidCredentialsException (){
        super("Invalid credentials");
    }
}
