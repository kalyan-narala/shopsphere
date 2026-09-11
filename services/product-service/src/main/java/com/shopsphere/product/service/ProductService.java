package com.shopsphere.product.service;

import com.shopsphere.product.dto.CreateProductRequest;
import com.shopsphere.product.dto.ProductResponse;
import com.shopsphere.product.dto.UpdateProductRequest;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse getProductById(Long productId);

    List<ProductResponse> getAllProducts();

    ProductResponse updateProduct(Long productId, UpdateProductRequest request);

    void deleteProduct(Long productId);
}