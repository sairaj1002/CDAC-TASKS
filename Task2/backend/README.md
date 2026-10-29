# WAMIS–GePNIC Integration Service

Separately deployable Spring Boot integration service connecting WAMIS to NIC-GePNIC.

## Architecture & Layout

```text
com.cdac.egov.integration.gepnic
├── GePNICServiceApplication.java
├── constants/
│   └── GePNICConstants.java
├── config/
│   ├── GePNICProperties.java
│   ├── HttpClientConfig.java
│   └── JacksonConfig.java
├── controller/
│   ├── DashboardController.java
│   ├── OfficeLookupController.java
│   ├── TenderIntegrationController.java
│   ├── DocumentIntegrationController.java
│   ├── AocIntegrationController.java
│   ├── MetadataIntegrationController.java
│   ├── JsonDiagnosticsController.java
│   ├── TransactionController.java
│   └── ConfigurationController.java
├── forms/
│   ├── common/
│   │   ├── ApiResponseForm.java
│   │   └── IntegrationResultForm.java
│   ├── tender/
│   │   ├── TenderPushForm.java
│   │   ├── TenderPullForm.java
│   │   ├── TenderResponseForm.java
│   │   ├── CorrigendumPullForm.java
│   │   └── CorrigendumResponseForm.java
│   ├── encryption/
│   │   ├── EncryptedEnvelopeForm.java
│   │   ├── EncryptionResultForm.java
│   │   └── DecryptionResultForm.java
│   ├── document/
│   │   ├── DocumentUploadForm.java
│   │   ├── DocumentStatusForm.java
│   │   └── DocumentResponseForm.java
│   ├── aoc/
│   │   ├── AocPullForm.java
│   │   ├── AocResponseForm.java
│   │   └── AocDownloadForm.java
│   ├── configuration/
│   │   ├── IntegrationConfigForm.java
│   │   └── CertificateMetadataForm.java
│   └── metadata/
│       ├── MetadataCheckForm.java
│       └── MetadataStatusResponseForm.java
├── service/
│   ├── tender/
│   │   ├── TenderIntegrationService.java
│   │   ├── TenderPushService.java
│   │   ├── TenderPullService.java
│   │   ├── TenderPayloadMapper.java
│   │   └── TenderValidationService.java
│   ├── encryption/
│   │   ├── GePNICEncryptionService.java
│   │   ├── GePNICDecryptionService.java
│   │   ├── DigitalSignatureService.java
│   │   ├── CertificateService.java
│   │   └── SessionKeyService.java
│   ├── document/
│   │   └── DocumentIntegrationService.java
│   ├── aoc/
│   │   └── AocIntegrationService.java
│   ├── metadata/
│   │   └── MetadataIntegrationService.java
│   ├── json/
│   │   ├── GePNICJsonBuilder.java
│   │   ├── GePNICJsonParser.java
│   │   └── GePNICJsonValidationService.java
│   └── integration/
│       ├── GePNICHttpClient.java
│       ├── GePNICRequestService.java
│       ├── GePNICResponseService.java
│       └── IntegrationOrchestrator.java
├── mapper/
│   ├── WamisToGePNICMapper.java
│   └── GePNICToWamisMapper.java
├── entity/
│   ├── IntegrationTransaction.java
│   ├── DocumentTransaction.java
│   └── IntegrationConfiguration.java
├── repository/
│   ├── IntegrationTransactionRepository.java
│   ├── DocumentTransactionRepository.java
│   └── IntegrationConfigurationRepository.java
├── enums/
│   ├── IntegrationStatus.java
│   └── IntegrationOperation.java
├── security/
│   ├── SecretProvider.java
│   └── CertificateProvider.java
├── exception/
│   ├── GePNICException.java
│   ├── GePNICCryptoException.java
│   ├── GePNICApiException.java
│   └── GlobalExceptionHandler.java
└── scheduler/
    └── StatusSyncScheduler.java
```

## Running the Service

```bash
# Local development (uses in-memory H2 database by default)
mvn clean spring-boot:run

# Run Unit & Integration Tests
mvn test

# Package JAR
mvn clean package
```
