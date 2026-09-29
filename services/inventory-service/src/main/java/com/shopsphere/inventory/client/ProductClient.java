package com.shopsphere.inventory.client;

import com.shopsphere.inventory.dto.client.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service",
        url = "${services.product-service.base-url}"
)
public interface ProductClient {

    @GetMapping("/api/v1/products/{productId}")
    ProductResponse getProductById(@PathVariable Long productId);
}
