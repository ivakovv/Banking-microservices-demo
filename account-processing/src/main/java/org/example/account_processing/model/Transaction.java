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
import org.example.account_processing.enums.transaction.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @ToString.Exclude
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    @ToString.Exclude
    private Card card;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ALLOWED;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction transaction)) return false;
        if (id != null && transaction.id != null) {
            return Objects.equals(id, transaction.id);
        }
        Long accountIdThis = account != null ? account.getId() : null;
        Long accountIdThat = transaction.account != null ? transaction.account.getId() : null;
        Long cardIdThis = card != null ? card.getId() : null;
        Long cardIdThat = transaction.card != null ? transaction.card.getId() : null;
        return Objects.equals(accountIdThis, accountIdThat)
                && Objects.equals(cardIdThis, cardIdThat)
                && Objects.equals(type, transaction.type)
                && Objects.equals(amount, transaction.amount)
                && Objects.equals(timestamp, transaction.timestamp);
    }

    @Override
    public final int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        Long accountIdVal = account != null ? account.getId() : null;
        Long cardIdVal = card != null ? card.getId() : null;
        return Objects.hash(accountIdVal, cardIdVal, type, amount, timestamp);
    }
}
