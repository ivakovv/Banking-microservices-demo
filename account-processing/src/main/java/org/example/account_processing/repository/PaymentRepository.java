package org.example.account_processing.repository;

import org.example.account_processing.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByAccountIdAndPayedAtIsNull(Long accountId);

    @Modifying
    @Query("UPDATE Payment p SET p.payedAt = :payedAt WHERE p.account.id = :accountId AND p.payedAt IS NULL")
    int updatePayedAtForAccount(@Param("accountId") Long accountId, @Param("payedAt") LocalDateTime payedAt);

    @Query("SELECT p FROM Payment p WHERE p.account.id = :accountId AND p.paymentDate <= :currentDate AND p.payedAt IS NULL AND p.expired = false")
    List<Payment> findDuePaymentsByAccountId(@Param("accountId") Long accountId, @Param("currentDate") LocalDateTime currentDate);
}
