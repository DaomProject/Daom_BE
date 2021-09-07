package com.daom.exception;

public class NotAuthorityThisJobException extends RuntimeException {
    public NotAuthorityThisJobException() {
        super();
    }

    public NotAuthorityThisJobException(String message) {
        super(message);
    }
}
