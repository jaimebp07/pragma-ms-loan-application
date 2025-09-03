-- Crear esquema si no existe
CREATE SCHEMA IF NOT EXISTS credi_ya;

-- Crear tabla loan_aplications
CREATE TABLE IF NOT EXISTS credi_ya.loan_applications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id VARCHAR(100) NOT NULL,
    amount NUMERIC(15,2) NOT NULL,
    term INT NOT NULL,
    loan_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
