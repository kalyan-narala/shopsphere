package com.shopsphere.inventory.service;

import com.shopsphere.inventory.dto.request.CreateInventoryRequest;
import com.shopsphere.inventory.dto.request.UpdateStockRequest;
import com.shopsphere.inventory.dto.response.InventoryResponse;

import java.util.List;

public interface InventoryService {

    InventoryResponse createInventory(CreateInventoryRequest request);

    InventoryResponse getInventoryById(Long inventoryId);

    InventoryResponse getInventoryByProductId(Long productId);

    List<InventoryResponse> getAllInventory();

    InventoryResponse updateStock(Long productId, UpdateStockRequest request);

    void deleteInventory(Long inventoryId);

    boolean isStockAvailable(Long productId, Integer quantity);

    void reserveStock(Long orderId, Long productId, Integer quantity);

    void releaseStock(Long orderId, Long productId, Integer quantity);

    void confirmReservation(Long orderId, Long productId, Integer quantity);
}