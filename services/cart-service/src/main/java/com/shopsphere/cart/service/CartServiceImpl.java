package com.shopsphere.cart.service;

import com.shopsphere.cart.client.ProductClient;
import com.shopsphere.cart.client.dto.ProductResponse;
import com.shopsphere.cart.dto.CartItemResponse;
import com.shopsphere.cart.dto.CartResponse;
import com.shopsphere.cart.dto.CreateCartItemRequest;
import com.shopsphere.cart.dto.CreateCartRequest;
import com.shopsphere.cart.dto.UpdateCartItemRequest;
import com.shopsphere.cart.entity.Cart;
import com.shopsphere.cart.entity.CartItem;
import com.shopsphere.cart.exception.CartNotFoundException;
import com.shopsphere.cart.exception.CartItemNotFoundException;
import com.shopsphere.cart.exception.ProductNotAvailableException;
import com.shopsphere.cart.mapper.CartMapper;
import com.shopsphere.cart.repository.CartItemRepository;
import com.shopsphere.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final ProductClient productClient;

    @Override
    public CartResponse createCart(CreateCartRequest request) {

        Cart cart = cartMapper.toEntity(request);

        Cart savedCart = cartRepository.save(cart);

        CartResponse response = cartMapper.toResponse(savedCart);
        response.setItems(new ArrayList<>());
        response.setTotalAmount(BigDecimal.ZERO);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartById(Long cartId) {

        Cart cart = getCartOrThrow(cartId);

        return buildCartResponse(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartByUserId(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new CartNotFoundException(
                                "Cart not found for user id: " + userId
                        )
                );

        return buildCartResponse(cart);
    }

    @Override
    public CartItemResponse addItemToCart(
            Long cartId,
            CreateCartItemRequest request
    ) {

        // Find Cart
        Cart cart = getCartOrThrow(cartId);

        // Get current product details from Product Service
        ProductResponse product =
                productClient.getProductById(
                        request.getProductId()
                );

        if (!product.isActive()) {
            throw new ProductNotAvailableException(
                    "Product is not available id: "
                            + request.getProductId()
            );
        }

        // Find existing Cart Item
        CartItem cartItem =
                cartItemRepository
                        .findByCartIdAndProductId(
                                cartId,
                                request.getProductId()
                        )
                        .map(existingCartItem -> {

                            // Increase quantity when product already exists
                            existingCartItem.setQuantity(
                                    existingCartItem.getQuantity()
                                            + request.getQuantity()
                            );

                            return existingCartItem;
                        })
                        .orElseGet(() ->

                                // Create new Cart Item
                                CartItem.builder()
                                        .cart(cart)
                                        .productId(
                                                request.getProductId()
                                        )
                                        .quantity(
                                                request.getQuantity()
                                        )
                                        .build()
                        );

        CartItem savedCartItem =
                cartItemRepository.save(cartItem);

        CartItemResponse response =
                cartMapper.toItemResponse(savedCartItem);

        response.setProductName(product.getName());
        response.setPrice(product.getPrice());

        BigDecimal subtotal =
                product.getPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        savedCartItem.getQuantity()
                                )
                        );

        response.setSubtotal(subtotal);

        return response;
    }

    @Override
    public CartResponse updateCartItem(
            Long cartId,
            Long cartItemId,
            UpdateCartItemRequest request
    ) {

        Cart cart = getCartOrThrow(cartId);

        CartItem cartItem =
                cartItemRepository
                        .findByIdAndCartId(
                                cartItemId,
                                cartId
                        )
                        .orElseThrow(() ->
                                new CartItemNotFoundException(
                                        "Cart item not found id: "
                                                + cartItemId
                                )
                        );

        cartMapper.updateEntity(request, cartItem);

        return buildCartResponse(cart);
    }

    @Override
    public CartResponse removeCartItem(
            Long cartId,
            Long cartItemId
    ) {

        Cart cart = getCartOrThrow(cartId);

        CartItem cartItem =
                cartItemRepository
                        .findByIdAndCartId(
                                cartItemId,
                                cartId
                        )
                        .orElseThrow(() ->
                                new CartItemNotFoundException(
                                        "Cart item not found id: "
                                                + cartItemId
                                )
                        );

        cart.getItems().remove(cartItem);

        return buildCartResponse(cart);
    }

    @Override
    public CartResponse clearCart(Long cartId) {

        Cart cart = getCartOrThrow(cartId);

        cart.getItems().clear();

        return buildCartResponse(cart);
    }

    private Cart getCartOrThrow(Long cartId) {

        return cartRepository.findById(cartId)
                .orElseThrow(() ->
                        new CartNotFoundException(
                                "Cart not found id: " + cartId
                        )
                );
    }

    private CartResponse buildCartResponse(Cart cart) {

        CartResponse response = cartMapper.toResponse(cart);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {

            ProductResponse product =
                    productClient.getProductById(
                            cartItem.getProductId()
                    );

            CartItemResponse itemResponse =
                    cartMapper.toItemResponse(cartItem);

            itemResponse.setProductName(product.getName());
            itemResponse.setPrice(product.getPrice());

            BigDecimal subtotal =
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            );

            itemResponse.setSubtotal(subtotal);

            response.getItems().add(itemResponse);

            totalAmount = totalAmount.add(subtotal);
        }

        response.setTotalAmount(totalAmount);

        return response;
    }
}