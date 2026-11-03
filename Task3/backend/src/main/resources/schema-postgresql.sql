-- ====================================================================
-- WAMIS - GePNIC Integration Module: PostgreSQL Schema Initialization
-- Database: wamis (PostgreSQL 14+)
-- ====================================================================

-- 1. Master Dropdowns Table with Audit Tracking
CREATE TABLE IF NOT EXISTS wamis_master_dropdowns (
    id BIGSERIAL PRIMARY KEY,
    category VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL,
    display_name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    display_order INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(100) DEFAULT 'SYSTEM',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_wamis_dropdown_cat_code UNIQUE (category, code)
);

CREATE INDEX IF NOT EXISTS idx_dropdown_cat ON wamis_master_dropdowns(category);
CREATE INDEX IF NOT EXISTS idx_dropdown_code ON wamis_master_dropdowns(code);

-- 2. Pre-Tender Core Entity
CREATE TABLE IF NOT EXISTS wamis_pretenders (
    id BIGSERIAL PRIMARY KEY,
    unique_file_no VARCHAR(100) NOT NULL UNIQUE,
    tender_ref_no VARCHAR(100) NOT NULL UNIQUE,
    work_id BIGINT NOT NULL,
    office_id BIGINT NOT NULL,
    org_id BIGINT NOT NULL,
    tender_title VARCHAR(250) NOT NULL,
    tender_description VARCHAR(2000),
    tender_type VARCHAR(50) NOT NULL DEFAULT 'Open Tender',
    form_contract VARCHAR(50) NOT NULL DEFAULT 'Percentage',
    category VARCHAR(50) NOT NULL DEFAULT 'Works',
    covers_count INT NOT NULL DEFAULT 2,
    bid_openers VARCHAR(50) DEFAULT '2 Off 2',
    automatic_evaluation BOOLEAN DEFAULT FALSE,
    tender_value NUMERIC(16, 2) NOT NULL,
    fee_amount NUMERIC(12, 2) NOT NULL,
    emd_amount NUMERIC(12, 2) NOT NULL,
    publishing_date TIMESTAMP NOT NULL,
    doc_download_start_date TIMESTAMP NOT NULL,
    seek_clarification_start_date TIMESTAMP,
    seek_clarification_end_date TIMESTAMP,
    pre_bid_meeting BOOLEAN DEFAULT FALSE,
    pre_bid_meeting_place VARCHAR(250),
    pre_bid_meeting_address VARCHAR(500),
    bid_submission_start_date TIMESTAMP NOT NULL,
    bid_submission_end_date TIMESTAMP NOT NULL,
    bid_open_date TIMESTAMP NOT NULL,
    bid_validity_days INT DEFAULT 90,
    delivery_period_days INT DEFAULT 365,
    location VARCHAR(100),
    pincode INT,
    bid_open_place VARCHAR(250),
    tenderer_class VARCHAR(50),
    inviting_officer VARCHAR(150) NOT NULL,
    inviting_officer_address VARCHAR(500) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    created_by VARCHAR(100),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_pretender_status ON wamis_pretenders(status);
CREATE INDEX IF NOT EXISTS idx_pretender_office ON wamis_pretenders(office_id);

-- 3. Pre-Tender Documents
CREATE TABLE IF NOT EXISTS wamis_pretender_documents (
    id BIGSERIAL PRIMARY KEY,
    pretender_id BIGINT NOT NULL REFERENCES wamis_pretenders(id) ON DELETE CASCADE,
    doc_unique_id BIGINT NOT NULL,
    doc_code VARCHAR(50) NOT NULL,
    doc_name VARCHAR(255) NOT NULL,
    doc_description VARCHAR(1000),
    doc_mime_type VARCHAR(50) NOT NULL,
    doc_size BIGINT NOT NULL,
    doc_priority INT NOT NULL,
    uploaded BOOLEAN NOT NULL DEFAULT FALSE,
    file_hash VARCHAR(128),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_pretender_docs_parent ON wamis_pretender_documents(pretender_id);

-- 4. Integration Transaction Job Tracking
CREATE TABLE IF NOT EXISTS wamis_integration_transactions (
    id BIGSERIAL PRIMARY KEY,
    operation VARCHAR(50) NOT NULL,
    portal_code VARCHAR(30) NOT NULL,
    pretender_id BIGINT,
    work_id BIGINT,
    correlation_id VARCHAR(100) NOT NULL,
    idempotency_key VARCHAR(100),
    status VARCHAR(30) NOT NULL,
    attempt_count INT NOT NULL DEFAULT 0,
    external_reference VARCHAR(100),
    error_code VARCHAR(50),
    sanitized_error_message VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_txn_correlation_id ON wamis_integration_transactions(correlation_id);
CREATE INDEX IF NOT EXISTS idx_txn_idempotency_key ON wamis_integration_transactions(idempotency_key);
CREATE INDEX IF NOT EXISTS idx_txn_pretender_id ON wamis_integration_transactions(pretender_id);
CREATE INDEX IF NOT EXISTS idx_txn_status ON wamis_integration_transactions(status);

-- 5. Immutable Integration Audit Events (Append-Only)
CREATE TABLE IF NOT EXISTS wamis_integration_audit_events (
    id BIGSERIAL PRIMARY KEY,
    transaction_id BIGINT,
    operation VARCHAR(50) NOT NULL,
    portal_code VARCHAR(30) NOT NULL,
    actor VARCHAR(100) NOT NULL,
    correlation_id VARCHAR(100) NOT NULL,
    from_status VARCHAR(30),
    to_status VARCHAR(30) NOT NULL,
    event_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sanitized_error_code VARCHAR(50),
    sanitized_message VARCHAR(1000),
    external_reference VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_audit_transaction_id ON wamis_integration_audit_events(transaction_id);
CREATE INDEX IF NOT EXISTS idx_audit_correlation_id ON wamis_integration_audit_events(correlation_id);
CREATE INDEX IF NOT EXISTS idx_audit_timestamp ON wamis_integration_audit_events(event_timestamp);
