package com.shopsphere.inventory.service;

import com.shopsphere.inventory.entity.Inventory;
import com.shopsphere.inventory.exception.InsufficientReservedStockException;
import com.shopsphere.inventory.exception.InsufficientStockException;
import com.shopsphere.inventory.exception.InvalidQuantityException;
import com.shopsphere.inventory.exception.InventoryNotFoundException;
import com.shopsphere.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Inventory inventory;

    @BeforeEach
    void setUp() {

        inventory = Inventory.builder()
                .id(1L)
                .productId(1L)
                .availableQuantity(100)
                .reservedQuantity(0)
                .version(0L)
                .build();
    }

    @Nested
    @DisplayName("Reserve Stock")
    class ReserveStockTests {

        @Test
        void shouldReserveStockSuccessfully() {

            // Arrange
            when(inventoryRepository.findByProductId(1L))
                    .thenReturn(Optional.of(inventory));

            // Act
            inventoryService.reserveStock(1L, 10);

            // Assert
            assertEquals(90, inventory.getAvailableQuantity());
            assertEquals(10, inventory.getReservedQuantity());

            verify(inventoryRepository)
                    .findByProductId(1L);
        }

        @Test
        void shouldThrowInventoryNotFoundExceptionWhenInventoryDoesNotExist() {

            // Arrange
            when(inventoryRepository.findByProductId(999L))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    InventoryNotFoundException.class,
                    () -> inventoryService.reserveStock(999L, 10)
            );

            verify(inventoryRepository)
                    .findByProductId(999L);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(ints = {0, -1, -5})
        void shouldThrowInvalidQuantityExceptionForNullOrNonPositiveQuantity(
                Integer quantity
        ) {

            // Arrange
            when(inventoryRepository.findByProductId(1L))
                    .thenReturn(Optional.of(inventory));

            // Act & Assert
            assertThrows(
                    InvalidQuantityException.class,
                    () -> inventoryService.reserveStock(1L, quantity)
            );
        }

        @Test
        void shouldThrowInsufficientStockExceptionWhenRequestedQuantityExceedsAvailableStock() {

            // Arrange
            when(inventoryRepository.findByProductId(1L))
                    .thenReturn(Optional.of(inventory));

            // Act & Assert
            assertThrows(
                    InsufficientStockException.class,
                    () -> inventoryService.reserveStock(1L, 150)
            );
        }

        @Test
        void shouldNotChangeInventoryWhenStockIsInsufficient() {

            // Arrange
            when(inventoryRepository.findByProductId(1L))
                    .thenReturn(Optional.of(inventory));

            // Act & Assert
            assertThrows(
                    InsufficientStockException.class,
                    () -> inventoryService.reserveStock(1L, 150)
            );

            assertEquals(100, inventory.getAvailableQuantity());
            assertEquals(0, inventory.getReservedQuantity());
        }

        @Test
        void shouldReserveAllAvailableStockWhenRequestedQuantityEqualsAvailableStock() {

            // Arrange
            when(inventoryRepository.findByProductId(1L))
                    .thenReturn(Optional.of(inventory));

            // Act
            inventoryService.reserveStock(1L, 100);

            // Assert
            assertEquals(0, inventory.getAvailableQuantity());
            assertEquals(100, inventory.getReservedQuantity());
        }

        @Test
        void shouldReserveStockWhenInventoryAlreadyHasReservedQuantity() {

            // Arrange
            inventory.setAvailableQuantity(70);
            inventory.setReservedQuantity(30);

            when(inventoryRepository.findByProductId(1L))
                    .thenReturn(Optional.of(inventory));

            // Act
            inventoryService.reserveStock(1L, 20);

            // Assert
            assertEquals(50, inventory.getAvailableQuantity());
            assertEquals(50, inventory.getReservedQuantity());
        }
    }

    @Nested
    @DisplayName("Release Stock")
    class ReleaseStockTests {

        @Test
        void shouldReleaseStockSuccessfully() {

            // Arrange
            inventory.setAvailableQuantity(70);
            inventory.setReservedQuantity(30);

            when(inventoryRepository.findByProductId(1L))
                    .thenReturn(Optional.of(inventory));

            // Act
            inventoryService.releaseStock(1L, 10);

            // Assert
            assertEquals(80, inventory.getAvailableQuantity());
            assertEquals(20, inventory.getReservedQuantity());

            verify(inventoryRepository)
                    .findByProductId(1L);
        }

        @Test
        void shouldThrowInventoryNotFoundExceptionWhenInventoryDoesNotExist() {

            // Arrange
            when(inventoryRepository.findByProductId(999L))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    InventoryNotFoundException.class,
                    () -> inventoryService.releaseStock(999L, 10)
            );

            verify(inventoryRepository)
                    .findByProductId(999L);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(ints = {0, -1, -5})
        void shouldThrowInvalidQuantityExceptionForNullOrNonPositiveQuantity(
                Integer quantity
        ) {

            // Arrange
            when(inventoryRepository.findByProductId(1L))
                    .thenReturn(Optional.of(inventory));

            // Act & Assert
            assertThrows(
                    InvalidQuantityException.class,
                    () -> inventoryService.releaseStock(1L, quantity)
            );
        }

        @Test
        void shouldThrowInsufficientReservedStockExceptionWhenReleaseQuantityExceedsReservedStock() {

            // Arrange
            inventory.setAvailableQuantity(70);
            inventory.setReservedQuantity(30);

            when(inventoryRepository.findByProductId(1L))
                    .thenReturn(Optional.of(inventory));

            // Act & Assert
            assertThrows(
                    InsufficientReservedStockException.class,
                    () -> inventoryService.releaseStock(1L, 40)
            );
        }
    }

    @Nested
    @DisplayName("Confirm Reservation")
    class ConfirmReservationTests {

        @Test
        void shouldConfirmReservationSuccessfully() {

            // Arrange
            inventory.setAvailableQuantity(70);
            inventory.setReservedQuantity(30);

            when(inventoryRepository.findByProductId(1L))
                    .thenReturn(Optional.of(inventory));

            // Act
            inventoryService.confirmReservation(1L, 10);

            // Assert
            assertEquals(70, inventory.getAvailableQuantity());
            assertEquals(20, inventory.getReservedQuantity());

            verify(inventoryRepository)
                    .findByProductId(1L);
        }

        @Test
        void shouldThrowInventoryNotFoundExceptionWhenInventoryDoesNotExist() {

            // Arrange
            when(inventoryRepository.findByProductId(999L))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    InventoryNotFoundException.class,
                    () -> inventoryService.confirmReservation(999L, 10)
            );

            verify(inventoryRepository)
                    .findByProductId(999L);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(ints = {0, -1, -5})
        void shouldThrowInvalidQuantityExceptionForNullOrNonPositiveQuantity(
                Integer quantity
        ) {

            // Arrange
            when(inventoryRepository.findByProductId(1L))
                    .thenReturn(Optional.of(inventory));

            // Act & Assert
            assertThrows(
                    InvalidQuantityException.class,
                    () -> inventoryService.confirmReservation(1L, quantity)
            );
        }

        @Test
        void shouldThrowInsufficientReservedStockExceptionWhenConfirmationQuantityExceedsReservedStock() {

            // Arrange
            inventory.setAvailableQuantity(70);
            inventory.setReservedQuantity(30);

            when(inventoryRepository.findByProductId(1L))
                    .thenReturn(Optional.of(inventory));

            // Act & Assert
            assertThrows(
                    InsufficientReservedStockException.class,
                    () -> inventoryService.confirmReservation(1L, 40)
            );
        }
    }
}