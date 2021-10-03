package com.daom.exception;

public class UnmatchPasswordException extends RuntimeException {
    public UnmatchPasswordException() {
        super();
    }

    public UnmatchPasswordException(String message) {
        super(message);
    }
}
