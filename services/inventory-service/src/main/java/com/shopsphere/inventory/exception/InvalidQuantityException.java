package com.shopsphere.inventory.exception;

public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException(Integer quantity) {
        super("Quantity must be greater than Zero: " + quantity);
    }
}
