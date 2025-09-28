-- ProductRegistry
INSERT INTO product_registry (client_id, account_id, product_id, interest_rate, month_count, amount, open_date)
SELECT '770100000001', 1, 'DC1', 12.50, 12, 100000.00, NOW()
WHERE NOT EXISTS (SELECT 1 FROM product_registry pr WHERE pr.client_id='770100000001' AND pr.product_id='DC1');

INSERT INTO product_registry (client_id, account_id, product_id, interest_rate, month_count, amount, open_date)
SELECT '770200000023', 2, 'CC2', 29.90, 24, 50000.00, NOW()
WHERE NOT EXISTS (SELECT 1 FROM product_registry pr WHERE pr.client_id='770200000023' AND pr.product_id='CC2');

-- PaymentRegistry
INSERT INTO payment_registry (product_registry_id, payment_date, amount, interest_rate_amount, debt_amount, expired, payment_expiration_date)
SELECT pr.id, CURRENT_DATE + INTERVAL '30 day', 10000.00, 120.00, 9880.00, FALSE, NOW() + INTERVAL '30 day'
FROM product_registry pr
WHERE pr.client_id IN ('770100000001', '770200000023')
  AND NOT EXISTS (SELECT 1 FROM payment_registry p WHERE p.product_registry_id = pr.id);