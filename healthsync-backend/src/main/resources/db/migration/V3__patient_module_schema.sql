-- Flyway Database Migration V3
-- Schema refinements for the Patient & Footfall Management Module

-- 1. Create patient_emergency_contacts table
CREATE TABLE patient_emergency_contacts (
    id UUID PRIMARY KEY,
    contact_name VARCHAR(150) NOT NULL,
    relationship VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL
);

-- 2. Create sequence for auto-generating Patient ID (PAT-YYYY-[seq])
CREATE SEQUENCE patient_id_seq START WITH 1 INCREMENT BY 1;

-- 3. Refine patients table
ALTER TABLE patients ADD COLUMN patient_id VARCHAR(50) NOT NULL UNIQUE;
ALTER TABLE patients ADD COLUMN full_name VARCHAR(150) NOT NULL;
ALTER TABLE patients ADD COLUMN age INT NOT NULL;
ALTER TABLE patients ADD COLUMN blood_group VARCHAR(20);
ALTER TABLE patients ADD COLUMN mobile_number VARCHAR(20) NOT NULL UNIQUE;
ALTER TABLE patients ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE patients ADD COLUMN district_id UUID NOT NULL;
ALTER TABLE patients ADD COLUMN health_center_id UUID NOT NULL;
ALTER TABLE patients ADD COLUMN emergency_contact_id UUID UNIQUE;

-- Add new constraints for patients table
ALTER TABLE patients ADD CONSTRAINT fk_patients_district FOREIGN KEY (district_id) REFERENCES districts(id);
ALTER TABLE patients ADD CONSTRAINT fk_patients_health_center FOREIGN KEY (health_center_id) REFERENCES health_centers(id);
ALTER TABLE patients ADD CONSTRAINT fk_patients_emergency_contact FOREIGN KEY (emergency_contact_id) REFERENCES patient_emergency_contacts(id) ON DELETE SET NULL;

-- Remove old contact_number column from V2
ALTER TABLE patients DROP COLUMN contact_number;

-- 4. Refine patient_visits table
ALTER TABLE patient_visits ADD COLUMN visit_time TIME NOT NULL;
ALTER TABLE patient_visits ADD COLUMN department VARCHAR(100) NOT NULL;
ALTER TABLE patient_visits ADD COLUMN visit_type VARCHAR(50) NOT NULL;
ALTER TABLE patient_visits ADD COLUMN prescription_reference VARCHAR(100);
ALTER TABLE patient_visits ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED';
ALTER TABLE patient_visits ADD COLUMN visit_duration INT NOT NULL DEFAULT 0;
ALTER TABLE patient_visits ADD COLUMN waiting_time INT NOT NULL DEFAULT 0;
ALTER TABLE patient_visits ADD COLUMN age_group VARCHAR(20) NOT NULL DEFAULT 'ADULT';

-- 5. Refine disease_categories table
ALTER TABLE disease_categories ADD COLUMN priority INT NOT NULL DEFAULT 3;
ALTER TABLE disease_categories ADD COLUMN communicable BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE disease_categories ADD COLUMN seasonal BOOLEAN NOT NULL DEFAULT FALSE;
