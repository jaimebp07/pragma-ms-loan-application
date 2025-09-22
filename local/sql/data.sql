INSERT INTO credi_ya.loan_type (id, name, minimum_amount, maximum_amount, interest_rate, automatic_validation)
VALUES
('a8f5d1a4-6b1b-4f39-8fa4-2c4dbd38d13e', 'PERSONAL', 1000000, 5000000, 5.5, TRUE),
('b3f2c13d-29c5-4c5e-93f1-1c8a7a63d0e9', 'AUTO', 5000000, 100000000, 4.2, TRUE),
('c47b9182-bc7d-4b86-b3e1-08f3b7e5d2a4', 'STUDENT', 500000, 20000000, 3.8, TRUE),
('d1f27b71-9c46-4aa1-9f0e-3f4b1c693b90', 'BUSINESS', 10000000, 200000000, 6.0, FALSE),
('e73c9d29-2e6e-4dc0-bf07-41b77d43a1c2', 'MICROCREDIT', 100000, 5000000, 7.5, TRUE),
('f5a3d884-0fd8-4c1f-9de1-92bb8fa57ab3', 'HOUSING', 20000000, 500000000, 4.8, FALSE);

-- 4 solicitudes con estado APPROVED
INSERT INTO credi_ya.loan_applications
(id, client_id, amount, term, loan_type_id, status)
VALUES
(gen_random_uuid(), '9cf91033-f501-4abc-9ca8-6fe16757cc92', 1500000.00, 12, 'a8f5d1a4-6b1b-4f39-8fa4-2c4dbd38d13e', 'APPROVED'),
(gen_random_uuid(), '3c59f39e-0a32-4d07-9a0b-fcd038e2e802', 3000000.00, 24, 'b3f2c13d-29c5-4c5e-93f1-1c8a7a63d0e9', 'APPROVED'),
(gen_random_uuid(), '4e3f5b9b-8e5d-4a76-b394-2f1a6a3d5c7f', 8000000.00, 18, 'c47b9182-bc7d-4b86-b3e1-08f3b7e5d2a4', 'APPROVED'),
(gen_random_uuid(), 'da187d87-1dc1-40fd-a2f0-3ac91a8f80fb', 5000000.00, 36, 'd1f27b71-9c46-4aa1-9f0e-3f4b1c693b90', 'APPROVED');

-- 4 solicitudes con estado REJECTED
INSERT INTO credi_ya.loan_applications
(id, client_id, amount, term, loan_type_id, status)
VALUES
(gen_random_uuid(), '3c59f39e-0a32-4d07-9a0b-fcd038e2e802', 200000.00, 6,  'e73c9d29-2e6e-4dc0-bf07-41b77d43a1c2', 'REJECTED'),
(gen_random_uuid(), '4e3f5b9b-8e5d-4a76-b394-2f1a6a3d5c7f', 7000.00, 12, 'a8f5d1a4-6b1b-4f39-8fa4-2c4dbd38d13e', 'REJECTED'),
(gen_random_uuid(), '7b49d4a5-293a-4fcb-95f2-f0af59e196fd', 25000.00, 24, 'b3f2c13d-29c5-4c5e-93f1-1c8a7a63d0e9', 'REJECTED'),
(gen_random_uuid(), '0c6485b6-6b3a-4bdb-823e-0a8d71a3e7de', 120000000.00, 48, 'f5a3d884-0fd8-4c1f-9de1-92bb8fa57ab3', 'REJECTED');

-- 4 solicitudes con estado PENDING
INSERT INTO credi_ya.loan_applications
(id, client_id, amount, term, loan_type_id, status)
VALUES
(gen_random_uuid(), '9cf91033-f501-4abc-9ca8-6fe16757cc92', 2000000.00, 6,  'e73c9d29-2e6e-4dc0-bf07-41b77d43a1c2', 'PENDING'),
(gen_random_uuid(), '4e3f5b9b-8e5d-4a76-b394-2f1a6a3d5c7f', 5000.00, 12, 'c47b9182-bc7d-4b86-b3e1-08f3b7e5d2a4', 'PENDING'),
(gen_random_uuid(), '0c6485b6-6b3a-4bdb-823e-0a8d71a3e7de', 18000000.00, 24, 'b3f2c13d-29c5-4c5e-93f1-1c8a7a63d0e9', 'PENDING'),
(gen_random_uuid(), '3c59f39e-0a32-4d07-9a0b-fcd038e2e802', 45000.00, 36, 'a8f5d1a4-6b1b-4f39-8fa4-2c4dbd38d13e', 'PENDING');

INSERT INTO credi_ya.loan_applications
(id, client_id, amount, term, loan_type_id, status)
VALUES
(gen_random_uuid(), '7b49d4a5-293a-4fcb-95f2-f0af59e196fd',  8500000.00, 12, 'a8f5d1a4-6b1b-4f39-8fa4-2c4dbd38d13e', 'PENDING'),
(gen_random_uuid(), '93d75c8d-7414-4900-b5e5-ccf1c9e62a40', 12000000.00, 24, 'b3f2c13d-29c5-4c5e-93f1-1c8a7a63d0e9', 'PENDING'),
(gen_random_uuid(), 'a6fa4c1e-6be7-4a6b-9391-82e646d92d60',  2500000.00, 18, 'c47b9182-bc7d-4b86-b3e1-08f3b7e5d2a4', 'PENDING'),
(gen_random_uuid(), 'b84a9f5a-8d4b-4c94-8466-9cb71241b772',  6000000.00, 36, 'd1f27b71-9c46-4aa1-9f0e-3f4b1c693b90', 'PENDING'),
(gen_random_uuid(), 'c3d2a55e-12f8-4b3a-82c7-2c9d174f91ee',  1500000.00,  6, 'e73c9d29-2e6e-4dc0-bf07-41b77d43a1c2', 'PENDING'),
(gen_random_uuid(), 'da187d87-1dc1-40fd-a2f0-3ac91a8f80fb', 50000000.00, 48, 'f5a3d884-0fd8-4c1f-9de1-92bb8fa57ab3', 'PENDING'),
(gen_random_uuid(), 'f5a31f6e-cd3f-44f7-bf11-0dbad81b5b59',  3000000.00, 12, 'a8f5d1a4-6b1b-4f39-8fa4-2c4dbd38d13e', 'PENDING');
