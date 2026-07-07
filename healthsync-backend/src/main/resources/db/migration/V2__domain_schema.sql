-- Flyway Database Migration V2
-- Scaffolds the entire domain schema for HealthSync AI

-- 1. Organization
CREATE TABLE districts (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    region VARCHAR(100) NOT NULL,
    population BIGINT NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE health_centers (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    type VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL,
    capacity INT NOT NULL,
    district_id UUID NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_health_centers_district FOREIGN KEY (district_id) REFERENCES districts(id)
);

-- 2. Medicine Inventory
CREATE TABLE medicine_categories (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE medicines (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    generic_name VARCHAR(150) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    category_id UUID NOT NULL,
    strength VARCHAR(50) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_medicines_category FOREIGN KEY (category_id) REFERENCES medicine_categories(id)
);

CREATE TABLE suppliers (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    contact_person VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE inventories (
    id UUID PRIMARY KEY,
    health_center_id UUID NOT NULL,
    medicine_id UUID NOT NULL,
    quantity INT NOT NULL,
    batch_number VARCHAR(50) NOT NULL,
    expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
    reorder_level INT NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_inventories_center FOREIGN KEY (health_center_id) REFERENCES health_centers(id),
    CONSTRAINT fk_inventories_medicine FOREIGN KEY (medicine_id) REFERENCES medicines(id),
    CONSTRAINT uc_center_medicine_batch UNIQUE (health_center_id, medicine_id, batch_number)
);

CREATE TABLE inventory_transactions (
    id UUID PRIMARY KEY,
    source_center_id UUID,
    destination_center_id UUID,
    medicine_id UUID NOT NULL,
    quantity INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    remarks VARCHAR(255),
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_inv_trans_source FOREIGN KEY (source_center_id) REFERENCES health_centers(id),
    CONSTRAINT fk_inv_trans_dest FOREIGN KEY (destination_center_id) REFERENCES health_centers(id),
    CONSTRAINT fk_inv_trans_medicine FOREIGN KEY (medicine_id) REFERENCES medicines(id)
);

-- 3. Patient
CREATE TABLE disease_categories (
    id UUID PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(255),
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE patients (
    id UUID PRIMARY KEY,
    user_id UUID UNIQUE,
    medical_record_number VARCHAR(50) NOT NULL UNIQUE,
    gender VARCHAR(20) NOT NULL,
    date_of_birth DATE NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_patients_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 4. Doctor
CREATE TABLE shifts (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE doctors (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    license_number VARCHAR(50) NOT NULL UNIQUE,
    specialization VARCHAR(100) NOT NULL,
    health_center_id UUID NOT NULL,
    default_shift_id UUID NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_doctors_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_doctors_center FOREIGN KEY (health_center_id) REFERENCES health_centers(id),
    CONSTRAINT fk_doctors_shift FOREIGN KEY (default_shift_id) REFERENCES shifts(id)
);

CREATE TABLE attendances (
    id UUID PRIMARY KEY,
    doctor_id UUID NOT NULL,
    attendance_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    check_in_time TIME,
    check_out_time TIME,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_attendances_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    CONSTRAINT uc_doctor_date UNIQUE (doctor_id, attendance_date)
);

-- 5. Patient Visits (Depends on Patient and Doctor)
CREATE TABLE patient_visits (
    id UUID PRIMARY KEY,
    patient_id UUID NOT NULL,
    health_center_id UUID NOT NULL,
    doctor_id UUID NOT NULL,
    visit_date TIMESTAMP WITH TIME ZONE NOT NULL,
    symptoms TEXT NOT NULL,
    diagnosis TEXT NOT NULL,
    disease_category_id UUID NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_patient_visits_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_patient_visits_center FOREIGN KEY (health_center_id) REFERENCES health_centers(id),
    CONSTRAINT fk_patient_visits_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    CONSTRAINT fk_patient_visits_disease FOREIGN KEY (disease_category_id) REFERENCES disease_categories(id)
);

-- 6. Bed Management
CREATE TABLE wards (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    health_center_id UUID NOT NULL,
    gender_restriction VARCHAR(50) NOT NULL,
    max_capacity INT NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_wards_center FOREIGN KEY (health_center_id) REFERENCES health_centers(id)
);

CREATE TABLE beds (
    id UUID PRIMARY KEY,
    bed_number VARCHAR(50) NOT NULL,
    ward_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_beds_ward FOREIGN KEY (ward_id) REFERENCES wards(id),
    CONSTRAINT uc_ward_bed UNIQUE (ward_id, bed_number)
);

CREATE TABLE bed_allocations (
    id UUID PRIMARY KEY,
    bed_id UUID NOT NULL,
    patient_id UUID NOT NULL,
    allocated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    discharged_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_bed_allocations_bed FOREIGN KEY (bed_id) REFERENCES beds(id),
    CONSTRAINT fk_bed_allocations_patient FOREIGN KEY (patient_id) REFERENCES patients(id)
);

-- 7. Laboratory
CREATE TABLE laboratories (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    health_center_id UUID NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_laboratories_center FOREIGN KEY (health_center_id) REFERENCES health_centers(id)
);

CREATE TABLE diagnostic_tests (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    normal_range VARCHAR(100) NOT NULL,
    base_cost DECIMAL(10,2) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE test_availabilities (
    id UUID PRIMARY KEY,
    laboratory_id UUID NOT NULL,
    diagnostic_test_id UUID NOT NULL,
    is_available BOOLEAN NOT NULL,
    turnaround_time_hours INT NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_test_avail_lab FOREIGN KEY (laboratory_id) REFERENCES laboratories(id),
    CONSTRAINT fk_test_avail_test FOREIGN KEY (diagnostic_test_id) REFERENCES diagnostic_tests(id),
    CONSTRAINT uc_lab_test UNIQUE (laboratory_id, diagnostic_test_id)
);

-- 8. Notifications, Alerts & Auditing
CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    recipient_id UUID NOT NULL,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(20) NOT NULL,
    is_read BOOLEAN NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_notifications_recipient FOREIGN KEY (recipient_id) REFERENCES users(id)
);

CREATE TABLE alerts (
    id UUID PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(255) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    acknowledged BOOLEAN NOT NULL,
    health_center_id UUID NOT NULL,
    triggered_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_alerts_center FOREIGN KEY (health_center_id) REFERENCES health_centers(id)
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    action VARCHAR(150) NOT NULL,
    entity_name VARCHAR(100) NOT NULL,
    entity_id VARCHAR(50),
    ip_address VARCHAR(50),
    details TEXT,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL
);

-- 9. Analytics & AI Outbreak
CREATE TABLE prediction_histories (
    id UUID PRIMARY KEY,
    prediction_type VARCHAR(100) NOT NULL,
    input_parameters TEXT NOT NULL,
    predicted_value VARCHAR(255) NOT NULL,
    confidence_score DECIMAL(3,2) NOT NULL,
    generated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE demand_forecasts (
    id UUID PRIMARY KEY,
    health_center_id UUID NOT NULL,
    medicine_id UUID NOT NULL,
    forecasted_quantity INT NOT NULL,
    forecast_date DATE NOT NULL,
    confidence_level DECIMAL(3,2) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_demand_forecast_center FOREIGN KEY (health_center_id) REFERENCES health_centers(id),
    CONSTRAINT fk_demand_forecast_medicine FOREIGN KEY (medicine_id) REFERENCES medicines(id)
);

CREATE TABLE redistribution_recommendations (
    id UUID PRIMARY KEY,
    source_center_id UUID NOT NULL,
    destination_center_id UUID NOT NULL,
    medicine_id UUID NOT NULL,
    quantity INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    reason VARCHAR(255) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_redist_recom_source FOREIGN KEY (source_center_id) REFERENCES health_centers(id),
    CONSTRAINT fk_redist_recom_dest FOREIGN KEY (destination_center_id) REFERENCES health_centers(id),
    CONSTRAINT fk_redist_recom_medicine FOREIGN KEY (medicine_id) REFERENCES medicines(id)
);

-- Indexes for performance optimizing
CREATE INDEX idx_districts_name ON districts(name);
CREATE INDEX idx_health_centers_district ON health_centers(district_id);
CREATE INDEX idx_health_centers_type ON health_centers(type);
CREATE INDEX idx_medicine_categories_name ON medicine_categories(name);
CREATE INDEX idx_medicines_code ON medicines(code);
CREATE INDEX idx_medicines_name ON medicines(name);
CREATE INDEX idx_medicines_category ON medicines(category_id);
CREATE INDEX idx_suppliers_name ON suppliers(name);
CREATE INDEX idx_inventories_center ON inventories(health_center_id);
CREATE INDEX idx_inventories_medicine ON inventories(medicine_id);
CREATE INDEX idx_inventories_expiry ON inventories(expiry_date);
CREATE INDEX idx_inv_trans_source ON inventory_transactions(source_center_id);
CREATE INDEX idx_inv_trans_dest ON inventory_transactions(destination_center_id);
CREATE INDEX idx_inv_trans_medicine ON inventory_transactions(medicine_id);
CREATE INDEX idx_inv_trans_type ON inventory_transactions(type);
CREATE INDEX idx_disease_categories_code ON disease_categories(code);
CREATE INDEX idx_disease_categories_name ON disease_categories(name);
CREATE INDEX idx_patients_mrn ON patients(medical_record_number);
CREATE INDEX idx_patients_user ON patients(user_id);
CREATE INDEX idx_doctors_license ON doctors(license_number);
CREATE INDEX idx_doctors_center ON doctors(health_center_id);
CREATE INDEX idx_doctors_user ON doctors(user_id);
CREATE INDEX idx_attendances_doctor ON attendances(doctor_id);
CREATE INDEX idx_attendances_date ON attendances(attendance_date);
CREATE INDEX idx_attendances_status ON attendances(status);
CREATE INDEX idx_patient_visits_patient ON patient_visits(patient_id);
CREATE INDEX idx_patient_visits_center ON patient_visits(health_center_id);
CREATE INDEX idx_patient_visits_doctor ON patient_visits(doctor_id);
CREATE INDEX idx_patient_visits_disease ON patient_visits(disease_category_id);
CREATE INDEX idx_wards_center ON wards(health_center_id);
CREATE INDEX idx_beds_ward ON beds(ward_id);
CREATE INDEX idx_beds_status ON beds(status);
CREATE INDEX idx_bed_alloc_bed ON bed_allocations(bed_id);
CREATE INDEX idx_bed_alloc_patient ON bed_allocations(patient_id);
CREATE INDEX idx_bed_alloc_allocated ON bed_allocations(allocated_at);
CREATE INDEX idx_laboratories_center ON laboratories(health_center_id);
CREATE INDEX idx_diag_tests_code ON diagnostic_tests(code);
CREATE INDEX idx_diag_tests_name ON diagnostic_tests(name);
CREATE INDEX idx_test_avail_lab ON test_availabilities(laboratory_id);
CREATE INDEX idx_test_avail_test ON test_availabilities(diagnostic_test_id);
CREATE INDEX idx_notifications_recipient ON notifications(recipient_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
CREATE INDEX idx_alerts_center ON alerts(health_center_id);
CREATE INDEX idx_alerts_severity ON alerts(severity);
CREATE INDEX idx_alerts_acknowledged ON alerts(acknowledged);
CREATE INDEX idx_audit_logs_username ON audit_logs(username);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX idx_pred_hist_type ON prediction_histories(prediction_type);
CREATE INDEX idx_demand_forecast_center ON demand_forecasts(health_center_id);
CREATE INDEX idx_demand_forecast_medicine ON demand_forecasts(medicine_id);
CREATE INDEX idx_demand_forecast_date ON demand_forecasts(forecast_date);
CREATE INDEX idx_redist_recom_source ON redistribution_recommendations(source_center_id);
CREATE INDEX idx_redist_recom_dest ON redistribution_recommendations(destination_center_id);
CREATE INDEX idx_redist_recom_medicine ON redistribution_recommendations(medicine_id);
CREATE INDEX idx_redist_recom_status ON redistribution_recommendations(status);
