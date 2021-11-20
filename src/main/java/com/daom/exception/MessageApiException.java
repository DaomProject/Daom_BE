package com.daom.exception;

public class MessageApiException extends RuntimeException{
    public MessageApiException() {
        super();
    }

    public MessageApiException(String message) {
        super(message);
    }
}
