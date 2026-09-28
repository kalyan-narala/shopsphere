package com.shopsphere.cart.service;

import com.shopsphere.cart.dto.CartItemResponse;
import com.shopsphere.cart.dto.CartResponse;
import com.shopsphere.cart.dto.CreateCartItemRequest;
import com.shopsphere.cart.dto.CreateCartRequest;
import com.shopsphere.cart.dto.UpdateCartItemRequest;

public interface CartService {

    CartResponse createCart(CreateCartRequest request);

    CartResponse getCartById(Long cartId);

    CartResponse getCartByUserId(Long userId);

    CartItemResponse addItemToCart(
            Long cartId,
            CreateCartItemRequest request
    );

    CartResponse updateCartItem(
            Long cartId,
            Long cartItemId,
            UpdateCartItemRequest request
    );

    CartResponse removeCartItem(
            Long cartId,
            Long cartItemId
    );

    CartResponse clearCart(Long cartId);
}