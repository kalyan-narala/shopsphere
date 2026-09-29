package com.shopsphere.inventory.repository;

import com.shopsphere.inventory.entity.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {

    Optional<StockReservation> findByOrderIdAndProductId(Long orderId, Long productId);
}