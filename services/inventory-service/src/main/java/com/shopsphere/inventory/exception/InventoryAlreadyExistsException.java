package com.shopsphere.inventory.exception;

public class InventoryAlreadyExistsException extends RuntimeException {
    public InventoryAlreadyExistsException(Long productId) {
        super("Inventory already exists with id: " + productId);
    }
}
