package com.shopsphere.cart.controller;

import com.shopsphere.cart.dto.CartItemResponse;
import com.shopsphere.cart.dto.CartResponse;
import com.shopsphere.cart.dto.CreateCartItemRequest;
import com.shopsphere.cart.dto.CreateCartRequest;
import com.shopsphere.cart.dto.UpdateCartItemRequest;
import com.shopsphere.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponse> createCart(
            @Valid @RequestBody CreateCartRequest request
    ) {

        CartResponse response =
                cartService.createCart(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartResponse> getCartById(
            @PathVariable Long cartId
    ) {

        return ResponseEntity.ok(
                cartService.getCartById(cartId)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CartResponse> getCartByUserId(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                cartService.getCartByUserId(userId)
        );
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItemResponse> addItemToCart(
            @PathVariable Long cartId,
            @Valid @RequestBody CreateCartItemRequest request
    ) {

        CartItemResponse response =
                cartService.addItemToCart(
                        cartId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{cartId}/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long cartId,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {

        return ResponseEntity.ok(
                cartService.updateCartItem(
                        cartId,
                        cartItemId,
                        request
                )
        );
    }

    @DeleteMapping("/{cartId}/items/{cartItemId}")
    public ResponseEntity<Void> removeCartItem(
            @PathVariable Long cartId,
            @PathVariable Long cartItemId
    ) {

        cartService.removeCartItem(
                cartId,
                cartItemId
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<Void> clearCart(
            @PathVariable Long cartId
    ) {

        cartService.clearCart(cartId);

        return ResponseEntity.noContent().build();
    }
}