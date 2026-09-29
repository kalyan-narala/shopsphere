package com.shopsphere.inventory.controller;

import com.shopsphere.inventory.dto.request.CreateInventoryRequest;
import com.shopsphere.inventory.dto.request.StockReservationRequest;
import com.shopsphere.inventory.dto.request.UpdateStockRequest;
import com.shopsphere.inventory.dto.response.InventoryResponse;
import com.shopsphere.inventory.service.InventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(@Valid @RequestBody CreateInventoryRequest request){

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(inventoryService.createInventory(request));
    }

    @GetMapping("/{inventoryId}")
    public ResponseEntity<InventoryResponse> getInventoryById(@PathVariable @Positive Long inventoryId){

        return ResponseEntity.ok(inventoryService.getInventoryById(inventoryId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryResponse> getInventoryByProductId(@PathVariable @Positive Long productId){

        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponse>>getAllInventory(){

        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @PutMapping("/product/{productId}/stockupdate")
    public ResponseEntity<InventoryResponse> updateStock(@PathVariable @Positive Long productId,
                                                         @RequestBody UpdateStockRequest request){
        return ResponseEntity.ok(inventoryService.updateStock(productId,request));
    }

    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<Void> deleteInventory(@PathVariable @Positive Long inventoryId){

        inventoryService.deleteInventory(inventoryId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveStock(@Valid @RequestBody StockReservationRequest request){

        inventoryService.reserveStock(
                request.getOrderId(),
                request.getProductId(),
                request.getQuantity()
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/release")
    public ResponseEntity<Void> releaseStock(@Valid @RequestBody StockReservationRequest request){

        inventoryService.releaseStock(
                request.getOrderId(),
                request.getProductId(),
                request.getQuantity()
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmReservation(@Valid @RequestBody StockReservationRequest request){

        inventoryService.confirmReservation(
                request.getOrderId(),
                request.getProductId(),
                request.getQuantity()
        );

        return ResponseEntity.noContent().build();
    }
}
