package org.example.account_processing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.example.account_processing.enums.Type;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @ToString.Exclude
    private Account account;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "is_credit", nullable = false)
    private Boolean isCredit;

    @Column(name = "payed_at")
    private LocalDateTime payedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment payment)) return false;
        if (id != null && payment.id != null) {
            return Objects.equals(id, payment.id);
        }
        return Objects.equals(account != null ? account.getId() : null,
                payment.account != null ? payment.account.getId() : null)
                && Objects.equals(paymentDate, payment.paymentDate)
                && Objects.equals(amount, payment.amount)
                && Objects.equals(type, payment.type);
    }

    @Override
    public final int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        Long accountId = account != null ? account.getId() : null;
        return Objects.hash(accountId, paymentDate, amount, type);
    }
}
