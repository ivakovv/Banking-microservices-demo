CREATE TABLE IF NOT EXISTS cards (
  id BIGSERIAL PRIMARY KEY,
  account_id BIGINT NOT NULL,
  card_id VARCHAR(255) NOT NULL UNIQUE,
  payment_system VARCHAR(100) NOT NULL,
  status VARCHAR(50) NOT NULL,
  CONSTRAINT fk_cards_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);