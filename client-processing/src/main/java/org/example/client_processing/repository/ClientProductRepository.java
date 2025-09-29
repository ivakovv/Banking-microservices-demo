package org.example.client_processing.repository;

import org.example.client_processing.enums.product.Key;
import org.example.client_processing.model.ClientProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientProductRepository extends JpaRepository<ClientProduct, Long> {
    
    List<ClientProduct> findByClientClientId(String clientId);
    
    List<ClientProduct> findByClientClientIdAndProductKey(String clientId, Key productKey);
    
    List<ClientProduct> findByClientClientIdAndStatus(String clientId, org.example.client_processing.enums.client_product.Status status);

    Optional<ClientProduct> findByClientClientIdAndProductProductId(String clientId, String productId);

    boolean existsByClientClientIdAndProductProductId(String clientId, String productId);

    boolean existsByProductProductId(String productId);
}
