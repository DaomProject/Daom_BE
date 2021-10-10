package com.daom.exception;

public class NotStudentException extends RuntimeException {
    public NotStudentException() {
        super();
    }

    public NotStudentException(String message) {
        super(message);
    }
}
