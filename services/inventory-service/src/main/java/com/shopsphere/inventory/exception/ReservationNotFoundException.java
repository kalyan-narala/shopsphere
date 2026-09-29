package com.shopsphere.inventory.exception;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(Long orderId, Long productId) {
        super("Stock reservation not found for order id: " + orderId
                + " and product id: " + productId);
    }
}
