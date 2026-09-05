package com.shopsphere.cart.repository;

import com.shopsphere.cart.entity.Cart;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<User> findByUserId(Long userid);
}
