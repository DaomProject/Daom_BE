package com.daom.exception;

public class NoSuchZzimException extends RuntimeException{
    public NoSuchZzimException() {
        super();
    }

    public NoSuchZzimException(String message) {
        super(message);
    }

    public NoSuchZzimException(String message, Throwable cause) {
        super(message, cause);
    }
}
