package com.shopsphere.inventory.exception;

public class InsufficientReservedStockException extends RuntimeException {
    public InsufficientReservedStockException(
            Long productId,
            Integer requestedQuantity,
            Integer reservedQuantity
    ) {
        super(
                "Insufficient stock for product: " + productId
                        + ". Requested: " + requestedQuantity
                        + ", Reserved: " + reservedQuantity
        );
    }
}
