package org.example.client_processing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
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
import org.example.client_processing.enums.client_product.Status;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "client_product")
public class ClientProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, foreignKey = @ForeignKey(name = "fk_clientproduct_client"))
    @ToString.Exclude
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_clientproduct_product"))
    @ToString.Exclude
    private Product product;

    @Column(name = "open_date", nullable = false)
    private LocalDateTime openDate;

    @Column(name = "close_date")
    private LocalDateTime closeDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientProduct that = (ClientProduct) o;

        if (id != null && that.id != null) {
            return Objects.equals(id, that.id);
        }

        Long clientIdThis = client != null ? client.getId() : null;
        Long clientIdThat = that.client != null ? that.client.getId() : null;
        Long productIdThis = product != null ? product.getId() : null;
        Long productIdThat = that.product != null ? that.product.getId() : null;
        return Objects.equals(clientIdThis, clientIdThat) &&
                Objects.equals(productIdThis, productIdThat) &&
                Objects.equals(openDate, that.openDate) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        Long clientIdVal = client != null ? client.getId() : null;
        Long productIdVal = product != null ? product.getId() : null;
        return Objects.hash(clientIdVal, productIdVal, openDate, status);
    }
}
