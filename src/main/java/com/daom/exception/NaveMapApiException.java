package com.daom.exception;

import java.io.IOException;

public class NaveMapApiException extends RuntimeException {
    public NaveMapApiException() {
        super();
    }

    public NaveMapApiException(String message) {
        super(message);
    }

    public NaveMapApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public NaveMapApiException(Throwable cause) {
        super(cause);
    }
}
