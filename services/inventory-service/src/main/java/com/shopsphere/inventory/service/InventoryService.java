package com.shopsphere.inventory.service;

public interface InventoryService {

    void reserveStock(Long productId, Integer quantity);

    void releaseStock(Long productId, Integer quantity);

    void confirmReservation(Long productId, Integer quantity);
}


