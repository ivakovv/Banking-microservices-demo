CREATE TABLE IF NOT EXISTS payments (
  id BIGSERIAL PRIMARY KEY,
  account_id BIGINT NOT NULL,
  payment_date TIMESTAMP NOT NULL,
  amount NUMERIC(15,2) NOT NULL,
  is_credit BOOLEAN NOT NULL,
  payed_at TIMESTAMP,
  type VARCHAR(100) NOT NULL,
  CONSTRAINT fk_payments_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);