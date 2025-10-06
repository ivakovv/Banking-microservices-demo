package org.example.account_processing.repository;

import org.example.account_processing.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    long countByCardIdAndTimestampAfter(Long cardId, LocalDateTime timestamp);
}
