package com.benhines.productservice.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException() {
        super("The data requested does not exist.");
    }

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
