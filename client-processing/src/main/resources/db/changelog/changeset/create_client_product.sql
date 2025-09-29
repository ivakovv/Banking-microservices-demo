CREATE TABLE IF NOT EXISTS client_product (
  id BIGSERIAL PRIMARY KEY,
  client_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  open_date TIMESTAMP NOT NULL,
  close_date TIMESTAMP,
  status VARCHAR(50) NOT NULL,
  CONSTRAINT fk_clientproduct_client FOREIGN KEY (client_id) REFERENCES clients(id),
  CONSTRAINT fk_clientproduct_product FOREIGN KEY (product_id) REFERENCES products(id)
);