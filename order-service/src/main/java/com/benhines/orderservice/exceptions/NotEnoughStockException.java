package com.benhines.orderservice.exceptions;

public class NotEnoughStockException extends Throwable {
    public NotEnoughStockException() {
        super("No stock available for this product.");
    }

    public NotEnoughStockException(String message) {
        super(message);
    }

    public NotEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
