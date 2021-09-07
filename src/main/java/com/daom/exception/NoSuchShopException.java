package com.daom.exception;

public class NoSuchShopException extends RuntimeException{
    public NoSuchShopException() {
        super();
    }

    public NoSuchShopException(String message) {
        super(message);
    }
}
