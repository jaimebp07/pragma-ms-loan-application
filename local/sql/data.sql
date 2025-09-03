INSERT INTO credi_ya.loan_applications (id, client_id, amount, term, loan_type, status)
VALUES
    (gen_random_uuid(), 'client-001', 5000000.00, 24, 'PERSONAL', 'PENDING'),
    (gen_random_uuid(), 'client-002', 12000000.00, 36, 'STUDENT', 'PENDING');
