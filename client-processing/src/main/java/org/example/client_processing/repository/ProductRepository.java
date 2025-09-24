package org.example.client_processing.repository;

import org.example.client_processing.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByKey(org.example.client_processing.enums.product.Key key);
    boolean existsByProductId(String productId);
    Optional<Product> findByProductId(String productId);
}
