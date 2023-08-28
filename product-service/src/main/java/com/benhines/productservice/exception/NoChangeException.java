package com.benhines.productservice.exception;

public class NoChangeException extends RuntimeException {
    public NoChangeException() {
        super("The data being updated is the same as the existing data.");
    }

    public NoChangeException(String message) {
        super(message);
    }

    public NoChangeException(String message, Throwable cause) {
        super(message, cause);
    }
}

