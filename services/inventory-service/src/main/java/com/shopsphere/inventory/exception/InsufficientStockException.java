package com.shopsphere.inventory.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(
            Long productId,
            Integer requestedQuantity,
            Integer availableQuantity
    ) {
        super(
                "Insufficient stock for product: " + productId
                        + ". Requested: " + requestedQuantity
                        + ", Available: " + availableQuantity
        );
    }
}
