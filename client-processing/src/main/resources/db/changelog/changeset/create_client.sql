CREATE TABLE IF NOT EXISTS clients (
  id BIGSERIAL PRIMARY KEY,
  client_id VARCHAR(12) NOT NULL UNIQUE,
  user_id BIGINT NOT NULL,
  first_name VARCHAR(255) NOT NULL,
  middle_name VARCHAR(255),
  last_name VARCHAR(255) NOT NULL,
  date_of_birth DATE NOT NULL,
  document_type VARCHAR(50) NOT NULL,
  document_id VARCHAR(255) NOT NULL,
  document_prefix VARCHAR(50),
  document_suffix VARCHAR(50),
  CONSTRAINT fk_clients_user FOREIGN KEY (user_id) REFERENCES users(id)
);

ALTER TABLE clients
  ADD CONSTRAINT chk_clients_client_id_format
  CHECK (client_id ~ '^[0-9]{12}$');
