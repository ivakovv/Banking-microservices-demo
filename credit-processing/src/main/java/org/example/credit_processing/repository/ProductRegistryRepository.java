package org.example.credit_processing.repository;

import org.example.credit_processing.model.ProductRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface ProductRegistryRepository extends JpaRepository<ProductRegistry, Long> {

    /**
     * Находит все активные кредитные продукты клиента
     */
    @Query("SELECT pr FROM ProductRegistry pr WHERE pr.clientId = :clientId")
    List<ProductRegistry> findByClientId(@Param("clientId") String clientId);

    /**
     * Рассчитывает общую сумму задолженности клиента
     */
    @Query("SELECT COALESCE(SUM(pr.amount), 0) FROM ProductRegistry pr WHERE pr.clientId = :clientId")
    BigDecimal calculateTotalDebtByClientId(@Param("clientId") String clientId);

    /**
     * Проверяет наличие просрочек у клиента
     */
    @Query("SELECT COUNT(pr) > 0 FROM ProductRegistry pr " +
           "JOIN pr.paymentRegistries pay " +
           "WHERE pr.clientId = :clientId AND pay.expired = true")
    boolean hasOverduePayments(@Param("clientId") String clientId);
}
