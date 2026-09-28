package com.shopsphere.cart.repository;

import com.shopsphere.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndProductId(
            Long cartId,
            Long productId
    );

    Optional<CartItem> findByIdAndCartId(
            Long cartItemId,
            Long cartId
    );

    void deleteByCartId(Long cartId);
}