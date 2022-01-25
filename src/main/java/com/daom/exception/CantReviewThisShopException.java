package com.daom.exception;

public class CantReviewThisShopException extends RuntimeException {
    public CantReviewThisShopException() {
        super();
    }

    public CantReviewThisShopException(String message) {
        super(message);
    }

    public CantReviewThisShopException(String message, Throwable cause) {
        super(message, cause);
    }
}
