-- Flyway Database Migration V4
-- Schema refinements for the Bed Management Module

-- 1. Refine wards table
ALTER TABLE wards ADD COLUMN code VARCHAR(50) NOT NULL UNIQUE;
ALTER TABLE wards ADD COLUMN department VARCHAR(100) NOT NULL;
ALTER TABLE wards ADD COLUMN floor INT NOT NULL DEFAULT 0;
ALTER TABLE wards ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE wards RENAME COLUMN max_capacity TO capacity;

-- 2. Refine beds table
ALTER TABLE beds DROP COLUMN status;
ALTER TABLE beds ADD COLUMN bed_type VARCHAR(50) NOT NULL DEFAULT 'GENERAL';
ALTER TABLE beds ADD COLUMN availability_status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE';
ALTER TABLE beds ADD COLUMN cleaning_status VARCHAR(20) NOT NULL DEFAULT 'CLEAN';
ALTER TABLE beds ADD COLUMN maintenance_status VARCHAR(20) NOT NULL DEFAULT 'FUNCTIONAL';

-- 3. Refine bed_allocations table
ALTER TABLE bed_allocations RENAME COLUMN allocated_at TO admission_date;
ALTER TABLE bed_allocations RENAME COLUMN discharged_at TO discharge_date;
ALTER TABLE bed_allocations ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ADMITTED';

-- 4. Create bed_transfers table
CREATE TABLE bed_transfers (
    id UUID PRIMARY KEY,
    patient_id UUID NOT NULL,
    from_bed_id UUID NOT NULL,
    to_bed_id UUID NOT NULL,
    transfer_time TIMESTAMP WITH TIME ZONE NOT NULL,
    reason VARCHAR(255) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_bed_transfers_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_bed_transfers_from_bed FOREIGN KEY (from_bed_id) REFERENCES beds(id),
    CONSTRAINT fk_bed_transfers_to_bed FOREIGN KEY (to_bed_id) REFERENCES beds(id)
);

-- Indexes for performance optimizing
CREATE INDEX idx_bed_transfers_patient ON bed_transfers(patient_id);
CREATE INDEX idx_bed_transfers_time ON bed_transfers(transfer_time);
