package com.shopsphere.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}