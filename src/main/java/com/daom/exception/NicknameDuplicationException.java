package com.daom.exception;

public class NicknameDuplicationException extends RuntimeException{
    public NicknameDuplicationException() {
        super();
    }

    public NicknameDuplicationException(String message) {
        super(message);
    }
}
