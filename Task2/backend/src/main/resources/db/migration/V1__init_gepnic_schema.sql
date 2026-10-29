-- Initial PostgreSQL schema for WAMIS-GePNIC Integration Service

CREATE TABLE IF NOT EXISTS gepnic_integration_transaction (
    id BIGSERIAL PRIMARY KEY,
    transaction_id VARCHAR(64) NOT NULL UNIQUE,
    correlation_id VARCHAR(128) NOT NULL,
    operation VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    source_system VARCHAR(32) NOT NULL,
    target_system VARCHAR(32) NOT NULL,
    office_code VARCHAR(64),
    endpoint_url VARCHAR(512),
    request_payload_sanitized TEXT,
    response_payload_sanitized TEXT,
    response_code INT,
    response_message VARCHAR(1024),
    records_processed INT,
    retry_count INT NOT NULL DEFAULT 0,
    error_message VARCHAR(2048),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_txn_transaction_id ON gepnic_integration_transaction(transaction_id);
CREATE INDEX IF NOT EXISTS idx_txn_correlation_id ON gepnic_integration_transaction(correlation_id);
CREATE INDEX IF NOT EXISTS idx_txn_status ON gepnic_integration_transaction(status);
CREATE INDEX IF NOT EXISTS idx_txn_operation ON gepnic_integration_transaction(operation);
CREATE INDEX IF NOT EXISTS idx_txn_created_at ON gepnic_integration_transaction(created_at);

CREATE TABLE IF NOT EXISTS gepnic_document_transaction (
    id BIGSERIAL PRIMARY KEY,
    unique_file_no VARCHAR(128) NOT NULL,
    doc_unique_id VARCHAR(64) NOT NULL,
    tender_reference VARCHAR(128) NOT NULL,
    file_name VARCHAR(256) NOT NULL,
    doc_code VARCHAR(64),
    doc_type VARCHAR(32),
    file_size_bytes BIGINT,
    upload_status VARCHAR(32) NOT NULL,
    response_code INT,
    response_message VARCHAR(1024),
    uploaded_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_doc_unique_file_no ON gepnic_document_transaction(unique_file_no);
CREATE INDEX IF NOT EXISTS idx_doc_tender_reference ON gepnic_document_transaction(tender_reference);
CREATE INDEX IF NOT EXISTS idx_doc_upload_status ON gepnic_document_transaction(upload_status);

CREATE TABLE IF NOT EXISTS gepnic_integration_configuration (
    id BIGSERIAL PRIMARY KEY,
    environment_name VARCHAR(32) NOT NULL,
    base_url VARCHAR(512) NOT NULL,
    unique_user_id VARCHAR(128) NOT NULL,
    key_id VARCHAR(128) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    connect_timeout_ms INT NOT NULL DEFAULT 10000,
    read_timeout_ms INT NOT NULL DEFAULT 30000,
    updated_by VARCHAR(64),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);
