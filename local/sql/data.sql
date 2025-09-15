INSERT INTO credi_ya.loan_type (id, name, minimum_amount, maximum_amount, interest_rate, automatic_validation)
VALUES
('a8f5d1a4-6b1b-4f39-8fa4-2c4dbd38d13e', 'PERSONAL', 1000, 50000, 5.5, TRUE),
('b3f2c13d-29c5-4c5e-93f1-1c8a7a63d0e9', 'AUTO', 5000, 100000, 4.2, TRUE),
('c47b9182-bc7d-4b86-b3e1-08f3b7e5d2a4', 'STUDENT', 500, 20000, 3.8, TRUE),
('d1f27b71-9c46-4aa1-9f0e-3f4b1c693b90', 'BUSINESS', 10000, 200000, 6.0, FALSE),
('e73c9d29-2e6e-4dc0-bf07-41b77d43a1c2', 'MICROCREDIT', 100, 5000, 7.5, TRUE),
('f5a3d884-0fd8-4c1f-9de1-92bb8fa57ab3', 'HOUSING', 20000, 500000, 4.8, FALSE);


-- 4 solicitudes con estado APPROVED
INSERT INTO credi_ya.loan_applications
(id, client_id, amount, email, term, loan_type_id, status)
VALUES
(gen_random_uuid(), gen_random_uuid(), 15000.00, 12, 'a8f5d1a4-6b1b-4f39-8fa4-2c4dbd38d13e', 'APPROVED'),
(gen_random_uuid(), gen_random_uuid(), 30000.00, 24, 'b3f2c13d-29c5-4c5e-93f1-1c8a7a63d0e9', 'APPROVED'),
(gen_random_uuid(), gen_random_uuid(), 8000.00, 18, 'c47b9182-bc7d-4b86-b3e1-08f3b7e5d2a4', 'APPROVED'),
(gen_random_uuid(), gen_random_uuid(), 50000.00, 36, 'd1f27b71-9c46-4aa1-9f0e-3f4b1c693b90', 'APPROVED');

-- 4 solicitudes con estado REJECTED
INSERT INTO credi_ya.loan_applications
(id, client_id, amount, email, term, loan_type_id, status)
VALUES
(gen_random_uuid(), gen_random_uuid(), 2000.00, 6,  'e73c9d29-2e6e-4dc0-bf07-41b77d43a1c2', 'REJECTED'),
(gen_random_uuid(), gen_random_uuid(), 7000.00, 12, 'a8f5d1a4-6b1b-4f39-8fa4-2c4dbd38d13e', 'REJECTED'),
(gen_random_uuid(), gen_random_uuid(), 25000.00, 24, 'b3f2c13d-29c5-4c5e-93f1-1c8a7a63d0e9', 'REJECTED'),
(gen_random_uuid(), gen_random_uuid(), 120000.00, 48, 'f5a3d884-0fd8-4c1f-9de1-92bb8fa57ab3', 'REJECTED');

-- 4 solicitudes con estado PENDING
INSERT INTO credi_ya.loan_applications
(id, client_id, amount, email, term, loan_type_id, status)
VALUES
(gen_random_uuid(), gen_random_uuid(), 1000.00, 6,  'e73c9d29-2e6e-4dc0-bf07-41b77d43a1c2', 'PENDING'),
(gen_random_uuid(), gen_random_uuid(), 5000.00, 12, 'c47b9182-bc7d-4b86-b3e1-08f3b7e5d2a4', 'PENDING'),
(gen_random_uuid(), gen_random_uuid(), 18000.00, 24, 'b3f2c13d-29c5-4c5e-93f1-1c8a7a63d0e9', 'PENDING'),
(gen_random_uuid(), gen_random_uuid(), 45000.00, 36, 'a8f5d1a4-6b1b-4f39-8fa4-2c4dbd38d13e', 'PENDING');