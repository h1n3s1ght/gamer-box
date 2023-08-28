package com.benhines.inventoryservice.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("The data requested does not exist in our database.");
    }
    public NotFoundException(String message) {
        super(message);
    }
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
