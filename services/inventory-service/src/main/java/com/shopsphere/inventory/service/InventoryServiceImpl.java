package com.shopsphere.inventory.service;

import com.shopsphere.inventory.entity.Inventory;
import com.shopsphere.inventory.exception.InsufficientReservedStockException;
import com.shopsphere.inventory.exception.InsufficientStockException;
import com.shopsphere.inventory.exception.InvalidQuantityException;
import com.shopsphere.inventory.exception.InventoryNotFoundException;
import com.shopsphere.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService{

    private final InventoryRepository inventoryRepository;

    @Override
    public void reserveStock(Long productId, Integer quantity) {

        Inventory inventory = getInventory(productId);

        validateQuantity(quantity);

        if (quantity > inventory.getAvailableQuantity()){
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
    }

    @Override
    public void releaseStock(Long productId, Integer quantity) {
        Inventory inventory = getInventory(productId);

        validateQuantity(quantity);

        if (quantity > inventory.getReservedQuantity()){
            throw new InsufficientReservedStockException(
                    productId,
                    quantity,
                    inventory.getReservedQuantity()
            );
        }

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity() + quantity
        );

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() - quantity
        );
    }

    @Override
    public void confirmReservation(Long productId, Integer quantity) {

        Inventory inventory = getInventory(productId);

        validateQuantity(quantity);

        if (quantity > inventory.getReservedQuantity()){
            throw new InsufficientReservedStockException(
                    productId,
                    quantity,
                    inventory.getReservedQuantity()
            );
        }

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() - quantity
        );

    }


    private Inventory getInventory(Long productId){
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(productId));
    }

    private void validateQuantity(Integer quantity){

        if (quantity == null || quantity<=0){
            throw new InvalidQuantityException(quantity);
        }
    }
}
