package org.example.credit_processing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "payment_registry")
public class PaymentRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_registry_id", nullable = false)
    @ToString.Exclude
    private ProductRegistry productRegistry;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "interest_rate_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal interestRateAmount;

    @Column(name = "debt_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal debtAmount;

    @Column(name = "expired", nullable = false)
    private Boolean expired;

    @Column(name = "payment_expiration_date", nullable = false)
    private LocalDateTime paymentExpirationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentRegistry that = (PaymentRegistry) o;

        if (id != null && that.id != null) {
            return Objects.equals(id, that.id);
        }

        Long productRegistryIdThis = productRegistry != null ? productRegistry.getId() : null;
        Long productRegistryIdThat = that.productRegistry != null ? that.productRegistry.getId() : null;
        return Objects.equals(productRegistryIdThis, productRegistryIdThat) &&
                Objects.equals(paymentDate, that.paymentDate) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(interestRateAmount, that.interestRateAmount) &&
                Objects.equals(debtAmount, that.debtAmount) &&
                Objects.equals(expired, that.expired);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        Long productRegistryIdVal = productRegistry != null ? productRegistry.getId() : null;
        return Objects.hash(productRegistryIdVal, paymentDate, amount,
                interestRateAmount, debtAmount, expired);
    }
}
