export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  errorCode?: string;
  timestamp: string;
}

export interface IntegrationTransaction {
  id: number;
  transactionId: string;
  correlationId: string;
  operation: string;
  status: string;
  sourceSystem: string;
  targetSystem: string;
  officeCode?: string;
  endpointUrl?: string;
  requestPayloadSanitized?: string;
  responsePayloadSanitized?: string;
  responseCode?: number;
  responseMessage?: string;
  recordsProcessed?: number;
  retryCount: number;
  createdAt: string;
  updatedAt?: string;
}

export interface DocumentTransaction {
  id?: number;
  uniqueFileNo: string;
  docUniqueId: string;
  tenderReference: string;
  fileName: string;
  docCode?: string;
  docType?: string;
  fileSizeBytes?: number;
  uploadStatus: string;
  responseCode?: number;
  responseMessage?: string;
  uploadedAt: string;
}

export interface OfficeItem {
  officeId: string;
  officeCode: string;
  officeName: string;
  officeHead: string;
  nicOrgId: number;
  preTenderConfigured: boolean;
  certificateConfigured: boolean;
  status: string;
}

export interface TenderDocumentItem {
  documentId: string;
  fileName: string;
  contentType: string;
  fileSize?: number;
  docCode?: string;
}

export interface TenderPushPayload {
  wamisTenderId: string;
  officeId: string;
  tenderTitle: string;
  estimatedCost: number;
  tenderFee?: number;
  emdAmount?: number;
  publishDate: string;
  submissionEndDate: string;
  workDescription?: string;
  documents: TenderDocumentItem[];
}

export interface CorrigendumItem {
  externalReference: string;
  publishedAt: string;
  summary: string;
}

export interface TenderResponse {
  sourceSystem: string;
  wamisTenderId: string;
  externalTenderReference: string;
  status: string;
  title: string;
  estimatedCost: number;
  publishDate: string;
  bidSubmissionEndDate: string;
  lastSynchronizedAt: string;
  corrigendumCount: number;
  corrigendums: CorrigendumItem[];
}

export interface CorrigendumResponse {
  corrigendumReference: string;
  tenderReference: string;
  publishedDate: string;
  description: string;
  type: string;
  status: string;
}

export interface AocDocumentItem {
  bId: string;
  docType: string;
  docCode: string;
  fileName: string;
  fileSize?: number;
}

export interface AocResponse {
  tenderReference: string;
  contractNumber: string;
  awardedBidderName: string;
  awardedValue: number;
  awardDate: string;
  status: string;
  documents: AocDocumentItem[];
}

export interface MetadataStatusResponse {
  tenderReference: string;
  uniqueFileNo: string;
  metadataSynced: boolean;
  documentCountUploaded: number;
  documentCountExpected: number;
  syncStatus: string;
  lastVerifiedAt: string;
  message: string;
}

export interface DiagnosticsFlow {
  stage1WamisJson: string;
  stage2MappedJson: string;
  stage3ValidationResult: string;
  stage4EncryptedEnvelope: any;
  stage5HttpResponseEnvelope: string;
  stage6DecryptedResponse: string;
  stage7FinalNormalizedResult: string;
}

export interface ConfigurationView {
  configuration: {
    environmentName: string;
    baseUrl: string;
    uniqueUserId: string;
    keyId: string;
    active: boolean;
    connectTimeoutMs: number;
    readTimeoutMs: number;
  };
  certificate: {
    keyId: string;
    subjectDn: string;
    issuerDn: string;
    serialNumber: string;
    validFrom: string;
    validTo: string;
    valid: boolean;
    algorithm: string;
  };
  connectionStatus: string;
  lastTestedAt: string;
}
