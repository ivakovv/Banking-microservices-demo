package org.example.account_processing.model;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.example.account_processing.enums.Status;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @ToString.Exclude
    private Account account;

    @Column(name = "card_id", unique = true, nullable = false)
    private String cardId;

    @Column(name = "payment_system", nullable = false)
    private String paymentSystem;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.OPENED;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Transaction> transactions = new HashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card card)) return false;
        if (id != null && card.id != null) {
            return Objects.equals(id, card.id);
        }
        return Objects.equals(cardId, card.cardId);
    }

    @Override
    public final int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(cardId);
    }
}
