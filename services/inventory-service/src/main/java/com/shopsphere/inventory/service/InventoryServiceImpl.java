package com.shopsphere.inventory.service;

import com.shopsphere.inventory.client.ProductClient;
import com.shopsphere.inventory.dto.client.ProductResponse;
import com.shopsphere.inventory.dto.request.CreateInventoryRequest;
import com.shopsphere.inventory.dto.response.InventoryResponse;
import com.shopsphere.inventory.dto.request.UpdateStockRequest;
import com.shopsphere.inventory.entity.Inventory;
import com.shopsphere.inventory.entity.StockReservation;
import com.shopsphere.inventory.enums.ReservationStatus;
import com.shopsphere.inventory.exception.*;
import com.shopsphere.inventory.mapper.InventoryMapper;
import com.shopsphere.inventory.repository.InventoryRepository;
import com.shopsphere.inventory.repository.StockReservationRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StockReservationRepository stockReservationRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductClient productClient;


    @Override
    public InventoryResponse createInventory(CreateInventoryRequest request) {

        getProductOrThrow(request.getProductId());

        if (inventoryRepository.existsByProductId(request.getProductId())) {
            throw new InventoryAlreadyExistsException(request.getProductId());
        }

        Inventory inventory = inventoryMapper.toEntity(request);
        Inventory savedInventory = inventoryRepository.save(inventory);

        return buildInventoryResponse(savedInventory);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryById(Long inventoryId) {
        Inventory inventory = getInventoryOrThrow(inventoryId);
        return buildInventoryResponse(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryByProductId(Long productId) {

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(productId));
        return buildInventoryResponse(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAll()
                .stream()
                .map(this::buildInventoryResponse)
                .toList();
    }

    @Override
    public InventoryResponse updateStock(Long productId, UpdateStockRequest request) {

        Inventory inventory = inventoryRepository.findByProductIdForUpdate(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(productId));
        inventory.setAvailableQuantity(request.getQuantity());
        Inventory savedInventory = inventoryRepository.save(inventory);
        return buildInventoryResponse(savedInventory);
    }

    @Override
    public void deleteInventory(Long inventoryId) {

        Inventory inventory = getInventoryOrThrow(inventoryId);
        inventoryRepository.delete(inventory);
    }

    @Override
    public boolean isStockAvailable(Long productId, Integer quantity) {

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(productId));
        return inventory.getAvailableQuantity() >= quantity;
    }

    @Override
    public void reserveStock(Long orderId, Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductIdForUpdate(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(productId));

        if (quantity > inventory.getAvailableQuantity()) {
            throw new InsufficientStockException(
                    productId,
                    quantity,
                    inventory.getAvailableQuantity()
            );
        }

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity() - quantity
        );

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() + quantity
        );

        StockReservation reservation = StockReservation.builder()
                .orderId(orderId)
                .productId(productId)
                .quantity(quantity)
                .status(ReservationStatus.RESERVED)
                .build();

        stockReservationRepository.save(reservation);

        inventoryRepository.save(inventory);
    }



    @Override
    public void releaseStock(Long orderId, Long productId, Integer quantity) {

        Inventory inventory = inventoryRepository.findByProductIdForUpdate(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(productId));

        StockReservation reservation = stockReservationRepository.findByOrderIdAndProductId(orderId,productId)
                .orElseThrow(() ->
                        new ReservationNotFoundException(orderId,productId));

        if (reservation.getStatus() != ReservationStatus.RESERVED){
            throw new InvalidReservationStateException(
                    "Stock reservation cannot be released. Current status: "
                            + reservation.getStatus()
            );
        }

        if (!reservation.getQuantity().equals(quantity)){
            throw new InsufficientReservedStockException(
                    productId,
                    quantity,
                    inventory.getReservedQuantity()
            );
        }

        int reservedQuantity = reservation.getQuantity();

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + reservedQuantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);

        reservation.setStatus(ReservationStatus.RELEASED);

        stockReservationRepository.save(reservation);
        inventoryRepository.save(inventory);
    }

    @Override
    public void confirmReservation(Long orderId, Long productId, Integer quantity) {

        Inventory inventory = inventoryRepository.findByProductIdForUpdate(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(productId));

        StockReservation reservation = stockReservationRepository.findByOrderIdAndProductId(orderId,productId)
                .orElseThrow(() ->
                        new ReservationNotFoundException(orderId,productId));

        if (reservation.getStatus() != ReservationStatus.RESERVED){
            throw new InvalidReservationStateException(
                    "Stock reservation cannot be released. Current status" +
                            ": "
                            + reservation.getStatus()
            );
        }



        if (!reservation.getQuantity().equals(quantity)){
            throw new InsufficientReservedStockException(
                    productId,
                    quantity,
                    inventory.getReservedQuantity()
            );
        }

        int reservedQuantity = reservation.getQuantity();

        if (inventory.getReservedQuantity() < reservedQuantity) {
            throw new InvalidReservationStateException(
                    "Reserved stock is insufficient for confirmation"
            );
        }
        inventory.setReservedQuantity(inventory.getReservedQuantity() - reservedQuantity);

        reservation.setStatus(ReservationStatus.CONFIRMED);

        stockReservationRepository.save(reservation);
        inventoryRepository.save(inventory);

    }

    private ProductResponse getProductOrThrow(Long productId){
       try {
           return productClient.getProductById(productId);
       }
       catch (FeignException.NotFound exception){
           throw new ProductNotFoundException(productId);
       }
    }

    private Inventory getInventoryOrThrow(Long inventoryId){
        return inventoryRepository.findById(inventoryId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(inventoryId));

    }

    private InventoryResponse buildInventoryResponse(Inventory inventory){

        InventoryResponse response = inventoryMapper.toResponse(inventory);

        int totalQuantity = inventory.getAvailableQuantity() + inventory.getReservedQuantity();
        String stockStatus = inventory.getAvailableQuantity() > 0 ? "IN_STOCK" : "OUT_OF_STOCK";

        response.setTotalQuantity(totalQuantity);
        response.setStockStatus(stockStatus);

        return response;
    }
}
