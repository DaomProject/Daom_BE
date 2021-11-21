package com.daom.exception;

public class MailDuplicationException extends RuntimeException{
    public MailDuplicationException() {
        super();
    }

    public MailDuplicationException(String message) {
        super(message);
    }
}
