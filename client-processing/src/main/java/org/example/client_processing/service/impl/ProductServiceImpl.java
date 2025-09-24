package org.example.client_processing.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.client_processing.dto.ProductRequest;
import org.example.client_processing.dto.ProductResponse;
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

    @Override
    public ProductResponse getByProductId(String productId) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product with productId " + productId + " not found"));
        return productMapper.toResponse(product);
    }

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }

    //TODO сделать обновления продукта
    @Transactional
    @Override
    public ProductResponse update(String productId, ProductRequest request) {
        return null;
    }

    @Transactional
    @Override
    public void delete(String productId) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product with productId " + productId + " not found"));
        productRepository.delete(product);
    }

}