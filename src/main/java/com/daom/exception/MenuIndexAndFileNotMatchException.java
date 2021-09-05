package com.daom.exception;

public class MenuIndexAndFileNotMatchException extends RuntimeException {
    public MenuIndexAndFileNotMatchException() {
        super();
    }

    public MenuIndexAndFileNotMatchException(String message) {
        super(message);
    }
}
