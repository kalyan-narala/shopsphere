package com.shopsphere.cart.mapper;

import com.shopsphere.cart.dto.CartItemResponse;
import com.shopsphere.cart.dto.CartResponse;
import com.shopsphere.cart.dto.CreateCartItemRequest;
import com.shopsphere.cart.dto.CreateCartRequest;
import com.shopsphere.cart.dto.UpdateCartItemRequest;
import com.shopsphere.cart.entity.Cart;
import com.shopsphere.cart.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CartMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Cart toEntity(CreateCartRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", ignore = true)
    CartItem toEntity(CreateCartItemRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "productId", ignore = true)
    void updateEntity(
            UpdateCartItemRequest request,
            @MappingTarget CartItem cartItem
    );

    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    CartItemResponse toItemResponse(CartItem cartItem);


    @Mapping(target = "items", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    CartResponse toResponse(Cart cart);
}