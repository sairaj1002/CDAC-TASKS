-- Sample data for the WAMIS GePNIC integration module (PostgreSQL, database: wamis)
-- Run AFTER the backend has started once (it creates both schemas and loads the xls masters;
-- the pretender rows below use those master values, enforced by foreign keys):
--   psql -U postgres -d wamis -f sample-data.sql
-- Safe to re-run: every insert is skipped if its row already exists.

SET search_path TO wamis_core, wamis_intergation, wamis_master;

-- 1. master dropdowns: loaded from the xls by db/master-data.sql at backend startup

-- 2. wamis_pretenders --------------------------------------------------------
-- master values are written as text below and looked up to their master ids
INSERT INTO wamis_pretenders (
    unique_file_no, tender_ref_no, work_id, office_id, org_id, tender_title, tender_description,
    tender_type_id, form_contract_id, tender_category_id, covers_count, bid_openers_id, automatic_evaluation,
    allow_multi_currency_boq, creator_name, creator_reference, tender_value, bid_validity_days,
    delivery_period_days, location_details, pincode, pre_bid_meeting, pre_bid_meeting_place,
    pre_bid_meeting_address, bid_open_place, tenderer_class_id, inviting_officer, inviting_officer_address,
    allow_itemwise_evaluation, fee_amount, emd_amount, allow_multi_currency_fee,
    publishing_date, doc_download_start_date, seek_clarification_start_date, seek_clarification_end_date,
    pre_bid_meeting_date, bid_submission_start_date, bid_submission_end_date, bid_open_date,
    status, created_at, updated_at)
SELECT v.unique_file_no, v.tender_ref_no, v.work_id, v.office_id, v.org_id, v.tender_title, v.tender_description,
       tt.id, fc.id, tc.id, v.covers_count, bo.id, v.automatic_evaluation,
       v.allow_multi_currency_boq, v.creator_name, v.creator_reference, v.tender_value, v.bid_validity_days,
       v.delivery_period_days, v.location_details, v.pincode, v.pre_bid_meeting, v.pre_bid_meeting_place,
       v.pre_bid_meeting_address, v.bid_open_place, cl.id, v.inviting_officer, v.inviting_officer_address,
       v.allow_itemwise_evaluation, v.fee_amount, v.emd_amount, v.allow_multi_currency_fee,
       v.publishing_date, v.doc_download_start_date, v.seek_clarification_start_date, v.seek_clarification_end_date,
       v.pre_bid_meeting_date, v.bid_submission_start_date, v.bid_submission_end_date, v.bid_open_date,
       v.status, v.created_at, v.updated_at
FROM (VALUES
    ('WAMIS/PWD/2026/001', 'TND/PWD/2026/001', 1001, 101, 1, 'Construction of Canal Lining - Reach 1',
     'Lining of 4.5 km of distributary canal including cross-drainage works.',
     'Open Tender', 'Percentage', 'Works', 2, '2 Off 2', FALSE, FALSE, 'R. Sharma', 'WAMIS',
     12500000.00, 90, 365, 'Pune', 411001, TRUE, 'Divisional Office', 'PWD Division, Pune - 411001',
     'Divisional Office, Pune', 'Class A', 'Executive Engineer', 'PWD Division, Station Road, Pune - 411001',
     FALSE, 5000.00, 250000.00, FALSE,
     TIMESTAMP '2026-11-10 10:00', TIMESTAMP '2026-11-10 12:00', TIMESTAMP '2026-11-11 10:00', TIMESTAMP '2026-11-15 17:00',
     TIMESTAMP '2026-11-14 11:00', TIMESTAMP '2026-11-12 10:00', TIMESTAMP '2026-11-28 17:00', TIMESTAMP '2026-11-30 11:00',
     'DRAFT', now(), now()),
    ('WAMIS/PWD/2026/002', 'TND/PWD/2026/002', 1002, 102, 1, 'Supply of Flow Meters for Barrage',
     'Supply and installation of 12 ultrasonic flow meters.',
     'Open Tender', 'Item Rate', 'Goods', 2, '2 Off 2', TRUE, FALSE, 'A. Kulkarni', 'WAMIS',
     3200000.00, 60, 90, 'Nashik', 422001, FALSE, 'NA', 'NA',
     'Circle Office, Nashik', 'Class B', 'Superintending Engineer', 'PWD Circle, College Road, Nashik - 422001',
     FALSE, 2000.00, 64000.00, FALSE,
     TIMESTAMP '2026-11-12 10:00', TIMESTAMP '2026-11-12 12:00', NULL, NULL,
     NULL, TIMESTAMP '2026-11-13 10:00', TIMESTAMP '2026-11-27 17:00', TIMESTAMP '2026-11-29 11:00',
     'READY', now(), now()),
    ('WAMIS/PWD/2026/003', 'TND/PWD/2026/003', 1003, 103, 1, 'Dam Safety Consultancy Services',
     'Instrumentation review and safety audit of two earthen dams.',
     'Limited', 'Lump-sum', 'Services', 2, '2 Off 3', FALSE, FALSE, 'S. Patil', 'WAMIS',
     8700000.00, 120, 180, 'Nagpur', 440001, TRUE, 'Project Office', 'Project Office, Civil Lines, Nagpur - 440001',
     'Project Office, Nagpur', 'Class A', 'Chief Engineer', 'Water Resources Dept, Civil Lines, Nagpur - 440001',
     FALSE, 10000.00, 174000.00, FALSE,
     TIMESTAMP '2026-10-20 10:00', TIMESTAMP '2026-10-20 12:00', TIMESTAMP '2026-10-22 10:00', TIMESTAMP '2026-10-26 17:00',
     TIMESTAMP '2026-10-25 11:00', TIMESTAMP '2026-10-21 10:00', TIMESTAMP '2026-11-05 17:00', TIMESTAMP '2026-11-07 11:00',
     'ACCEPTED', now(), now())
) AS v(unique_file_no, tender_ref_no, work_id, office_id, org_id, tender_title, tender_description,
       tender_type, form_contract, category, covers_count, bid_openers, automatic_evaluation,
       allow_multi_currency_boq, creator_name, creator_reference, tender_value, bid_validity_days,
       delivery_period_days, location_details, pincode, pre_bid_meeting, pre_bid_meeting_place,
       pre_bid_meeting_address, bid_open_place, tenderer_class, inviting_officer, inviting_officer_address,
       allow_itemwise_evaluation, fee_amount, emd_amount, allow_multi_currency_fee,
       publishing_date, doc_download_start_date, seek_clarification_start_date, seek_clarification_end_date,
       pre_bid_meeting_date, bid_submission_start_date, bid_submission_end_date, bid_open_date,
       status, created_at, updated_at)
JOIN master_tender_type      tt ON tt.tendertype     = v.tender_type
JOIN master_form_of_contract fc ON fc.formofcontract = v.form_contract
JOIN master_tender_category  tc ON tc.category       = v.category
JOIN master_bid_openers_type bo ON bo.botype         = v.bid_openers
JOIN master_tenderer_class   cl ON cl.tenderclass    = v.tenderer_class
WHERE NOT EXISTS (SELECT 1 FROM wamis_pretenders p WHERE p.unique_file_no = v.unique_file_no);

-- 2b. wamis_pretender_documents (children of the pretenders above) ------------
INSERT INTO wamis_pretender_documents (pretender_id, doc_unique_id, doc_code, doc_name, doc_description, doc_mime_type, doc_size, doc_priority, uploaded, file_hash, created_at)
SELECT p.id, v.doc_unique_id, v.doc_code, v.doc_name, v.doc_description, v.doc_mime_type, v.doc_size, v.doc_priority, v.uploaded, v.file_hash, now()
FROM (VALUES
    ('WAMIS/PWD/2026/001', 50001, 'NITDOC', 'tender-notice.pdf', 'Notice inviting tender', '.pdf', 245760,  1, FALSE, NULL),
    ('WAMIS/PWD/2026/001', 50002, 'BOQDOC', 'boq.xls',           'Bill of quantities',     '.xls', 102400,  2, FALSE, NULL),
    ('WAMIS/PWD/2026/002', 50003, 'NITDOC', 'tender-notice.pdf', 'Notice inviting tender', '.pdf', 198656,  1, FALSE, NULL),
    ('WAMIS/PWD/2026/003', 50004, 'NITDOC', 'tender-notice.pdf', 'Notice inviting tender', '.pdf', 301056,  1, TRUE,
     'a3f1c9d27b6e48f0a1d5c2b7e9f04a68d3c1b5e7f2a94c60b8d1e3f5a7c92b04')
) AS v(file_no, doc_unique_id, doc_code, doc_name, doc_description, doc_mime_type, doc_size, doc_priority, uploaded, file_hash)
JOIN wamis_pretenders p ON p.unique_file_no = v.file_no
WHERE NOT EXISTS (SELECT 1 FROM wamis_pretender_documents d WHERE d.doc_unique_id = v.doc_unique_id);

-- 3. wamis_integration_transactions -----------------------------------------
INSERT INTO wamis_integration_transactions (operation, portal_code, pretender_id, work_id, correlation_id, idempotency_key, status, attempt_count, external_reference, error_code, sanitized_error_message, created_at, updated_at, completed_at)
SELECT v.operation, 'GEPNIC', p.id, p.work_id, v.correlation_id, v.idempotency_key, v.status, v.attempt_count, v.external_reference, v.error_code, v.msg, v.ts, v.ts, v.completed
FROM (VALUES
    ('WAMIS/PWD/2026/003', 'PRE_TENDER',      'corr-0001-aaaa', 'idem-0001', 'ACCEPTED', 1, 'GEPNIC-TND-884201', NULL,          NULL,                                     TIMESTAMP '2026-10-20 10:05', TIMESTAMP '2026-10-20 10:05'),
    ('WAMIS/PWD/2026/003', 'DOCUMENT_UPLOAD', 'corr-0002-bbbb', 'idem-0002', 'ACCEPTED', 1, 'GEPNIC-DOC-120045', NULL,          NULL,                                     TIMESTAMP '2026-10-20 10:12', TIMESTAMP '2026-10-20 10:12'),
    ('WAMIS/PWD/2026/002', 'PRE_TENDER',      'corr-0003-cccc', 'idem-0003', 'FAILED',   3, NULL,               'GEPNIC_TIMEOUT', 'Portal did not respond within 30 seconds.', TIMESTAMP '2026-11-03 09:40', TIMESTAMP '2026-11-03 09:41'),
    ('WAMIS/PWD/2026/001', 'PRE_TENDER',      'corr-0004-dddd', 'idem-0004', 'QUEUED',   0, NULL,               NULL,          NULL,                                     TIMESTAMP '2026-11-03 10:00', NULL)
) AS v(file_no, operation, correlation_id, idempotency_key, status, attempt_count, external_reference, error_code, msg, ts, completed)
JOIN wamis_pretenders p ON p.unique_file_no = v.file_no
WHERE NOT EXISTS (SELECT 1 FROM wamis_integration_transactions t WHERE t.correlation_id = v.correlation_id);

-- 4. wamis_integration_audit_events -----------------------------------------
INSERT INTO wamis_integration_audit_events (transaction_id, operation, portal_code, actor, correlation_id, from_status, to_status, event_timestamp, sanitized_error_code, sanitized_message, external_reference)
SELECT t.id, v.operation, 'GEPNIC', v.actor, v.correlation_id, v.from_status, v.to_status, v.ts, v.error_code, v.msg, v.external_reference
FROM (VALUES
    ('PRE_TENDER',      'system', 'corr-0001-aaaa', 'QUEUED',      'IN_PROGRESS', TIMESTAMP '2026-10-20 10:04', NULL, NULL, NULL),
    ('PRE_TENDER',      'system', 'corr-0001-aaaa', 'IN_PROGRESS', 'ACCEPTED',    TIMESTAMP '2026-10-20 10:05', NULL, 'Tender accepted by portal.', 'GEPNIC-TND-884201'),
    ('DOCUMENT_UPLOAD', 'system', 'corr-0002-bbbb', 'IN_PROGRESS', 'ACCEPTED',    TIMESTAMP '2026-10-20 10:12', NULL, 'Document uploaded.',         'GEPNIC-DOC-120045'),
    ('PRE_TENDER',      'system', 'corr-0003-cccc', 'IN_PROGRESS', 'RETRY_PENDING', TIMESTAMP '2026-11-03 09:35', 'GEPNIC_TIMEOUT', 'Portal timed out; retry scheduled.', NULL),
    ('PRE_TENDER',      'system', 'corr-0003-cccc', 'RETRY_PENDING', 'FAILED',    TIMESTAMP '2026-11-03 09:41', 'GEPNIC_TIMEOUT', 'Retries exhausted.', NULL),
    ('PRE_TENDER',      'r.sharma', 'corr-0004-dddd', 'READY',     'QUEUED',      TIMESTAMP '2026-11-03 10:00', NULL, 'Queued by user.', NULL)
) AS v(operation, actor, correlation_id, from_status, to_status, ts, error_code, msg, external_reference)
JOIN wamis_integration_transactions t ON t.correlation_id = v.correlation_id
WHERE NOT EXISTS (
    SELECT 1 FROM wamis_integration_audit_events a
    WHERE a.correlation_id = v.correlation_id AND a.to_status = v.to_status AND a.event_timestamp = v.ts
);


-- 4. Post-Tender DB Parameters (controls tab visibility on Post-Tender screen)
INSERT INTO wamis_core.wamis_master_dropdowns (category, code, display_name, description, display_order, active, created_by, created_at)
SELECT 'POST_TENDER_PARAM', v.code, v.display_name, v.description, v.display_order, v.active, 'system', CURRENT_TIMESTAMP
FROM (VALUES
    ('ENABLE_CORRIGENDUM_TAB', 'Enable Corrigendum Tab', 'Controls visibility of Corrigendum Retrieval tab on Post-Tender screen', 1, TRUE),
    ('ENABLE_NEGOTIATION_TAB', 'Enable Negotiation Tab', 'Controls visibility of Bidder Negotiation tab on Post-Tender screen', 2, TRUE)
) AS v(code, display_name, description, display_order, active)
WHERE NOT EXISTS (
    SELECT 1 FROM wamis_core.wamis_master_dropdowns d
    WHERE d.category = 'POST_TENDER_PARAM' AND d.code = v.code
);
