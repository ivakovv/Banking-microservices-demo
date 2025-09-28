-- Users
INSERT INTO users (login, password, email)
SELECT 'ivan', '{bcrypt}$2a$10$QpKZf7mQnSxK8m2mXqUeau1m6oXbN2e1yYqz7aG9h9b2Zf8mS3fKe', 'ivan55@example.com'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE login='ivan');
INSERT INTO users (login, password, email)
SELECT 'petr', '{bcrypt}$2a$10$7s3g9dQHk8Lm2PqR1TzZ9u6u0xG2s7X3pQe8YvJ4fXoZr9Lq3cK6G', 'petr.007@example.com'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE login='petr');
INSERT INTO users (login, password, email)
SELECT 'sergey', '{bcrypt}$2a$10$K1mN5xC3vB8UzQ7Lh9JtHeu2rG4Wv6Yp8Sd3Qk6TzVh1Bn0Lm2yS6', 'sergey.rus@example.com'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE login='sergey');
INSERT INTO users (login, password, email)
SELECT 'anna', '{bcrypt}$2a$10$M9qL7nH5cV2bX8pR3tJwYeu4hL6Qo8Vx2Md5Nk7RzPf0As1Qe4iFa', 'anna@example.com'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE login='anna');
INSERT INTO users (login, password, email)
SELECT 'olga', '{bcrypt}$2a$10$B8fK2lP6qR9tW3yH7nJcXeF1gH5Jk8Lm2Nq4St6Vx9Zc1Op3Rr7Da', 'olga@example.com'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE login='olga');

-- Products
INSERT INTO products (name, key)
SELECT 'Debit Card', 'DC' WHERE NOT EXISTS (SELECT 1 FROM products WHERE key='DC');
INSERT INTO products (name, key)
SELECT 'Credit Card', 'CC' WHERE NOT EXISTS (SELECT 1 FROM products WHERE key='CC');
INSERT INTO products (name, key)
SELECT 'Auto Credit', 'AC' WHERE NOT EXISTS (SELECT 1 FROM products WHERE key='AC');
INSERT INTO products (name, key)
SELECT 'Mortgage', 'IPO' WHERE NOT EXISTS (SELECT 1 FROM products WHERE key='IPO');
INSERT INTO products (name, key)
SELECT 'Personal Credit', 'PC' WHERE NOT EXISTS (SELECT 1 FROM products WHERE key='PC');
INSERT INTO products (name, key)
SELECT 'Pension', 'PENS' WHERE NOT EXISTS (SELECT 1 FROM products WHERE key='PENS');
INSERT INTO products (name, key)
SELECT 'Savings', 'NS' WHERE NOT EXISTS (SELECT 1 FROM products WHERE key='NS');
INSERT INTO products (name, key)
SELECT 'Insurance', 'INS' WHERE NOT EXISTS (SELECT 1 FROM products WHERE key='INS');
INSERT INTO products (name, key)
SELECT 'Brokerage', 'BS' WHERE NOT EXISTS (SELECT 1 FROM products WHERE key='BS');

-- Clients
WITH u AS (SELECT id FROM users WHERE login='ivan')
INSERT INTO clients (client_id, user_id, first_name, middle_name, last_name, date_of_birth, document_type, document_id, document_prefix)
SELECT '770100000001', id, 'Иван','Иванович','Иванов', DATE '1990-01-01','PASSPORT','4010 123456','AB' FROM u
WHERE NOT EXISTS (SELECT 1 FROM clients WHERE client_id='770100000001');

WITH u AS (SELECT id FROM users WHERE login='petr')
INSERT INTO clients (client_id, user_id, first_name, middle_name, last_name, date_of_birth, document_type, document_id)
SELECT '770200000023', id, 'Пётр','Петрович','Петров', DATE '1988-05-05','PASSPORT','4011 654321' FROM u
WHERE NOT EXISTS (SELECT 1 FROM clients WHERE client_id='770200000023');

WITH u AS (SELECT id FROM users WHERE login='sergey')
INSERT INTO clients (client_id, user_id, first_name, middle_name, last_name, date_of_birth, document_type, document_id)
SELECT '780100000045', id, 'Сергей','Антонович','Кутузов', DATE '1992-03-10','INT_PASSPORT','70 112233' FROM u
WHERE NOT EXISTS (SELECT 1 FROM clients WHERE client_id='780100000045');

WITH u AS (SELECT id FROM users WHERE login='anna')
INSERT INTO clients (client_id, user_id, first_name, middle_name, last_name, date_of_birth, document_type, document_id)
SELECT '500100000007', id, 'Анна','Ивановна','Михайлова', DATE '1995-07-20','PASSPORT','4012 778899' FROM u
WHERE NOT EXISTS (SELECT 1 FROM clients WHERE client_id='500100000007');

WITH u AS (SELECT id FROM users WHERE login='olga')
INSERT INTO clients (client_id, user_id, first_name, middle_name, last_name, date_of_birth, document_type, document_id)
SELECT '770100000008', id, 'Ольга','Петровна','Кузнецова', DATE '1985-11-11','BIRTH_CERT','VIII-000111' FROM u
WHERE NOT EXISTS (SELECT 1 FROM clients WHERE client_id='770100000008');

-- ClientProduct
INSERT INTO client_product (client_id, product_id, open_date, status)
SELECT c.id, p.id, NOW(), 'ACTIVE'
FROM clients c, products p
WHERE c.client_id='770100000001' AND p.key IN ('DC','NS')
  AND NOT EXISTS (SELECT 1 FROM client_product cp WHERE cp.client_id=c.id AND cp.product_id=p.id AND cp.status='ACTIVE');

INSERT INTO client_product (client_id, product_id, open_date, status)
SELECT c.id, p.id, NOW(), 'ACTIVE'
FROM clients c, products p
WHERE c.client_id='770200000023' AND p.key='CC'
  AND NOT EXISTS (SELECT 1 FROM client_product cp WHERE cp.client_id=c.id AND cp.product_id=p.id AND cp.status='ACTIVE');

INSERT INTO client_product (client_id, product_id, open_date, status)
SELECT c.id, p.id, NOW(), 'ACTIVE'
FROM clients c, products p
WHERE c.client_id='780100000045' AND p.key='AC'
  AND NOT EXISTS (SELECT 1 FROM client_product cp WHERE cp.client_id=c.id AND cp.product_id=p.id AND cp.status='ACTIVE');

INSERT INTO client_product (client_id, product_id, open_date, status)
SELECT c.id, p.id, NOW(), 'ACTIVE'
FROM clients c, products p
WHERE c.client_id='500100000007' AND p.key='PENS'
  AND NOT EXISTS (SELECT 1 FROM client_product cp WHERE cp.client_id=c.id AND cp.product_id=p.id AND cp.status='ACTIVE');


