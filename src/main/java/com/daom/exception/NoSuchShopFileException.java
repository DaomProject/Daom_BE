package com.daom.exception;

public class NoSuchShopFileException extends RuntimeException{
    public NoSuchShopFileException() {
        super();
    }

    public NoSuchShopFileException(String message) {
        super(message);
    }
}
