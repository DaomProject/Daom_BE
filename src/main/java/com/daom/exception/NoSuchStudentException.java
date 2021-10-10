package com.daom.exception;

public class NoSuchStudentException extends RuntimeException{
    public NoSuchStudentException() {
        super();
    }

    public NoSuchStudentException(String message) {
        super(message);
    }
}
