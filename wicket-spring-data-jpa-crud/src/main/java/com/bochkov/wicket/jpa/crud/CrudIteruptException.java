package com.bochkov.wicket.jpa.crud;

public class CrudIteruptException extends RuntimeException {

    public CrudIteruptException() {
    }

    public CrudIteruptException(String message) {
        super(message);
    }

    public CrudIteruptException(String message, Throwable cause) {
        super(message, cause);
    }

    public CrudIteruptException(Throwable cause) {
        super(cause);
    }

    public CrudIteruptException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
