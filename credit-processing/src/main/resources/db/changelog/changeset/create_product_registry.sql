CREATE TABLE IF NOT EXISTS product_registry (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    interest_rate NUMERIC(5,2) NOT NULL,
    open_date TIMESTAMP NOT NULL
);