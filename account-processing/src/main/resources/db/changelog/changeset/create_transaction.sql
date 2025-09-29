CREATE TABLE IF NOT EXISTS transactions (
  id BIGSERIAL PRIMARY KEY,
  account_id BIGINT NOT NULL,
  card_id BIGINT,
  type VARCHAR(50) NOT NULL,
  amount NUMERIC(15,2) NOT NULL,
  status VARCHAR(50) NOT NULL,
  timestamp TIMESTAMP NOT NULL,
  CONSTRAINT fk_transactions_account FOREIGN KEY (account_id) REFERENCES accounts(id),
  CONSTRAINT fk_transactions_card FOREIGN KEY (card_id) REFERENCES cards(id)
);