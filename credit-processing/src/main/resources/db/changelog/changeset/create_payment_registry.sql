CREATE TABLE IF NOT EXISTS payment_registry (
    id BIGSERIAL PRIMARY KEY,
    product_registry_id BIGINT NOT NULL,
    payment_date DATE NOT NULL,
    amount NUMERIC(15,2) NOT NULL,
    interest_rate_amount NUMERIC(15,2) NOT NULL,
    debt_amount NUMERIC(15,2) NOT NULL,
    expired BOOLEAN NOT NULL DEFAULT FALSE,
    payment_expiration_date TIMESTAMP NOT NULL,

    CONSTRAINT fk_payment_registry_product_registry
        FOREIGN KEY (product_registry_id)
        REFERENCES product_registry(id)
);
