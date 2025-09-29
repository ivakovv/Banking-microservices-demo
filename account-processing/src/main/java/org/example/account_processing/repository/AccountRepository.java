package org.example.account_processing.repository;

import org.example.account_processing.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    Account findByClientIdAndProductId(String clientId, String productId);
}
