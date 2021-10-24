package com.daom.exception;

public class NoSuchLikeException extends RuntimeException{
    public NoSuchLikeException() {
        super();
    }

    public NoSuchLikeException(String message) {
        super(message);
    }

    public NoSuchLikeException(String message, Throwable cause) {
        super(message, cause);
    }
}
