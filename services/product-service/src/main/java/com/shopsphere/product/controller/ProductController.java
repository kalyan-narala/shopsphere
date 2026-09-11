package com.shopsphere.product.controller;

import com.shopsphere.product.dto.CreateProductRequest;
import com.shopsphere.product.dto.ProductResponse;
import com.shopsphere.product.dto.UpdateProductRequest;
import com.shopsphere.product.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid
            @RequestBody CreateProductRequest request){
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable
            @Positive(message = "Product id must be positive")
            Long productId

    ){
        ProductResponse response = productService.getProductById(productId);

        return ResponseEntity
                .ok(response);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateEntity(
            @PathVariable
            @Positive(message = "Product id must be Positive")
            Long productId,
            @Valid
            @RequestBody UpdateProductRequest request
            ){
        ProductResponse response = productService.updateProduct(productId, request);

        return ResponseEntity
                .ok(response);
    }

    @DeleteMapping("{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable
            @Positive(message = "Product id must be positive")
            Long productId
    ){
        productService.deleteProduct(productId);

        return ResponseEntity
                .noContent()
                .build();
    }
}
