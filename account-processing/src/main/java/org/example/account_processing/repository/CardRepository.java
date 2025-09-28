package org.example.account_processing.repository;

import org.example.account_processing.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    
    boolean existsByCardId(String cardId);
    
    Optional<Card> findByCardId(String cardId);
}
