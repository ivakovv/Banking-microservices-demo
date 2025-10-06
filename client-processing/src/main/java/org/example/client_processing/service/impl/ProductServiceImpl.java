package org.example.client_processing.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.client_processing.annotation.Cached;
import org.example.client_processing.dto.product.ProductRequest;
import org.example.client_processing.dto.product.ProductResponse;
import org.example.client_processing.exception.NotFoundException;
import org.example.client_processing.mapper.ProductMapper;
import org.example.client_processing.model.Product;
import org.example.client_processing.repository.ProductRepository;
import org.example.client_processing.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    private final ProductRepository productRepository;

    @Transactional
    @Override
    public ProductResponse create(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Cached(description = "Cache product data by productId", ttlSeconds = 900)
    @Override
    public ProductResponse getByProductId(String productId) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product with productId " + productId + " not found"));
        return productMapper.toResponse(product);
    }

    @Cached(description = "Cache all products list", ttlSeconds = 180)
    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional
    @Override
    public ProductResponse update(String productId, ProductRequest request) {
        Product existingProduct = productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product with productId " + productId + " not found"));

        Product updatedProduct = productMapper.updateProduct(existingProduct, request);
        Product savedProduct = productRepository.save(updatedProduct);
        
        return productMapper.toResponse(savedProduct);
    }

    @Transactional
    @Override
    public void delete(String productId) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product with productId " + productId + " not found"));
        productRepository.delete(product);
    }

    @Cached(description = "Cache product entity by productId", ttlSeconds = 600)
    @Override
    public Product getProductByProductId(String productId) {
        return productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product with productId " + productId + " not found"));
    }

}