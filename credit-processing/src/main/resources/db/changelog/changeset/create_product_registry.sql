CREATE TABLE IF NOT EXISTS product_registry (
    id BIGSERIAL PRIMARY KEY,
    client_id VARCHAR(12) NOT NULL,
    account_id BIGINT NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    interest_rate NUMERIC(5,2) NOT NULL,
    month_count SMALLINT NOT NULL,
    amount NUMERIC(15,2) NOT NULL,
    open_date TIMESTAMP NOT NULL
);