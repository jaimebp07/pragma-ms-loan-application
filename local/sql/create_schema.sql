CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Crear esquema si no existe
CREATE SCHEMA IF NOT EXISTS credi_ya;

-- Tabla loan_type (sin cambios relevantes)
CREATE TABLE IF NOT EXISTS credi_ya.loan_type (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    minimum_amount NUMERIC(15,2) NOT NULL,
    maximum_amount NUMERIC(15,2) NOT NULL,
    interest_rate NUMERIC(5,2) NOT NULL,
    automatic_validation BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Tabla loan_applications con foreign key
CREATE TABLE IF NOT EXISTS credi_ya.loan_applications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL,
    amount NUMERIC(15,2) NOT NULL,
    term INT NOT NULL,
    loan_type_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_loan_type
        FOREIGN KEY (loan_type_id)
        REFERENCES credi_ya.loan_type (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);