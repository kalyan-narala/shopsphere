package com.shopsphere.product.service;

import com.shopsphere.product.dto.CreateProductRequest;
import com.shopsphere.product.dto.ProductResponse;
import com.shopsphere.product.dto.UpdateProductRequest;
import com.shopsphere.product.entity.Product;
import com.shopsphere.product.mapper.ProductMapper;
import com.shopsphere.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.shopsphere.product.exception.ProductNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = productMapper.toEntity(request);
        product.setActive(true);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long productId) {
        Product product = findProductById(productId);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {
        Product product = findProductById(productId);
        productMapper.updateEntity(request, product);
        return productMapper.toResponse(product);
    }

    @Override
    public void deleteProduct(Long productId) {

        Product product = findProductById(productId);

        product.setActive(false);

    }
    private Product findProductById(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(productId));
    }
}
