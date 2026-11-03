# WAMIS Backend Development Flow --- GePNIC Pre-Tender Adapter

**Backend package:** `in.gov.wamis`\
**Stack:** Java 21, Spring Boot, Spring Validation, Jackson, PostgreSQL\
**Initial scope:** WAMIS Pre-Tender → GePNIC, including tender JSON
push, tender document upload, response processing, and transaction/audit
history.\
**Future scope:** Tender/corrigendum retrieval, AOC retrieval/download,
then other e-procurement portals through separate adapters.

> **Protocol and environment note:** The GePNIC URLs and examples in
> this document are taken from the supplied *NIC-GePNIC PreTender
> Integration Service Document* and the WAMIS integration build plan.
> The URLs use `demoeproc.nic.in` and are demo endpoints, not a
> guarantee of current availability or production access. Confirm
> endpoint versions, required fields, certificate/key rules, request
> methods, response formats, and production URLs with NIC before
> enabling live calls. Values such as user IDs, organization IDs,
> certificates, signatures, and encrypted fields must come from
> authorized configuration and runtime processing---not copied from
> sample data.

------------------------------------------------------------------------

## 1. Goals and architectural rules

1.  Keep procurement business logic independent from any e-procurement
    portal.
2.  Keep GePNIC field names, API paths, payload envelopes, certificate
    rules, and response parsing inside `integration/gepnic`.
3.  Start with a small, complete Pre-Tender vertical slice before
    implementing every retrieval/AOC operation.
4.  Use the existing WAMIS `Work`, `Office`, document, and estimate
    records where available; do not create duplicate business entities
    without a requirement.
5.  Use `Form` for API request/response models as required by this
    project; do not create a `dto` package.
6.  Keep JPA entities internal. Never return entities directly from
    controllers.
7.  Centralize stable constants, but keep environment-dependent values
    in Spring configuration and secrets in a secret store or protected
    environment variables.
8.  Store operation status, correlation ID, external reference,
    attempts, and sanitized errors. Do not log private keys, passwords,
    session keys, or unredacted sensitive payloads.
9.  A timeout after submission may mean the portal processed the
    request. Mark the result as uncertain and reconcile before retrying.
10. Do not assume that multiple WAMIS works can be merged into one
    external tender. Batch submission and a combined tender are
    different business operations.

## 2. Target backend structure

``` text
src/main/java/in/gov/wamis/
├── WamisApplication.java
├── common/
│   ├── config/
│   ├── constants/
│   │   ├── CommonConstants.java
│   │   ├── ApiConstants.java
│   │   ├── ValidationConstants.java
│   │   └── IntegrationConstants.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   └── ErrorResponse.java
│   └── security/
│       └── [shared WAMIS authorization/security components]
├── procurement/
│   ├── pretender/
│   │   ├── PreTender.java
│   │   ├── PreTenderForm.java
│   │   ├── PreTenderRepository.java
│   │   ├── PreTenderService.java
│   │   ├── PreTenderController.java
│   │   └── PreTenderValidator.java
│   ├── posttender/
│   │   └── [future post-tender business workflow]
│   └── aoc/
│       └── [future award-of-contract business workflow]
├── integration/
│   ├── common/
│   │   ├── ProcurementPortal.java
│   │   ├── PortalRequest.java
│   │   ├── PortalResponse.java
│   │   └── IntegrationResult.java
│   ├── registry/
│   │   └── PortalAdapterRegistry.java
│   └── gepnic/
│       ├── GePNICAdapter.java
│       ├── config/
│       │   ├── GePNICConstants.java
│       │   ├── GePNICProperties.java
│       │   └── GePNICConfig.java
│       ├── client/
│       │   └── GePNICClient.java
│       ├── form/
│       │   ├── GePNICEnvelopeForm.java
│       │   ├── GePNICResponseForm.java
│       │   ├── TenderPushForm.java
│       │   ├── TenderPullForm.java
│       │   ├── CorrigendumPullForm.java
│       │   ├── AocPullForm.java
│       │   └── DocumentUploadForm.java
│       ├── mapper/
│       │   ├── WamisToGePNICMapper.java
│       │   └── GePNICToWamisMapper.java
│       ├── security/
│       │   ├── GePNICEncryptionService.java
│       │   ├── GePNICDecryptionService.java
│       │   ├── GePNICCertificateProvider.java
│       │   └── GePNICSignatureVerifier.java
│       └── response/
│           └── GePNICResponseParser.java
└── audit/
    ├── entity/
    │   └── IntegrationAuditEvent.java
    ├── repository/
    │   └── IntegrationAuditRepository.java
    └── service/
        └── IntegrationAuditService.java
```

**Implementation note:** Keep the structure aligned with the
repository's established conventions. Add a class only when it has a
clear responsibility. If transaction persistence is needed, add
`integration/transaction/` (entity, repository, service, status enum) or
put it in the existing audit/integration persistence module. Audit
events and integration job state are related but not identical: do not
use an append-only audit log as the only current-state record.

------------------------------------------------------------------------

## 3. Responsibility of every file

### 3.1 Common constants

  -----------------------------------------------------------------------------
  File                          What it should contain  What it must not
                                                        contain
  ----------------------------- ----------------------- -----------------------
  `CommonConstants.java`        Stable shared defaults, GePNIC endpoint URLs,
                                common date formats,    passwords, private
                                shared language/default keys, per-screen text
                                labels if genuinely     
                                reused                  

  `ApiConstants.java`           Internal WAMIS route    External GePNIC paths
                                segments, such as       mixed with internal
                                `/pre-tenders` and      routes
                                `/integration-jobs`     

  `ValidationConstants.java`    Reused validation       Business rules that
                                limits and common       belong in a validator
                                validation messages     

  `IntegrationConstants.java`   Stable internal portal  Environment URLs,
                                codes and operation     certificates, secret
                                names, if enums are not material
                                sufficient              
  -----------------------------------------------------------------------------

Prefer Java enums for constrained values such as `IntegrationStatus`,
`IntegrationOperation`, and `PortalCode`. Avoid duplicating enum values
as string constants.

### 3.2 Common exception and security

  -----------------------------------------------------------------------
  File/package                        Responsibility
  ----------------------------------- -----------------------------------
  `GlobalExceptionHandler.java`       Convert known exceptions to
                                      consistent HTTP error responses;
                                      never return stack traces or secret
                                      data

  `ErrorResponse.java`                Internal API error shape:
                                      timestamp, status, error code,
                                      message, correlation ID, field
                                      errors

  `common/security/`                  Shared WAMIS
                                      authentication/authorization, if
                                      already applicable; do not put
                                      GePNIC-specific cryptography here
  -----------------------------------------------------------------------

### 3.3 Procurement Pre-Tender files

  -----------------------------------------------------------------------
  File                                Responsibility
  ----------------------------------- -----------------------------------
  `PreTender.java`                    JPA entity for WAMIS pre-tender
                                      data and its business lifecycle. It
                                      must not contain GePNIC encryption
                                      or HTTP code.

  `PreTenderForm.java`                API input model for
                                      creating/updating a WAMIS
                                      pre-tender. Add Bean Validation
                                      annotations such as `@NotNull`,
                                      `@NotBlank`, `@Size`, and
                                      numeric/date constraints as
                                      applicable.

  `PreTenderRepository.java`          Database access for pre-tender
                                      records. Use explicit queries only
                                      when needed.

  `PreTenderService.java`             Business operations:
                                      create/update/load pre-tender,
                                      validate business invariants,
                                      authorize office/work access, and
                                      coordinate persistence.

  `PreTenderController.java`          Internal REST endpoints for WAMIS
                                      pre-tender CRUD/validation. Keep it
                                      thin; do not call the external
                                      GePNIC client directly.

  `PreTenderValidator.java`           Business validation of WAMIS
                                      pre-tender data before portal
                                      mapping. Portal-specific validation
                                      belongs in GePNIC
                                      validation/mapping services.
  -----------------------------------------------------------------------

### 3.4 Adapter contracts and registry

  -----------------------------------------------------------------------
  File                                Responsibility
  ----------------------------------- -----------------------------------
  `ProcurementPortal.java`            Small common interface for
                                      supported operations. Do not force
                                      every portal to implement
                                      unsupported capabilities. Consider
                                      separate capability interfaces if
                                      portal operations diverge.

  `PortalRequest.java`                Internal request context, such as
                                      pre-tender ID, work ID, office ID,
                                      correlation ID, and operation.
                                      Prefer loading authoritative data
                                      from the database rather than
                                      trusting a client-supplied full
                                      work object.

  `PortalResponse.java`               Normalized adapter-level outcome:
                                      accepted/rejected/unknown, external
                                      reference, sanitized message, and
                                      correlation ID.

  `IntegrationResult.java`            Result exposed by the WAMIS
                                      application service/API;
                                      distinguish WAMIS job acceptance
                                      from the remote portal's business
                                      acceptance.

  `PortalAdapterRegistry.java`        Selects an adapter by a stable
                                      portal code. Reject
                                      unknown/disabled portals with a
                                      clear application exception. Avoid
                                      a long `if/else` tree.
  -----------------------------------------------------------------------

### 3.5 GePNIC configuration

  -----------------------------------------------------------------------
  File                                Responsibility
  ----------------------------------- -----------------------------------
  `GePNICConstants.java`              Stable GePNIC field names, known
                                      operation identifiers, and verified
                                      relative endpoint paths. Keep only
                                      values that are truly stable.

  `GePNICProperties.java`             Typed `@ConfigurationProperties`
                                      for enabled flag, environment, base
                                      URL, timeouts, upload limits, and
                                      certificate/secret references.

  `GePNICConfig.java`                 Creates/configures the HTTP client,
                                      Jackson settings, TLS options, and
                                      property validation. Do not disable
                                      TLS certificate validation.
  -----------------------------------------------------------------------

Use `application-dev.yml`, `application-test.yml`, and
`application-prod.yml` for non-secret environment configuration. Inject
secret values from a secrets manager or protected environment variables.
Never commit keystore passwords or private-key files.

### 3.6 GePNIC client and forms

  -----------------------------------------------------------------------
  File                                Responsibility
  ----------------------------------- -----------------------------------
  `GePNICClient.java`                 HTTP transport only: sends the
                                      expected body/multipart fields to
                                      the configured endpoint, applies
                                      timeouts, captures HTTP
                                      status/headers safely, and returns
                                      raw response bytes or a transport
                                      result. It must not map WAMIS
                                      business data.

  `GePNICEnvelopeForm.java`           Represents the documented external
                                      security envelope fields:
                                      `encryptData`, `signData`,
                                      `uniqueUserId`, `encryptKey`,
                                      `keyId`, subject to the exact
                                      official schema.

  `GePNICResponseForm.java`           Represents the external response
                                      envelope. Do not assume all
                                      endpoints have identical response
                                      types.

  `TenderPushForm.java`               Internal WAMIS API request for
                                      requesting a tender push. Prefer
                                      identifiers such as
                                      `preTenderId`/`workId`; load
                                      authoritative fields from the
                                      database.

  `TenderPullForm.java`               Validated input for a tender
                                      retrieval operation, including the
                                      publish date in the required
                                      `yyyyMMdd` format if confirmed by
                                      the contract.

  `CorrigendumPullForm.java`          Validated publish-date input for
                                      corrigendum retrieval.

  `AocPullForm.java`                  Validated created-date input for
                                      AOC retrieval.

  `DocumentUploadForm.java`           Multipart metadata and file input;
                                      validates document category,
                                      extension, detected MIME type,
                                      size, and association with
                                      previously submitted document
                                      metadata.
  -----------------------------------------------------------------------

### 3.7 GePNIC mapper and security

  --------------------------------------------------------------------------
  File                                Responsibility
  ----------------------------------- --------------------------------------
  `WamisToGePNICMapper.java`          Converts WAMIS
                                      work/office/estimate/dates/documents
                                      into the exact GePNIC schema. Keep
                                      field-name mapping here, not in
                                      controllers or encryption classes.

  `GePNICToWamisMapper.java`          Converts validated/decrypted portal
                                      responses into internal models or
                                      result forms. Avoid silently
                                      overwriting WAMIS records.

  `GePNICEncryptionService.java`      Implements the official
                                      payload-encryption procedure only
                                      after the algorithm, encoding, key
                                      size, IV/padding and envelope rules
                                      are confirmed.

  `GePNICDecryptionService.java`      Decrypts responses according to the
                                      official protocol. Reject
                                      malformed/unauthenticated data.

  `GePNICCertificateProvider.java`    Loads approved certificates/keys,
                                      validates expiry and trust as
                                      required, resolves the key
                                      identifier/thumbprint, and supports
                                      planned certificate rotation. Never
                                      returns private key bytes to
                                      controllers.

  `GePNICSignatureVerifier.java`      Verifies signatures over the exact
                                      bytes/representation specified by NIC
                                      before trusting the response. Add a
                                      signing service if the request
                                      requires signing.

  `GePNICResponseParser.java`         Parses the outer envelope and the
                                      decrypted business response. Some
                                      examples contain a JSON string nested
                                      inside `responseMessage`; parse that
                                      nested JSON only when the relevant
                                      endpoint response contract requires
                                      it.
  --------------------------------------------------------------------------

### 3.8 Audit and transaction persistence

  -----------------------------------------------------------------------
  File                                Responsibility
  ----------------------------------- -----------------------------------
  `IntegrationAuditEvent.java`        Append-only audit metadata:
                                      operation, actor/service identity,
                                      timestamp, correlation ID, status
                                      transition, sanitized error code,
                                      and relevant references

  `IntegrationAuditRepository.java`   Persist/query audit events

  `IntegrationAuditService.java`      Write audit events at defined
                                      workflow boundaries; avoid logging
                                      secret values or full sensitive
                                      payloads
  -----------------------------------------------------------------------

Add a current-state integration transaction/job entity when implementing
reliable retries and history. Suggested fields: `id`, `operation`,
`portalCode`, `preTenderId`, `workId`, `correlationId`,
`idempotencyKey`, `status`, `attemptCount`, `externalReference`,
`createdAt`, `updatedAt`, `completedAt`, `errorCode`, and
`sanitizedErrorMessage`.

------------------------------------------------------------------------

## 4. Constants vs configuration vs request fields

### Constants

Keep stable names in Java constants, for example:

``` java
package in.gov.wamis.integration.gepnic.config;

public final class GePNICConstants {
    private GePNICConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String PORTAL_CODE = "GEPNIC";
    public static final String OPERATION_PRE_TENDER = "PRE_TENDER";

    // Verified paths from the supplied NIC document:
    public static final String TENDER_PUSH_PATH =
        "/nicgep_external_webservice/push/postTenderJsonDetailsService";
    public static final String DOCUMENT_UPLOAD_PATH =
        "/nicgep_external_webservice/push/postTenderFileUploadDetailsService";
    public static final String TENDER_PULL_BY_PUBLISH_DATE_PATH =
        "/nicgep_external_webservice/pull/getTenderInfoFromPreTenderbyPublishDateService";
    public static final String CORRIGENDUM_PULL_BY_PUBLISH_DATE_PATH =
        "/nicgep_external_webservice/pull/getPreTenderCorrigendumInfosByPublishedDateService";
    public static final String AOC_PULL_BY_CREATED_DATE_PATH =
        "/nicgep_external_webservice/pull/getAocInfoFromPreTenderbyCreatedDateService";
    public static final String AOC_FILE_DOWNLOAD_PATH =
        "/nicgep_external_webservice/pull/postAOCFileDownloadDetailsService";
}
```

These paths are extracted from the supplied NIC document. They must
still be verified with NIC before deployment. Keep the base URL in
configuration, not in this class.

### Configuration

Example `application.yml` (values are WAMIS configuration keys, not a
declaration of official NIC parameter names):

``` yaml
integration:
  gepnic:
    enabled: false
    environment: demo
    base-url: ${GEPNIC_BASE_URL:https://demoeproc.nic.in}
    connect-timeout: 5s
    read-timeout: 30s
    max-upload-size: 20MB
    unique-user-id: ${GEPNIC_UNIQUE_USER_ID:}
    key-store-location: ${GEPNIC_KEYSTORE_LOCATION:}
    key-store-password: ${GEPNIC_KEYSTORE_PASSWORD:}
    trust-store-location: ${GEPNIC_TRUSTSTORE_LOCATION:}
    trust-store-password: ${GEPNIC_TRUSTSTORE_PASSWORD:}
```

Set `enabled: false` until credentials, certificates, endpoint access,
and the official protocol have been verified. Avoid exposing secrets via
configuration read APIs or logs.

------------------------------------------------------------------------

## 5. Development flow --- implement in this order

### Phase 0 --- Confirm the contract

**Tasks** 1. Read the supplied NIC document and create an endpoint
contract matrix. 2. Confirm each endpoint's HTTP method, content type,
multipart requirements, envelope, decrypted payload, response codes,
error codes, and production URL with NIC. 3. Confirm `uniqueUserId`,
`keyId`, certificate/thumbprint, session-key process,
encryption/signature algorithms, signature input bytes, and response
verification rules. 4. Identify which values come from WAMIS tables and
which come from GePNIC office/user configuration. 5. Agree the internal
WAMIS API request/response contract.

**Exit criteria:** No production cryptographic behavior or undocumented
endpoint is based on guesses.

### Phase 1 --- Scaffolding and configuration

1.  Create the packages and classes shown above.
2.  Add constants, enums, `GePNICProperties`, profiles, exception
    handling, and correlation IDs.
3.  Configure PostgreSQL and schema migrations.
4.  Configure an HTTP client with timeouts and TLS verification enabled.
5.  Add a feature flag to prevent calls until the adapter is configured.
6.  Create unit-test fixtures from sanitized/synthetic samples.

**Exit criteria:** Application starts, connects to the database, and
does not make live GePNIC calls by default.

### Phase 2 --- Pre-Tender form and validation

1.  Implement `PreTenderForm` and Bean Validation.
2.  Implement `PreTenderValidator` for WAMIS business rules.
3.  Ensure selected work and office IDs are authorized and loaded from
    the database.
4.  Confirm required estimate, fee/EMD, critical dates, inviting
    officer, work-item and document data.
5.  Keep GePNIC-specific mandatory-field checks in a portal-specific
    validator or mapper validation service.

**Exit criteria:** Invalid WAMIS pre-tender data is rejected before
payload creation.

### Phase 3 --- Build and validate the GePNIC JSON

1.  Implement `WamisToGePNICMapper`.
2.  Preserve exact property names, numeric/boolean types,
    array-vs-object structure, date format, and null/optional-field
    rules.
3.  Build the outer metadata shape
    `inputData → uniqueRefNo + inputString → PRETENDER → TENDER[]`.
4.  Validate required `TENDER_BASIC_DETAILS`, `TENDER_WORKITEM`,
    `TENDER_FEE_DETAILS`, `TENDER_CRITICAL_DATES`, and
    `TENDER_DOCS_DETAILS` structures against the authoritative schema.
5.  Add golden-file tests using synthetic values.

**Exit criteria:** The mapped payload matches the agreed NIC schema
before encryption.

### Phase 4 --- Cryptography and certificate handling

1.  Implement the exact NIC-approved encryption and signing protocol.
2.  Implement the documented envelope fields: `encryptData`, `signData`,
    `uniqueUserId`, `encryptKey`, and `keyId`.
3.  Implement response signature verification, decryption, and
    certificate/key-ID validation.
4.  Add tests for wrong key, invalid signature, expired/untrusted
    certificate, malformed envelope, and tampered ciphertext.
5.  Never log or display session keys, private keys, keystore passwords,
    or unredacted sensitive payloads.

**Exit criteria:** Security tests and authorized interoperability tests
pass. Sample ciphertext from a PDF is not a reusable test vector for a
new request.

### Phase 5 --- Tender push vertical slice

1.  `TenderIntegrationController` (or your existing internal integration
    controller) accepts a WAMIS pre-tender identifier.
2.  The service loads the authorized WAMIS record and validates it.
3.  Mapper builds the official JSON.
4.  JSON validator checks it.
5.  Security service builds the encrypted/signed envelope.
6.  `GePNICClient` sends it to the configured demo endpoint.
7.  Response is checked, verified, decrypted, and parsed according to
    the official response contract.
8.  Persist transaction state, correlation ID, external reference (if
    returned), and sanitized outcome.
9.  Return a normalized internal result.

**Exit criteria:** One authorized test pre-tender can be pushed,
correlated, and audited end to end.

### Phase 6 --- Document upload

1.  Only allow upload after the required tender document metadata has
    been submitted.
2.  Resolve `uniqueFileNo` and `docuniqueid` from persisted
    tender/document metadata; do not trust arbitrary client-supplied
    identifiers.
3.  Validate extension, detected content type, file size, and category.
4.  Submit the multipart fields specified in the supplied document.
5.  Parse the encrypted response as required by the official contract.
6.  Store upload status per document and support safe reconciliation.

**Exit criteria:** Upload success/failure is tracked independently for
each document.

### Phase 7 --- Retrieval and AOC

Implement tender pull, corrigendum pull, AOC retrieval and AOC file
download only after the push/upload slice is reliable. For each
operation, implement its own request form, response parser and mapping
tests. Do not assume every endpoint uses the same response shape or
encrypted envelope behavior.

### Phase 8 --- Transaction history, audit and retry

1.  Store the current integration-job status separately from append-only
    audit events.
2.  Use a correlation ID per logical operation and an idempotency key
    for duplicate protection.
3.  Retry only operations known to be safe to retry.
4.  On an ambiguous timeout, mark `OUTCOME_UNKNOWN` and reconcile before
    resubmitting.
5.  Restrict access to request/response diagnostics and redact sensitive
    values.

### Phase 9 --- Testing and deployment

Test mapping, validation, cryptography, transport, response parsing,
persistence, duplicate submission, timeouts, certificate rotation,
authorization and production configuration. Keep diagnostics disabled
server-side in production. Document deployment, health checks, key
rotation, error handling and recovery procedures.

------------------------------------------------------------------------

## 6. GePNIC external API contract extracted from the supplied document

**Important distinction:** These are external GePNIC service endpoints.
Internal WAMIS routes are separate and should not be confused with them.

The supplied document shows the following demo base host:
`https://demoeproc.nic.in`.

  --------------------------------------------------------------------------------------------------------------------------------------------------
  Operation            HTTP / content     Demo endpoint path                                                                      Request shape
  -------------------- ------------------ --------------------------------------------------------------------------------------- ------------------
  Push tender JSON     REST service;      `/nicgep_external_webservice/push/postTenderJsonDetailsService`                         Encrypted/signed
                       confirm exact HTTP                                                                                         envelope whose
                       method/content                                                                                             plaintext contains
                       type in current                                                                                            `inputData`
                       NIC contract                                                                                               

  Upload               Multipart upload   `/nicgep_external_webservice/push/postTenderFileUploadDetailsService`                   `uniqueUserId`,
  NIT/BOQ/additional   according to                                                                                               `uniqueFileNo`,
  document             document                                                                                                   `docuniqueid`,
                                                                                                                                  `file`

  Pull tender          Pull service;      `/nicgep_external_webservice/pull/getTenderInfoFromPreTenderbyPublishDateService`       Plaintext input
  information by       confirm current                                                                                            example:
  publish date         method and                                                                                                 `publishedDate`
                       envelope                                                                                                   

  Pull corrigendum by  Pull service;      `/nicgep_external_webservice/pull/getPreTenderCorrigendumInfosByPublishedDateService`   Plaintext input
  publish date         confirm current                                                                                            example:
                       method and                                                                                                 `publishedDate`
                       envelope                                                                                                   

  Pull AOC by created  Pull service;      `/nicgep_external_webservice/pull/getAocInfoFromPreTenderbyCreatedDateService`          Plaintext input
  date                 confirm current                                                                                            example:
                       method and                                                                                                 `createdDate`
                       envelope                                                                                                   

  AOC file download    Pull service;      `/nicgep_external_webservice/pull/postAOCFileDownloadDetailsService`                    AOC document
                       confirm request                                                                                            fields described
                       content type and                                                                                           below
                       binary/encrypted                                                                                           
                       response contract                                                                                          
  --------------------------------------------------------------------------------------------------------------------------------------------------

The supplied document/build notes do not establish exact service names
and request formats for all metadata/document-status checks. Do not
invent those paths. Ask NIC for the exact endpoint and contract before
implementing those operations.

### 6.1 Tender push plaintext structure

The supplied NIC example uses this hierarchy:

``` json
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
                "T_NO_OF_BID_OPENERS": "2 Off 2",
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
                "T_ALLOW_GTE_DETAILS": false,
                "T_TEMPLATE_NAME": "NA",
                "T_LOCATION_DETAILS": "Sample location",
                "T_PINCODE": 411001,
                "T_PRE_BID_MEETING": false,
                "T_PRE_BID_MEETING_PLACE": "NA",
                "T_PRE_BID_MEETING_ADDRESS": "NA",
                "T_BID_OPENNING_PLACE": "Sample office",
                "T_TENDERER_CLASS": "Class A",
                "T_INVITING_OFFICER": "Sample Executive Engineer",
                "T_INVITING_OFF_ADDRESS": "Sample office address",
                "T_ALLOW_ITEMWISE_EVALUATION": false
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

This is a **sanitized, shortened illustration** based on the supplied
example, not a complete schema. The source example contains more fields
and some example values may be old or environment-specific. Implement
the full current NIC schema and required-field rules. Preserve exact
capitalization and nesting.

### 6.2 Tender push encrypted envelope

The supplied document shows these envelope fields:

``` json
{
  "encryptData": "<BASE64_ENCRYPTED_PAYLOAD>",
  "signData": "<BASE64_SIGNATURE>",
  "uniqueUserId": "<NIC-ISSUED-EXTERNAL-USER-ID>",
  "encryptKey": "<BASE64_ENCRYPTED_SESSION_KEY>",
  "keyId": "<GEPNIC-CERTIFICATE-THUMBPRINT>"
}
```

These placeholders are not valid cryptographic values. Generate them at
runtime according to the confirmed NIC protocol. Never copy
ciphertext/signature values from the sample document into code.

### 6.3 Tender push response example

The supplied document shows this decrypted response:

``` json
{
  "responseCode": 1,
  "responseMessage": "{\"responseCode\":101,\"responseMessage\":\"Inserted Successfully\"}"
}
```

The outer `responseMessage` contains JSON encoded as a string. Parse the
outer response, then parse the nested message only when the endpoint's
response contract indicates this shape. Do not assume `responseCode: 1`
alone means the tender is published; it is the sample outer response and
the inner response indicates insertion.

### 6.4 Document upload request and response

The document lists multipart inputs:

-   `uniqueUserId`
-   `uniqueFileNo`
-   `docuniqueid`
-   `file`

The file's metadata should correspond to the document already registered
in `TENDER_DOCS_DETAILS`.

  Document category     Formats stated in supplied document
  --------------------- -------------------------------------
  NIT                   PDF
  BOQ                   XLS
  Additional document   PDF, XLS, RAR, JPG

Check actual file bytes, size, extension and category. Do not trust the
browser-provided MIME type alone.

Sample decrypted response:

``` json
{
  "responseCode": 1,
  "responseMessage": "{\"responseCode\":105,\"responseMessage\":\"Document Uploaded Successfully\"}"
}
```

### 6.5 Tender pull by publish date

**Endpoint path:**
`/nicgep_external_webservice/pull/getTenderInfoFromPreTenderbyPublishDateService`

Sample plaintext request shown in the document:

``` json
{
  "publishedDate": "20230519"
}
```

The date is shown as `yyyyMMdd`. Confirm the current endpoint's required
request envelope and whether the input is sent plaintext or inside an
encrypted/signed envelope.

The document's example response includes an outer `responseCode` and a
nested `responseMessage` containing response code `104` and a `TENDERS`
result. Parse it according to the exact response structure; do not
hard-code the sample tender data or treat code `104` as a universal
success code for all services.

### 6.6 Corrigendum pull by publish date

**Endpoint path:**
`/nicgep_external_webservice/pull/getPreTenderCorrigendumInfosByPublishedDateService`

Sample plaintext request:

``` json
{
  "publishedDate": "20230519"
}
```

The source response example contains a nested `TENDER_CORRIGENDUM`
collection. Implement a dedicated response form/parser and preserve the
portal's field names when parsing. Confirm the current response codes
and whether the response is encrypted for this endpoint.

### 6.7 AOC pull by created date

**Endpoint path:**
`/nicgep_external_webservice/pull/getAocInfoFromPreTenderbyCreatedDateService`

Sample plaintext request:

``` json
{
  "createdDate": "20230818"
}
```

The document's response example contains a nested `AOC` collection with
AOC details and document metadata. Confirm the full response schema and
status codes from the applicable pages and current NIC contract.

### 6.8 AOC file download field mapping

The supplied document/build notes show this mapping:

  AOC response field   Download request field   Meaning
  -------------------- ------------------------ ----------------
  `t_DOC_TYPE`         `docType`                Document type
  `t_DOC_NAME`         `fileName`               File name
  `t_BID_ID`           `bId`                    Bid identifier
  `t_DOC_CODE`         `docCode`                Document code

Illustrative request:

``` json
{
  "docType": "BOQ",
  "bId": "64929",
  "docCode": "BOQ64929",
  "fileName": "BOQ_29543.xls"
}
```

Document types listed in the notes include `BOQ`, `LOA`, `NEGO`,
`TIEBREAK`, and `BOQCHART`. Confirm the current allowed values and
whether the endpoint returns JSON, an encrypted envelope, or binary file
content.

### 6.9 Metadata and document-status services

The available source material does not provide a sufficiently verified
exact endpoint name and complete request/response schema for every
metadata/document-status operation. Keep the internal WAMIS route
separate, and do not implement a guessed external path. Ask NIC for the
relevant service definition, HTTP method, request fields,
encryption/signing requirements, response schema, and error codes.

------------------------------------------------------------------------

## 7. Proposed internal WAMIS API routes

These are **internal WAMIS routes**, not GePNIC URLs. Align them with
existing WAMIS routing and authorization conventions.

  -----------------------------------------------------------------------------------------------
  Method                  Internal route                             Purpose
  ----------------------- ------------------------------------------ ----------------------------
  `POST`                  `/api/pre-tenders`                         Create a WAMIS pre-tender

  `GET`                   `/api/pre-tenders/{id}`                    Load pre-tender details

  `POST`                  `/api/pre-tenders/{id}/validate`           Validate WAMIS and portal
                                                                     requirements

  `POST`                  `/api/pre-tenders/{id}/integration-jobs`   Create a GePNIC submission
                                                                     job

  `GET`                   `/api/integration-jobs/{id}`               Read job status/result

  `GET`                   `/api/integration-jobs/{id}/history`       Read sanitized job events

  `POST`                  `/api/gepnic/tenders/push`                 Optional command route if
                                                                     this is the chosen API
                                                                     convention

  `POST`                  `/api/gepnic/documents/upload`             Upload one tender document

  `POST`                  `/api/gepnic/tenders/pull`                 Pull tender information

  `POST`                  `/api/gepnic/corrigendums/pull`            Pull corrigenda

  `POST`                  `/api/gepnic/aoc/pull`                     Pull AOC information

  `POST`                  `/api/gepnic/aoc/documents/download`       Download/request an AOC
                                                                     document

  `GET`                   `/api/gepnic/transactions`                 Search integration
                                                                     transactions

  `GET`                   `/api/gepnic/transactions/{id}`            Read transaction details

  `GET`                   `/api/gepnic/transactions/{id}/audit`      Read audit history

  `POST`                  `/api/gepnic/transactions/{id}/retry`      Retry an eligible operation
                                                                     after
                                                                     idempotency/reconciliation
                                                                     checks

  `GET`                   `/api/gepnic/configuration`                Read authorized, non-secret
                                                                     configuration
  -----------------------------------------------------------------------------------------------

Choose one consistent internal API pattern rather than exposing
duplicate routes for the same operation. Never expose production
encryption/decryption as a general-purpose public endpoint. Development
diagnostics must be disabled server-side in production.

------------------------------------------------------------------------

## 8. Multi-work behavior

For the first release, distinguish these use cases:

-   **Batch submission:** A user selects several WAMIS works. Create a
    separate integration job for each work unless the approved portal
    contract explicitly supports a batch operation. Display each outcome
    independently.
-   **Combined tender:** Several works are deliberately grouped into one
    external tender. This is a separate business capability that
    requires portal support, authorization and procurement-rule
    validation.

Illustrative internal batch request (not a GePNIC schema):

``` json
{
  "portalCode": "GEPNIC",
  "operation": "PRE_TENDER",
  "workIds": [101, 102, 103]
}
```

The backend must validate each work and create traceable jobs. Never
assume the portal accepts a `workIds` array or can merge the works into
one tender.

------------------------------------------------------------------------

## 9. Status model and retry rules

Use an enum, not arbitrary strings throughout services.

``` java
public enum IntegrationStatus {
    DRAFT,
    VALIDATION_FAILED,
    READY,
    QUEUED,
    IN_PROGRESS,
    ACCEPTED,
    REJECTED,
    RETRY_PENDING,
    OUTCOME_UNKNOWN,
    FAILED
}
```

-   `ACCEPTED` means the remote system acknowledged the operation
    according to its contract. It does not automatically mean the tender
    is published.
-   `REJECTED` means the remote business response indicates rejection.
-   `OUTCOME_UNKNOWN` means a timeout or connection interruption
    occurred after the remote system may have processed the request.
-   Retry only after checking whether the operation is safe to repeat.
    Reconcile uncertain tender submissions before retrying to avoid
    duplicates.

------------------------------------------------------------------------

## 10. Test plan

### Unit tests

-   `PreTenderValidator`: missing office, invalid dates, invalid
    amounts, missing work data, invalid document metadata.
-   `WamisToGePNICMapper`: required field mapping, nulls, arrays,
    number/boolean types, date formatting, exact field names.
-   `GePNICResponseParser`: valid outer envelope, nested JSON string,
    malformed nested JSON, unexpected code/shape.
-   Security services: invalid signature, tampered data, wrong key,
    certificate expiry/trust failure, malformed Base64.
-   `PortalAdapterRegistry`: known, disabled and unknown portal codes.

### Integration tests

-   PostgreSQL persistence and migrations.
-   HTTP timeout, non-2xx response, malformed response, connection
    refusal.
-   Tender push with a mocked GePNIC endpoint.
-   Multipart document upload.
-   Partial batch failures and per-work status.
-   Duplicate submission and idempotency handling.
-   Authorization failures and office/work access restrictions.

### Acceptance tests

-   A valid synthetic WAMIS record maps to the approved GePNIC schema.
-   An invalid record is rejected before encryption/network
    transmission.
-   No secret appears in logs, UI, exceptions or audit records.
-   A successful remote response is correlated with the correct WAMIS
    record.
-   An uncertain result does not automatically trigger an unsafe
    duplicate submission.
-   Production startup fails clearly when the adapter is enabled but
    required configuration is missing.

------------------------------------------------------------------------

## 11. Definition of done for the first release

-   [ ] Existing WAMIS entities and authorization rules are reused
    appropriately.
-   [ ] Pre-Tender form and business validation are complete.
-   [ ] GePNIC constants and environment properties are separated.
-   [ ] Canonical WAMIS data maps to the verified NIC tender schema.
-   [ ] Envelope encryption/signing follows the confirmed official
    specification.
-   [ ] Tender push works in the authorized test environment.
-   [ ] Outer and nested response formats are parsed safely.
-   [ ] Integration transaction and audit events are persisted.
-   [ ] Correlation ID and idempotency/reconciliation strategy are
    implemented.
-   [ ] Secrets are not logged or committed.
-   [ ] Unit and integration tests pass.
-   [ ] Production configuration, deployment and recovery instructions
    are documented.

## 12. Recommended first vertical slice

Build only this path first:

**Load WAMIS Pre-Tender → validate → map to GePNIC JSON → validate
mapped schema → encrypt/sign using the confirmed protocol → call
tender-push endpoint → verify/decrypt response → parse response →
persist status/audit → return normalized result.**

Once this slice is stable, reuse its shared security and
transaction-processing components for document upload, retrieval,
corrigendum, and AOC operations. Keep each operation's payload mapping
and response parsing specific to its own contract.
