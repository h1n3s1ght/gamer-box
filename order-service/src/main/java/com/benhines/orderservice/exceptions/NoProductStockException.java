package com.benhines.orderservice.exceptions;

public class NoProductStockException  extends RuntimeException {
    public NoProductStockException() {
        super("No stock available for this product.");
    }

    public NoProductStockException(String message) {
        super(message);
    }

    public NoProductStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
