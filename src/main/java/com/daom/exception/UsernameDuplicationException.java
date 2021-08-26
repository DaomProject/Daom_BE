package com.daom.exception;

public class UsernameDuplicationException extends RuntimeException {
    public UsernameDuplicationException() {
        super();
    }

    public UsernameDuplicationException(String message) {
        super(message);
    }
}
