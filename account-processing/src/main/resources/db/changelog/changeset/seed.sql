-- Accounts
INSERT INTO accounts (client_id, product_id, balance, interest_rate, is_recalc, card_exist, status)
SELECT '770100000001', 'DC1', 0, NULL, FALSE, FALSE, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM accounts a WHERE a.client_id = '770100000001' AND a.product_id = 'DC1');
INSERT INTO accounts (client_id, product_id, balance, interest_rate, is_recalc, card_exist, status)
SELECT '770100000001', 'NS7', 0, NULL, FALSE, FALSE, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM accounts a WHERE a.client_id = '770100000001' AND a.product_id = 'NS7');
INSERT INTO accounts (client_id, product_id, balance, interest_rate, is_recalc, card_exist, status)
SELECT '770200000023', 'CC2', 0, NULL, FALSE, FALSE, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM accounts a WHERE a.client_id = '770200000023' AND a.product_id = 'CC2');
INSERT INTO accounts (client_id, product_id, balance, interest_rate, is_recalc, card_exist, status)
SELECT '780100000045', 'AC3', 0, 12.50, TRUE, FALSE, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM accounts a WHERE a.client_id = '780100000045' AND a.product_id = 'AC3');
INSERT INTO accounts (client_id, product_id, balance, interest_rate, is_recalc, card_exist, status)
SELECT '500100000007', 'PENS6', 0, NULL, FALSE, FALSE, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM accounts a WHERE a.client_id = '500100000007' AND a.product_id = 'PENS6');

-- Cards
INSERT INTO cards (account_id, card_id, payment_system, status)
SELECT a.id, '4111111111111111', 'VISA', 'OPENED'
FROM accounts a
WHERE a.client_id = '770100000001' AND a.product_id = 'DC1'
  AND NOT EXISTS (SELECT 1 FROM cards c2 WHERE c2.card_id='4111111111111111');

INSERT INTO cards (account_id, card_id, payment_system, status)
SELECT a.id, '5555444433331111', 'MIR', 'OPENED'
FROM accounts a
WHERE a.client_id = '770200000023' AND a.product_id = 'CC2'
  AND NOT EXISTS (SELECT 1 FROM cards c2 WHERE c2.card_id='5555444433331111');

-- Payments
INSERT INTO payments (account_id, payment_date, amount, is_credit, payed_at, type)
SELECT a.id, NOW(), 500.00, FALSE, NOW(), 'DEPOSIT'
FROM accounts a
WHERE NOT EXISTS (SELECT 1 FROM payments p WHERE p.account_id = a.id);

-- Transactions
INSERT INTO transactions (account_id, card_id, type, amount, status, timestamp)
SELECT a.id, c.id, 'DEPOSIT', 500.00, 'COMPLETE', NOW()
FROM accounts a
LEFT JOIN cards c ON c.account_id = a.id
WHERE NOT EXISTS (SELECT 1 FROM transactions t WHERE t.account_id = a.id);