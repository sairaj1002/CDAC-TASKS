# WAMIS–GePNIC Integration Service

## Enterprise Build Plan, Requirements, Screens, Phases, and Sample JSON

**Target stack**

- Frontend: Angular 21 + Angular Material
- Backend: Java 21 + Spring Boot + Maven
- Database: PostgreSQL
- Backend base package: `com.cdac.egov.integration.gepnic`
- Application class: `GePNICServiceApplication.java`

**Purpose:** Build GePNIC as a separately deployable integration service for exchanging tender, corrigendum, document, metadata, and AOC information between WAMIS and GePNIC. The service must map JSON in both directions, apply the official GePNIC envelope and cryptographic protocol, communicate with configured GePNIC endpoints, and maintain auditable transaction status.

> **Protocol warning:** The envelope and JSON below are illustrative development examples, not a claim that every GePNIC endpoint uses exactly this schema. Confirm endpoint names, required fields, signing rules, algorithms, certificate/key handling, response formats, and operation semantics against the current official GePNIC integration specification before implementing production calls. Do not invent or silently alter protocol requirements.

---

## 1. Core architecture and constraints

### 1.1 Architecture

```text
Angular 21 / Angular Material
          |
          | HTTPS, WAMIS internal API JSON
          v
GePNIC Spring Boot Integration Service
          |
          +-- Forms and API validation
          +-- WAMIS <-> GePNIC JSON mapping
          +-- Tender/document/AOC integration services
          +-- JSON serialization and validation
          +-- Encryption / decryption / digital signatures
          +-- GePNIC HTTP client
          +-- Transaction history and audit metadata
          |
          +------------------+
          |                  |
          v                  v
      PostgreSQL        GePNIC APIs
```

### 1.2 Mandatory implementation constraints

1. **Separate service:** The GePNIC integration backend is a separately deployable Spring Boot service, with its own configuration, build, tests, and deployment files.
2. **Backend package:** Use `com.cdac.egov.integration.gepnic`.
3. **Centralized constants:** Create a backend `GePNICConstants.java` and frontend `gepnic.constants.ts`. Use externalized environment properties for URLs, timeouts, certificate locations, credentials, and feature flags. Constants should centralize stable names/defaults; environment-dependent and secret values must not be hard-coded.
4. **Use `forms`, not `dto`:** Name request/response data carriers `*Form` and place them in `forms/`. Do not create a `dto` package. Keep JPA entities separate and never expose entities directly from controllers.
5. **Cohesive services:** Group tender processing under `service/tender/` and cryptographic operations under `service/encryption/`. The tender workflow orchestrates the encryption service, JSON mapper, HTTP client, and transaction logging; cryptographic code remains a separately testable security boundary.
6. **Frontend component files:** Each Angular component directory contains only its `.ts` and `.html` component files. Do not create per-component CSS or SCSS files.
7. **Single global stylesheet:** Put shared styling in `src/styles.css`, including Material theme, typography, fields, buttons, tables, dialog sizing, spacing, and responsive rules.
8. **Angular Material only:** Use Angular Material components for forms, navigation, buttons, tables, pagination, tabs, progress, chips, tooltips, snackbars, and dialogs. Create reusable shared components and consistent styling.
9. **Notifications and confirmations:** Use a shared `SnackbarService` backed by `MatSnackBar` for success, error, warning, and information. Use `MatDialog` confirmation dialogs for deletion, certificate/endpoint changes, duplicate-sensitive retries, overwrites, and other critical actions.
10. **Details and audits:** Show transaction details and audit history in Material dialogs, using table-format presentation.
11. **Input help:** Every input must provide a concise `matTooltip` on hover and keyboard focus. Tooltip text explains the field's meaning, format, and constraints.
12. **Development JSON diagnostics:** In development, show synthetic sample JSON at each stage of WAMIS→GePNIC and GePNIC→WAMIS. Production diagnostics must be disabled server-side and in the UI.
13. **Security:** Never display or log session keys, private keys, passwords, or unredacted secrets. Use synthetic data by default. Debugging must not bypass validation, signature checks, encryption, authorization, or audit controls.
14. **Testing:** Unit-test mapping, JSON validation, cryptography, and response parsing; integration-test API calls, persistence, error handling, and retries.
15. **Protocol compliance:** Confirm the precise GePNIC API endpoints, request fields, response fields, certificate requirements, algorithms, signature input, padding, and key-ID rules from the official specification.

---

## 2. Screen count and screen list

Build **10 screens** for the complete module. The JSON preview/debugging screen is a dedicated screen, while reusable dialogs/components are not counted as screens.

|   # | Screen                                   | Primary purpose                                                          | Key UI elements                                                                    |
| --: | ---------------------------------------- | ------------------------------------------------------------------------ | ---------------------------------------------------------------------------------- |
|   1 | GePNIC Integration Dashboard             | Summary of integration activity                                          | KPI cards, recent transactions, operation status, failures                         |
|   2 | Pre-Tender Configuration & Office Lookup | Select/resolve office and configure pre-tender values                    | Office lookup, Office Head, public-key/certificate reference, configuration status |
|   3 | Tender JSON Push                         | Validate and send WAMIS tender to GePNIC                                 | Tender reference, payload preview, validation, submit confirmation                 |
|   4 | Document Upload & Status                 | Upload tender files and inspect status                                   | File selection, upload progress, document table, status checks                     |
|   5 | Tender & Corrigendum Retrieval           | Pull tender and corrigendum information                                  | Search filters, date range, result table, details dialog                           |
|   6 | AOC Management                           | Retrieve Award of Contract data and documents                            | Search filters, AOC results, document download/status                              |
|   7 | Metadata & Document Status Check         | Check metadata/document synchronization                                  | Tender/document references, check actions, status table                            |
|   8 | JSON Preview & Validation                | Inspect safe JSON transformation stages in development                   | WAMIS JSON, mapped GePNIC JSON, envelope, response JSON, validation results        |
|   9 | Integration History & Transaction Logs   | Search and inspect all integration operations                            | Filters, status, timestamps, correlation ID, error summary, retry where safe       |
|  10 | Integration Configuration & Certificates | Manage environment-level integration settings and certificate references | Endpoint config, timeout, environment, certificate metadata, connectivity test     |

### Shared UI components (not separate screens)

- Confirmation dialog
- Transaction details dialog
- Audit details dialog
- JSON viewer / JSON comparison viewer
- Validation result panel
- Office lookup component
- Document status table
- Integration error panel
- Snackbar notification service

---

## 3. Frontend folder structure

```text
wamis-frontend/
└── src/
    ├── styles.css
    └── app/
        ├── core/
        │   ├── guards/
        │   ├── interceptors/
        │   └── services/
        ├── layout/
        │   ├── main-layout/
        │   ├── header/
        │   └── sidebar/
        ├── shared/
        │   ├── components/
        │   │   ├── confirm-dialog/
        │   │   ├── details-dialog/
        │   │   ├── audit-dialog/
        │   │   ├── json-viewer/
        │   │   ├── json-diff-viewer/
        │   │   └── status-chip/
        │   └── services/
        │       ├── snackbar.service.ts
        │       └── dialog.service.ts
        └── features/
            └── gepnic/
                ├── constants/
                │   └── gepnic.constants.ts
                ├── pages/
                │   ├── dashboard/
                │   ├── pre-tender-configuration/
                │   ├── tender-push/
                │   ├── document-management/
                │   ├── tender-corrigendum-pull/
                │   ├── aoc-management/
                │   ├── metadata-status/
                │   ├── json-preview/
                │   ├── transaction-history/
                │   └── integration-settings/
                ├── components/
                │   ├── tender-form/
                │   ├── office-lookup/
                │   ├── request-response-viewer/
                │   ├── validation-results/
                │   └── transaction-table/
                ├── services/
                │   ├── gepnic-api.service.ts
                │   ├── tender-integration.service.ts
                │   ├── encryption-debug.service.ts
                │   ├── document.service.ts
                │   ├── aoc.service.ts
                │   └── transaction.service.ts
                ├── forms/
                ├── models/
                ├── validators/
                └── gepnic.routes.ts
```

For each component folder, create only the component `.ts` and `.html` files. Shared visual rules belong in `src/styles.css`. Other non-component TypeScript files (services, models, constants, route definitions) are allowed.

### Frontend UI rules

- Use `mat-form-field`, `matInput`, `mat-select`, `mat-datepicker`, `mat-table`, `mat-paginator`, `mat-sort`, `mat-toolbar`, `mat-sidenav`, `mat-tabs`, `mat-chip`, `mat-tooltip`, `MatSnackBar`, and `MatDialog` as appropriate.
- Use a shared set of classes for page containers, page headers, form grids, action rows, tables, status chips, dialogs, and empty/loading/error states.
- Do not add component-specific CSS/SCSS.
- Every input should have a helpful tooltip, with consistent positioning and delay.
- Critical operations require a confirmation dialog.
- Details and audit dialogs use tables.
- Show errors without leaking raw secrets or sensitive request data.

---

## 4. Backend folder structure

```text
wamis-gepnic-service/
├── src/
│   ├── main/
│   │   ├── java/com/cdac/egov/integration/gepnic/
│   │   │   ├── GePNICServiceApplication.java
│   │   │   ├── constants/
│   │   │   │   └── GePNICConstants.java
│   │   │   ├── config/
│   │   │   │   ├── GePNICProperties.java
│   │   │   │   ├── HttpClientConfig.java
│   │   │   │   └── JacksonConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── DashboardController.java
│   │   │   │   ├── OfficeLookupController.java
│   │   │   │   ├── TenderIntegrationController.java
│   │   │   │   ├── DocumentIntegrationController.java
│   │   │   │   ├── AocIntegrationController.java
│   │   │   │   ├── MetadataIntegrationController.java
│   │   │   │   ├── JsonDiagnosticsController.java
│   │   │   │   ├── TransactionController.java
│   │   │   │   └── ConfigurationController.java
│   │   │   ├── forms/
│   │   │   │   ├── common/
│   │   │   │   │   └── IntegrationResultForm.java
│   │   │   │   ├── tender/
│   │   │   │   │   ├── TenderPushForm.java
│   │   │   │   │   ├── TenderPullForm.java
│   │   │   │   │   └── TenderResponseForm.java
│   │   │   │   ├── encryption/
│   │   │   │   │   ├── EncryptedEnvelopeForm.java
│   │   │   │   │   ├── EncryptionResultForm.java
│   │   │   │   │   └── DecryptionResultForm.java
│   │   │   │   ├── document/
│   │   │   │   ├── aoc/
│   │   │   │   └── configuration/
│   │   │   ├── service/
│   │   │   │   ├── tender/
│   │   │   │   │   ├── TenderIntegrationService.java
│   │   │   │   │   ├── TenderPushService.java
│   │   │   │   │   ├── TenderPullService.java
│   │   │   │   │   ├── TenderPayloadMapper.java
│   │   │   │   │   └── TenderValidationService.java
│   │   │   │   ├── encryption/
│   │   │   │   │   ├── GePNICEncryptionService.java
│   │   │   │   │   ├── GePNICDecryptionService.java
│   │   │   │   │   ├── DigitalSignatureService.java
│   │   │   │   │   ├── CertificateService.java
│   │   │   │   │   └── SessionKeyService.java
│   │   │   │   ├── document/
│   │   │   │   │   └── DocumentIntegrationService.java
│   │   │   │   ├── aoc/
│   │   │   │   │   └── AocIntegrationService.java
│   │   │   │   ├── metadata/
│   │   │   │   │   └── MetadataIntegrationService.java
│   │   │   │   ├── json/
│   │   │   │   │   ├── GePNICJsonBuilder.java
│   │   │   │   │   ├── GePNICJsonParser.java
│   │   │   │   │   └── GePNICJsonValidationService.java
│   │   │   │   └── integration/
│   │   │   │       ├── GePNICHttpClient.java
│   │   │   │       ├── GePNICRequestService.java
│   │   │   │       ├── GePNICResponseService.java
│   │   │   │       └── IntegrationOrchestrator.java
│   │   │   ├── mapper/
│   │   │   │   ├── WamisToGePNICMapper.java
│   │   │   │   └── GePNICToWamisMapper.java
│   │   │   ├── entity/
│   │   │   │   ├── IntegrationTransaction.java
│   │   │   │   ├── DocumentTransaction.java
│   │   │   │   └── IntegrationConfiguration.java
│   │   │   ├── repository/
│   │   │   ├── enums/
│   │   │   │   ├── IntegrationStatus.java
│   │   │   │   └── IntegrationOperation.java
│   │   │   ├── security/
│   │   │   │   ├── SecretProvider.java
│   │   │   │   └── CertificateProvider.java
│   │   │   ├── exception/
│   │   │   │   ├── GePNICException.java
│   │   │   │   ├── GePNICCryptoException.java
│   │   │   │   ├── GePNICApiException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── scheduler/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-test.yml
│   │       ├── application-prod.yml
│   │       └── db/migration/
│   └── test/java/com/cdac/egov/integration/gepnic/
│       ├── service/
│       ├── mapper/
│       ├── encryption/
│       ├── json/
│       └── integration/
├── pom.xml
├── Dockerfile
├── compose.yaml
└── README.md
```

### Backend responsibilities

- **Controllers:** validate input forms, authorize the caller, invoke application services, and return normalized results.
- **Tender services:** orchestrate mapping, validation, encryption, sending, response processing, and transaction recording.
- **Encryption services:** own session-key generation, encryption/decryption, signing, signature verification, and certificate/thumbprint checks as specified by GePNIC.
- **JSON services:** serialize, parse, validate, and construct the envelope.
- **HTTP client:** send requests to configured GePNIC endpoints, handle timeouts, transport errors, and response codes.
- **Mappers:** convert WAMIS forms/models to GePNIC payloads and back.
- **Persistence:** store transaction metadata, statuses, retry information, and synchronization checkpoints.
- **Configuration:** bind environment-specific properties without hard-coding endpoint URLs or secrets.
- **Diagnostics:** return safe, synthetic or sanitized debug stages only when development mode is enabled.

---

## 5. API endpoint proposal

These are **internal WAMIS-to-integration-service endpoints**, not the external GePNIC URLs. Adjust route names to the API contract agreed by WAMIS and the integration service.

| Method | Internal endpoint                     | Purpose                                                 |
| ------ | ------------------------------------- | ------------------------------------------------------- |
| GET    | `/api/gepnic/dashboard`               | Dashboard summary                                       |
| GET    | `/api/gepnic/offices`                 | Lookup permitted offices                                |
| POST   | `/api/gepnic/tenders/push`            | Validate, map, encrypt, sign, and push tender           |
| POST   | `/api/gepnic/tenders/pull`            | Retrieve tender information                             |
| POST   | `/api/gepnic/corrigendums/pull`       | Retrieve corrigendum information                        |
| POST   | `/api/gepnic/documents/upload`        | Upload tender document                                  |
| POST   | `/api/gepnic/documents/status`        | Check document status                                   |
| POST   | `/api/gepnic/metadata/check`          | Check metadata status                                   |
| POST   | `/api/gepnic/aoc/pull`                | Retrieve AOC information                                |
| POST   | `/api/gepnic/aoc/documents/download`  | Request/download AOC documents                          |
| GET    | `/api/gepnic/transactions`            | Search transaction history                              |
| GET    | `/api/gepnic/transactions/{id}`       | Transaction details                                     |
| GET    | `/api/gepnic/transactions/{id}/audit` | Audit history                                           |
| POST   | `/api/gepnic/transactions/{id}/retry` | Retry an eligible operation with idempotency safeguards |
| GET    | `/api/gepnic/configuration`           | Read authorized, non-secret configuration               |
| PUT    | `/api/gepnic/configuration`           | Update authorized integration configuration             |
| POST   | `/api/gepnic/diagnostics/sample-flow` | Development-only synthetic JSON flow                    |

Do not expose an endpoint that accepts arbitrary plaintext and returns production encryption material. Debug endpoints must be disabled server-side in production.

---

## 6. Sample JSON — WAMIS to GePNIC

The following is a **synthetic illustrative payload**. It demonstrates the mapping concept only; replace field names and required fields with the actual GePNIC tender schema.

### 6.1 Example WAMIS tender JSON before mapping

```json
{
  "wamisTenderId": "WAMIS-TND-2026-000123",
  "officeId": "OFF-001",
  "tenderTitle": "Road Improvement Work",
  "estimatedCost": 2500000.0,
  "tenderFee": 1500.0,
  "emdAmount": 50000.0,
  "publishDate": "2026-11-01T10:00:00+05:30",
  "submissionEndDate": "2026-11-20T17:00:00+05:30",
  "workDescription": "Improvement of internal road",
  "documents": [
    {
      "documentId": "DOC-101",
      "fileName": "tender-notice.pdf",
      "contentType": "application/pdf"
    }
  ]
}
```

### 6.2 Example mapped GePNIC tender JSON before encryption

```json
{
  "tenderReference": "WAMIS-TND-2026-000123",
  "organizationOfficeCode": "OFF-001",
  "title": "Road Improvement Work",
  "estimatedTenderValue": 2500000.0,
  "tenderDocumentFee": 1500.0,
  "earnestMoneyDeposit": 50000.0,
  "publicationDate": "2026-11-01T10:00:00+05:30",
  "bidSubmissionEndDate": "2026-11-20T17:00:00+05:30",
  "description": "Improvement of internal road",
  "documentReferences": [
    {
      "documentReference": "DOC-101",
      "fileName": "tender-notice.pdf",
      "mimeType": "application/pdf"
    }
  ]
}
```

**Mapping rules to implement:** preserve the WAMIS tender reference for correlation; validate dates and amounts; reject missing required fields; map document metadata to the exact schema; and do not assume these illustrative field names match GePNIC's production schema.

### 6.3 Example encrypted request envelope

```json
{
  "uniqueUserId": "WAMIS-REGISTERED-USER-ID",
  "encryptData": "<BASE64_ENCRYPTED_JSON>",
  "signData": "<BASE64_DIGITAL_SIGNATURE>",
  "encryptKey": "<BASE64_ENCRYPTED_SESSION_KEY>",
  "keyId": "<GEPNIC_CERTIFICATE_THUMBPRINT>"
}
```

These field names are based on the supplied dynamic encryption document. Values above are placeholders, not valid cryptographic output. Generate all cryptographic values in the backend.

### 6.4 Example GePNIC HTTP response envelope

```json
{
  "uniqueUserId": "WAMIS-REGISTERED-USER-ID",
  "encryptData": "<BASE64_ENCRYPTED_RESPONSE_JSON>",
  "signData": "<BASE64_RESPONSE_SIGNATURE>",
  "encryptKey": "<BASE64_ENCRYPTED_SESSION_KEY>",
  "keyId": "<GEPNIC_CERTIFICATE_THUMBPRINT>"
}
```

Treat the response envelope as illustrative until the exact response contract is confirmed. Verify the signature against the exact protocol-defined signed bytes and validate certificate/key identity before trusting the decrypted response.

### 6.5 Example decrypted GePNIC response JSON

```json
{
  "responseCode": 1,
  "responseMessage": {
    "tenderReference": "WAMIS-TND-2026-000123",
    "status": "RECEIVED",
    "message": "Tender request received for processing"
  }
}
```

This demonstrates a possible normalized response only. Confirm whether GePNIC uses numeric or string response codes, and whether `responseMessage` is an object or a JSON-encoded string.

---

## 7. Sample JSON — GePNIC to WAMIS

### 7.1 Example encrypted response envelope received by the integration service

```json
{
  "uniqueUserId": "WAMIS-REGISTERED-USER-ID",
  "encryptData": "<BASE64_ENCRYPTED_GEPNIC_JSON>",
  "signData": "<BASE64_SIGNATURE>",
  "encryptKey": "<BASE64_ENCRYPTED_SESSION_KEY>",
  "keyId": "<GEPNIC_CERTIFICATE_THUMBPRINT>"
}
```

### 7.2 Example decrypted GePNIC tender/corrigendum result

```json
{
  "tenderReference": "WAMIS-TND-2026-000123",
  "tenderStatus": "PUBLISHED",
  "lastUpdated": "2026-11-03T12:15:00+05:30",
  "corrigendums": [
    {
      "corrigendumReference": "COR-001",
      "publishedDate": "2026-11-03T12:00:00+05:30",
      "description": "Submission deadline revised"
    }
  ]
}
```

This is sample application-level data, not a verified GePNIC schema.

### 7.3 Example mapped WAMIS response

```json
{
  "sourceSystem": "GePNIC",
  "wamisTenderId": "WAMIS-TND-2026-000123",
  "externalTenderReference": "WAMIS-TND-2026-000123",
  "status": "PUBLISHED",
  "lastSynchronizedAt": "2026-11-03T12:15:00+05:30",
  "corrigendumCount": 1,
  "corrigendums": [
    {
      "externalReference": "COR-001",
      "publishedAt": "2026-11-03T12:00:00+05:30",
      "summary": "Submission deadline revised"
    }
  ]
}
```

The integration service should map verified external data into WAMIS's agreed internal contract, validate it, and persist synchronization metadata. Do not overwrite WAMIS business records without explicit rules for conflict resolution and authorization.

### 7.4 Example internal integration result

```json
{
  "transactionId": "TXN-2026-000001",
  "operation": "TENDER_PULL",
  "status": "SUCCESS",
  "sourceSystem": "GePNIC",
  "correlationId": "WAMIS-TND-2026-000123",
  "receivedAt": "2026-11-03T12:15:00+05:30",
  "recordsProcessed": 1,
  "message": "Tender data retrieved and validated"
}
```

---

## 8. Development JSON diagnostics

Build Screen 8 to show the stages as separate Material tabs or panels.

### WAMIS → GePNIC stages

1. Synthetic WAMIS source JSON
2. Mapped GePNIC JSON
3. Validation result
4. Encrypted and signed request envelope
5. Sanitized HTTP response envelope
6. Verified/decrypted response JSON, only if safe and authorized
7. Final normalized result

### GePNIC → WAMIS stages

1. Synthetic GePNIC response envelope
2. Envelope validation result
3. Signature verification result
4. Session-key decryption result (boolean/status only; never show the key)
5. Payload decryption result
6. Parsed GePNIC JSON
7. Mapped WAMIS JSON
8. Persistence/synchronization result

For production, disable the diagnostics controller and do not return real decrypted payloads through debug endpoints. Display transaction metadata and safe error information instead.

---

## 9. Phased implementation plan

### Phase 0 — Repository and protocol discovery

**Tasks**

- Inspect the existing WAMIS frontend/backend, current Material theme, routing, authentication/authorization, API conventions, database patterns, and audit approach.
- Obtain the authoritative GePNIC integration specification and confirm the demo/production environment.
- Confirm every endpoint, method, request/response schema, field requirement, certificate format, key identifier, signing input, encryption algorithm, padding, and error code.
- Agree on the internal WAMIS↔integration-service API contract.
- Identify which data comes from WAMIS database versus GePNIC.

**Deliverables**

- Protocol endpoint matrix
- Field-mapping document
- Security design
- Agreed API contract
- Initial acceptance criteria

**Exit criteria:** No production cryptographic behavior is based solely on assumptions or illustrative JSON.

### Phase 1 — Project scaffolding and configuration

**Tasks**

- Create the Angular feature structure and separately deployable Spring Boot project.
- Add centralized constants and typed enums.
- Add environment-specific configuration profiles.
- Configure PostgreSQL, database migrations, Jackson, validation, and HTTP client timeouts.
- Establish structured logging, correlation IDs, and error response format.
- Configure the existing Angular Material theme through `styles.css`.

**Deliverables**

- Builds run locally
- Health endpoint
- Database connection
- Initial route and navigation
- Configuration documentation

**Exit criteria:** Frontend and backend build and start reliably without real GePNIC calls.

### Phase 2 — Forms, validation, mapping, and sample JSON

**Tasks**

- Implement `TenderPushForm`, pull forms, document forms, response forms, and envelope forms.
- Implement WAMIS→GePNIC and GePNIC→WAMIS mapping.
- Add JSON parsing and schema/business validation.
- Create synthetic request/response fixtures for success, rejection, malformed JSON, missing fields, and unexpected response codes.
- Build the JSON Preview & Validation screen.

**Deliverables**

- Mapping services
- Validation services
- Sample JSON fixtures
- Unit tests for mapping and validation

**Exit criteria:** Sample WAMIS JSON maps to the agreed GePNIC schema and back without data loss for mapped fields.

### Phase 3 — Cryptography and certificate management

**Tasks**

- Implement session-key generation.
- Implement the exact GePNIC-approved payload encryption.
- Implement request signing and response signature verification.
- Implement session-key encryption/decryption.
- Implement certificate loading, validity checks, key-ID/thumbprint checks, and safe error handling.
- Protect keystore passwords and private keys using the approved secret-management approach.
- Add known-answer/interoperability tests against official examples or the approved test environment.

**Deliverables**

- Encryption and decryption services
- Signature services
- Certificate provider
- Cryptographic unit tests

**Exit criteria:** Cryptographic behavior matches the authoritative GePNIC protocol and approved test vectors. Do not use sample placeholder values as tests of real interoperability.

### Phase 4 — GePNIC HTTP client and tender push

**Tasks**

- Implement the HTTP client with TLS, timeouts, bounded retries where safe, and transport-error handling.
- Construct the official envelope.
- Send a tender payload to the configured GePNIC test endpoint.
- Verify and process the response.
- Record transaction status and correlation ID.
- Add idempotency/duplicate-submission safeguards.

**Deliverables**

- Tender Push screen
- Tender integration service
- Request/response processing
- Transaction records
- Error and retry handling

**Exit criteria:** A controlled test tender can be submitted and its response correlated to the WAMIS request.

### Phase 5 — Document upload and status

**Tasks**

- Implement file metadata validation and allowed size/type limits.
- Implement document upload according to the GePNIC file-upload protocol.
- Implement document status and upload-check operations.
- Track document references and transaction results.
- Add confirmation for duplicate uploads/retries where necessary.

**Deliverables**

- Document Upload & Status screen
- Document service/client
- Document transaction persistence
- File-related tests

**Exit criteria:** Upload and status operations work against the approved test environment and report recoverable failures clearly.

### Phase 6 — Tender, corrigendum, AOC, and metadata retrieval

**Tasks**

- Implement tender pull.
- Implement corrigendum pull.
- Implement AOC retrieval and document download.
- Implement metadata and document status checks.
- Parse and verify incoming responses.
- Map external data to WAMIS models and persist synchronization checkpoints.
- Define conflict resolution and update rules.

**Deliverables**

- Tender & Corrigendum Retrieval screen
- AOC Management screen
- Metadata & Document Status screen
- Synchronization logic and tests

**Exit criteria:** Each retrieval operation validates, maps, and records results without silently overwriting unrelated WAMIS data.

### Phase 7 — Transaction history, audit, and dashboard

**Tasks**

- Build dashboard queries and summary cards.
- Implement paginated transaction search.
- Implement transaction-details and audit dialogs with tables.
- Record operation, timestamps, outcome, correlation ID, and sanitized error details.
- Implement authorization and safe retry actions.

**Deliverables**

- Dashboard
- Integration History screen
- Transaction detail dialog
- Audit dialog

**Exit criteria:** Users can trace an operation from the WAMIS reference through request, response, and final processing status.

### Phase 8 — Material consistency and interaction standards

**Tasks**

- Apply global design tokens and shared classes in `styles.css`.
- Standardize Material form fields, buttons, tables, pagination, chips, dialogs, and loading states.
- Add tooltips to every input.
- Implement shared snackbar and confirmation-dialog services.
- Check responsive layout and keyboard accessibility.

**Deliverables**

- Consistent UI across all 10 screens
- Reusable dialog and notification components
- UI verification checklist

**Exit criteria:** Screens use the common Material design and all critical actions have suitable feedback and confirmation.

### Phase 9 — Security hardening and diagnostics

**Tasks**

- Add development-only synthetic JSON flow diagnostics.
- Ensure diagnostics endpoints are disabled server-side in production.
- Mask sensitive values and remove secrets from logs.
- Test access controls, invalid signatures, invalid certificates, malformed envelopes, replay/duplicate submissions, timeouts, and unsafe retries.
- Review secret storage, retention, and audit access.

**Deliverables**

- Development JSON diagnostics
- Security test report
- Configuration and deployment checklist

**Exit criteria:** Production cannot invoke debug-only APIs, and no tested path leaks private keys, passwords, or session keys.

### Phase 10 — End-to-end testing and deployment

**Tasks**

- Run unit, integration, and end-to-end tests.
- Test valid responses, rejection responses, malformed payloads, network errors, timeouts, retries, and duplicate operations.
- Test PostgreSQL migrations and backup/recovery expectations.
- Configure deployment, health checks, logs, metrics, environment variables, and certificate rotation procedures.
- Prepare API and operational documentation.

**Deliverables**

- Test report
- Deployment artifacts
- Runbook and troubleshooting guide
- API documentation
- Release checklist

**Exit criteria:** The service is deployable, observable, secured, and accepted against the approved integration scenarios.

---

## 10. Acceptance checklist

### Architecture

- [ ] Separate Spring Boot integration service
- [ ] Correct package `com.cdac.egov.integration.gepnic`
- [ ] `GePNICServiceApplication.java`
- [ ] No `dto` package; API data carriers use `forms`
- [ ] Centralized backend and frontend constants
- [ ] Externalized environment-specific settings

### Frontend

- [ ] All 10 screens implemented
- [ ] Angular Material used consistently
- [ ] Component directories contain only `.ts` and `.html`
- [ ] One shared `styles.css`
- [ ] Snackbar success/error/warning/info
- [ ] Confirmation dialogs for critical operations
- [ ] Transaction and audit details displayed in table-format dialogs
- [ ] Every input has hover/focus help

### JSON and security

- [ ] WAMIS→GePNIC mapping and validation
- [ ] GePNIC→WAMIS parsing and mapping
- [ ] Official encrypted envelope support
- [ ] Signature verification and certificate validation
- [ ] Synthetic before/after JSON diagnostics in development
- [ ] No secrets in UI or logs
- [ ] Diagnostics disabled server-side in production

### Persistence and operations

- [ ] PostgreSQL migrations
- [ ] Transaction history and correlation IDs
- [ ] Sanitized error tracking
- [ ] Safe retry/idempotency controls
- [ ] Unit and integration tests
- [ ] Deployment and troubleshooting documentation

## 11. Implementation order summary

Build in this order:

1. Protocol discovery and API contract
2. Project structure and configuration
3. Forms, mapping, validation, and synthetic JSON
4. Cryptography and certificate handling
5. HTTP client and tender push
6. Document upload and status
7. Tender/corrigendum/AOC/metadata retrieval
8. Dashboard, history, and audit
9. UI consistency and development diagnostics
10. Security testing, end-to-end validation, and deployment

**Recommended delivery strategy:** First complete one vertical slice—sample WAMIS JSON → map → validate → encrypt/sign → send to GePNIC test endpoint → verify/decrypt response → map result → save transaction. Then reuse that pipeline for documents, retrieval, corrigendums, and AOC.

# NIC Specification Alignment Addendum

This addendum aligns the build plan with the supplied 29-page **NIC-GePNIC PreTender Integration Service Document**. The URLs below are demo endpoints in that document; do not assume they are production URLs. Confirm the current contract and cryptographic details with NIC before deployment.

## 1. NIC endpoints documented in the source

| Capability                            | Endpoint                                                                                                      |
| ------------------------------------- | ------------------------------------------------------------------------------------------------------------- |
| Tender JSON push                      | `https://demoeproc.nic.in/nicgep_external_webservice/push/postTenderJsonDetailsService`                       |
| Tender document upload                | `https://demoeproc.nic.in/nicgep_external_webservice/push/postTenderFileUploadDetailsService`                 |
| Tender retrieval by publish date      | `https://demoeproc.nic.in/nicgep_external_webservice/pull/getTenderInfoFromPreTenderbyPublishDateService`     |
| Corrigendum retrieval by publish date | `https://demoeproc.nic.in/nicgep_external_webservice/pull/getPreTenderCorrigendumInfosByPublishedDateService` |
| AOC retrieval by created date         | `https://demoeproc.nic.in/nicgep_external_webservice/pull/getAocInfoFromPreTenderbyCreatedDateService`        |
| AOC file download                     | `https://demoeproc.nic.in/nicgep_external_webservice/pull/postAOCFileDownloadDetailsService`                  |

The supplied document's metadata/document-check services need exact endpoint names and request formats confirmed from the relevant pages before constants or clients are finalized. Do not invent endpoint paths.

## 2. NIC tender JSON structure

The tender push example in the NIC document uses `inputData`, with `uniqueRefNo` and `inputString.PRETENDER.TENDER`. The `TENDER` property is an array. Its entries include `TENDER_BASIC_DETAILS`, `TENDER_WORKITEM`, and `TENDER_DOCS_DETAILS` sections.

The following is a **shortened synthetic illustration** that preserves the documented hierarchy and field-name style. It is not a complete production schema; implement all required fields from the applicable NIC schema.

```json
{
  "inputData": {
    "uniqueRefNo": "WAMIS-UNIQUE-REFERENCE",
    "inputString": {
      "PRETENDER": {
        "TENDER": [
          {
            "TENDER_BASIC_DETAILS": {
              "BASICINFO": {
                "UNIQUE_FILE_NO": "WAMIS-FILE-UNIQUE-NO",
                "ACTUAL_ORGID": 2297,
                "T_REF_NO": "WAMIS-TENDER-REFERENCE",
                "T_TYPE": "Open Tender",
                "T_FORM_CONTRACT": "Supply",
                "T_NO_OF_COVERS": 2,
                "T_TENDER_CATEGORY": "Works",
                "T_ALLOW_AUTOMATIC_EVALUATION": false,
                "T_ALLOW_MULTI_CURRENCY_FOR_BOQ": true,
                "CREATOR_NAME": "Sample Creator",
                "CREATOR_REFERENCE": "WAMIS"
              },
              "MULTI_CURRENCY_FOR_BOQ": {
                "NAME": ["USD", "JPY"]
              },
              "LIMITED_BIDDERS": {
                "BIDDER_LOGIN_ID": ["bidder1@example.invalid"]
              },
              "BOQINFO": null,
              "COVERINFO": {
                "COVER": [
                  {
                    "T_COVER_NO": 1,
                    "T_DOC_DESC": "Fee Document",
                    "T_DOC_TYPE": ".pdf"
                  },
                  {
                    "T_COVER_NO": 2,
                    "T_DOC_DESC": "Financial BOQ",
                    "T_DOC_TYPE": ".xls"
                  }
                ]
              }
            },
            "TENDER_WORKITEM": {
              "WORKITEM": {
                "T_WORKITEM_TITLE": "Sample road improvement",
                "T_WORKITEM_DESC": "Sample work description",
                "T_WORKITEM_PRE_QUAL": "NA",
                "T_WORKITEM_PROD_CAT": "Civil Works",
                "T_WORKITEM_PROD_SUB_CAT": "Road",
                "T_WORKITEM_CONTRACT_TYPE": "Tender",
                "T_WORKITEM_TENDER_VALUE": 469298,
                "T_BID_VALIDITY_DAYS": 60,
                "T_DELIVERY_PERIOD_DAYS": 0,
                "T_PRE_BID_MEETING": false,
                "T_LOCATION_DETAILS": "Sample location",
                "T_PINCODE": 411001,
                "T_INVITING_OFFICER": "Sample Executive Engineer",
                "T_INVITING_OFF_ADDRESS": "Sample office address"
              },
              "TENDER_FEE_DETAILS": {
                "T_FEE": 500,
                "T_EMD_FEE": 9386,
                "T_ALLOW_MULTI_CURRENCY_FOR_FEE": false,
                "MULTI_CURRENCY_FOR_FEE": {
                  "CURRENCY": []
                }
              },
              "TENDER_CRITICAL_DATES": {
                "T_PUBLISHING_DATE": "2026-11-01T10:30:00",
                "T_DOC_DOWNLOAD_START_DATE": "2026-11-01T11:00:00",
                "T_SEEK_CLARIFICATION_START_DATE": "2026-11-02T10:15:00",
                "T_PRE_BID_MEETING_DATE": null,
                "T_SEEK_CLARIFICATION_END_DATE": "2026-11-05T10:15:00",
                "T_BIDSUB_START_DATE": "2026-11-10T10:00:00",
                "T_BIDSUB_END_DATE": "2026-11-20T17:00:00",
                "T_BID_OPEN_DATE": "2026-11-21T10:00:00"
              }
            },
            "TENDER_DOCS_DETAILS": {
              "BASIC_DOC": {
                "DOC": [
                  {
                    "T_DOC_CODE": "NITDOC",
                    "T_DOC_NAME": "tender-notice.pdf",
                    "T_DOC_DESCRIPTION": "Notice inviting tender",
                    "T_DOC_MIME_TYPE": ".pdf",
                    "T_DOC_SIZE": 12000,
                    "T_DOC_PRIORITY": 1,
                    "T_DOC_UNIQUE_ID": 1
                  }
                ]
              },
              "WORKITEM_DOC": {
                "DOC": [
                  {
                    "T_DOC_CODE": "BOQDOC",
                    "T_DOC_NAME": "financial-boq.xls",
                    "T_DOC_DESCRIPTION": "Bill of quantities",
                    "T_DOC_MIME_TYPE": ".xls",
                    "T_DOC_SIZE": 24000,
                    "T_DOC_PRIORITY": 2,
                    "T_DOC_UNIQUE_ID": 2
                  }
                ]
              }
            }
          }
        ]
      }
    }
  }
}
```

The actual NIC example includes more fields and nested structures. Preserve exact key names, data types, array-vs-object structure, and null/optional-field behavior from the official schema when implementing.

## 3. Encrypted request envelope

The NIC document shows the following envelope fields:

```json
{
  "encryptData": "<BASE64_ENCRYPTED_PAYLOAD>",
  "signData": "<BASE64_SIGNATURE>",
  "uniqueUserId": "<NIC-ISSUED-EXTERNAL-USER-ID>",
  "encryptKey": "<BASE64_ENCRYPTED_SESSION_KEY>",
  "keyId": "<GEPNIC-CERTIFICATE-THUMBPRINT>"
}
```

These are placeholders, not valid cryptographic values. Generate them in the backend. Signature generation and verification must use the exact data bytes and rules required by NIC.

## 4. Document upload requirements

The source document says the upload uses metadata already submitted in `TENDER_DOCS_DETAILS`. Its multipart input fields are:

- `uniqueUserId`
- `uniqueFileNo`
- `docuniqueid`
- `file`

Supported formats shown in the source:

| Document category   | Formats stated     |
| ------------------- | ------------------ |
| NIT document        | PDF                |
| BOQ document        | XLS                |
| Additional document | PDF, XLS, RAR, JPG |

Implement upload as a separate multipart request. Validate the document category, extension, MIME type, size, and association with the tender metadata before transmission.

## 5. Sample pull requests and responses

### Tender pull by publish date

Endpoint: `getTenderInfoFromPreTenderbyPublishDateService`

```json
{
  "publishedDate": "20230519"
}
```

### Corrigendum pull by publish date

Endpoint: `getPreTenderCorrigendumInfosByPublishedDateService`

```json
{
  "publishedDate": "20230519"
}
```

### AOC pull by created date

Endpoint: `getAocInfoFromPreTenderbyCreatedDateService`

```json
{
  "createdDate": "20230818"
}
```

### Sample decrypted tender-push response shown by NIC

```json
{
  "responseCode": 1,
  "responseMessage": "{\"responseCode\":101,\"responseMessage\":\"Inserted Successfully\"}"
}
```

### Sample decrypted document-upload response shown by NIC

```json
{
  "responseCode": 1,
  "responseMessage": "{\"responseCode\":105,\"responseMessage\":\"Document Uploaded Successfully\"}"
}
```

The `responseMessage` values in these examples are JSON-encoded strings. Parse the outer response, then parse the nested message when appropriate. Do not assume every endpoint shares the same response codes or response shape.

## 6. AOC download field mapping

The source document maps the decrypted AOC fields to download inputs as follows:

| AOC response field | Download input field | Description   |
| ------------------ | -------------------- | ------------- |
| `t_DOC_TYPE`       | `docType`            | Document type |
| `t_DOC_NAME`       | `fileName`           | File name     |
| `t_BID_ID`         | `bId`                | Bid ID        |
| `t_DOC_CODE`       | `docCode`            | Document code |

AOC response fragment:

```json
{
  "t_DOC_TYPE": "BOQ",
  "t_BID_ID": 64929,
  "t_DOC_CODE": "BOQ64929",
  "t_DOC_NAME": "BOQ_29543.xls"
}
```

Corresponding download input:

```json
{
  "docType": "BOQ",
  "bId": "64929",
  "docCode": "BOQ64929",
  "fileName": "BOQ_29543.xls"
}
```

The document lists AOC document types including BOQ, LOA, NEGO, TIEBREAK, and BOQCHART. Confirm allowed types and whether each response is JSON, encrypted JSON, or file/binary content.

## 7. Updated screen-to-service mapping

Keep the planned **10 screens**:

1. Dashboard — internal transaction summaries.
2. Pre-Tender Configuration & Office Lookup — WAMIS office data and authorized configuration.
3. Tender JSON Push — `postTenderJsonDetailsService`.
4. Document Upload & Status — `postTenderFileUploadDetailsService` and status/check operations once their exact endpoint contracts are verified.
5. Tender & Corrigendum Retrieval — tender and corrigendum publish-date services.
6. AOC Management — AOC created-date service and AOC file-download service.
7. Metadata & Document Status Check — implement once exact NIC endpoint names and request/response formats are confirmed.
8. JSON Preview & Validation — synthetic development-only JSON transformation stages.
9. Integration History & Transaction Logs — internal transaction persistence.
10. Integration Configuration & Certificates — secure endpoint and certificate-reference configuration.

## 8. Updated build phases

1. **NIC contract extraction:** create an endpoint matrix with method, URL, content type, request fields, envelope, decrypted response, error codes, and sample cases.
2. **Configuration and constants:** centralize verified service paths and field names; externalize demo/production base URL, credentials, timeouts, certificate references, and feature flags.
3. **Tender schema/forms:** model `inputData → uniqueRefNo + inputString → PRETENDER → TENDER[]`, including nested basic details, work item, critical dates, fee details, and document details.
4. **Mapping and validation:** map WAMIS records to the exact NIC field names; test data types, required fields, arrays, nulls, and dates.
5. **Cryptography:** implement the NIC-approved session-key, encryption, signature, certificate, and thumbprint behavior; validate against authorized test vectors.
6. **Tender push:** call the tender JSON details service, parse both response layers, and persist safe transaction metadata.
7. **File upload:** link `uniqueFileNo` and `docuniqueid` to the submitted tender metadata and send the file via multipart form-data.
8. **Retrieval:** implement tender and corrigendum pull by publish date, and AOC pull by created date.
9. **AOC downloads:** map AOC response fields to `docType`, `fileName`, `bId`, and `docCode`.
10. **Metadata/document checks:** verify exact service paths and contracts from the full NIC document before implementation.
11. **Angular UI:** implement the 10 Material screens, one global `styles.css`, tooltips on every input, shared snackbars, confirmation dialogs, and table-based transaction/audit dialogs.
12. **Diagnostics and tests:** show synthetic before/after JSON stages in development; test invalid signatures, malformed JSON, rejected requests, missing files, duplicate operations, timeouts, and safe retries.
13. **Demo validation and deployment:** test against NIC's authorized demo environment, document configuration, then perform a separate production readiness review.

## 9. Source traceability

The supplied NIC document supports these details:

- Pages 2–5: integration overview and encryption/signature request flow.
- Pages 6–11: tender push endpoint, tender JSON hierarchy, encrypted envelope, and sample decrypted push response.
- Pages 12–13: document upload inputs, supported file types, endpoint, and sample response.
- Pages 14–15: tender pull by publish date.
- Page 16 onward: corrigendum pull by publish date.
- Pages 19–23: AOC pull by created date and AOC download field mapping.

This addendum uses the uploaded NIC document as the source of endpoint and schema details. Items not explicitly confirmed in the excerpts—especially metadata/document-check endpoints and exact cryptographic interoperability requirements—must be verified rather than guessed.
"""
