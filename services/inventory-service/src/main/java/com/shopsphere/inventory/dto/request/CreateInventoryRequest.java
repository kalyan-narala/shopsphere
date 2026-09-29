package com.shopsphere.inventory.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateInventoryRequest {

    @NotNull
    private Long productId;

    @NotNull
    @PositiveOrZero
    private Integer availableQuantity;
}