package com.daom.exception;

public class NoSuchCategoryException extends RuntimeException{
    public NoSuchCategoryException() {
        super();
    }

    public NoSuchCategoryException(String message) {
        super(message);
    }
}
