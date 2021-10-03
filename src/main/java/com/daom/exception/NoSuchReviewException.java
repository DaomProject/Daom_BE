package com.daom.exception;

public class NoSuchReviewException extends RuntimeException {
    public NoSuchReviewException() {
        super();
    }

    public NoSuchReviewException(String message) {
        super(message);
    }
}
