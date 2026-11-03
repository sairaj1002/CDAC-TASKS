import { APP_CONSTANTS } from '../constants/app.constants';

export interface PreTenderDocumentForm {
  id?: number;
  docUniqueId: number;
  docCode: string;
  docName: string;
  docDescription?: string;
  docType?: string;
  docMimeType?: string;
  docSize?: number;
  docPriority?: number;
  documentCategory?: string;
  docPath?: string;
  isMandatory?: boolean;
  uploadedAt?: string;
}

export interface PreTenderResponseForm {
  id: number;
  workId: number;
  officeId: number;
  orgId: number;
  uniqueFileNo: string;
  tenderReferenceNo: string;
  tenderTitle: string;
  tenderDescription?: string;
  tenderType: string;
  formContract: string;
  category: string;
  coversCount: number;
  bidOpeners?: string;
  tenderValue: number;
  feeAmount: number;
  emdAmount: number;
  publishingDate: string;
  docDownloadStartDate: string;
  seekClarificationStartDate?: string;
  seekClarificationEndDate?: string;
  preBidMeeting?: boolean;
  preBidMeetingPlace?: string;
  preBidMeetingAddress?: string;
  bidSubmissionStartDate: string;
  bidSubmissionEndDate: string;
  bidOpenDate: string;
  bidValidityDays?: number;
  deliveryPeriodDays?: number;
  location?: string;
  pincode?: number;
  bidOpenPlace?: string;
  tendererClass?: string;
  invitingOfficer: string;
  invitingOfficerAddress: string;
  status: string;
  documents: PreTenderDocumentForm[];
  createdBy?: string;
  createdDate?: string;
  updatedBy?: string;
  updatedDate?: string;
}

export interface PreTenderForm {
  id?: number;
  uniqueFileNo: string;
  tenderReferenceNo: string;
  workId: number;
  officeId: number;
  orgId: number;
  tenderTitle: string;
  tenderDescription?: string;
  tenderType: string;
  formContract: string;
  category: string;
  coversCount: number;
  bidOpeners?: string;
  tenderValue: number;
  feeAmount: number;
  emdAmount: number;
  publishingDate: string;
  docDownloadStartDate: string;
  seekClarificationStartDate?: string;
  seekClarificationEndDate?: string;
  preBidMeeting?: boolean;
  preBidMeetingPlace?: string;
  preBidMeetingAddress?: string;
  bidSubmissionStartDate: string;
  bidSubmissionEndDate: string;
  bidOpenDate: string;
  bidValidityDays?: number;
  deliveryPeriodDays?: number;
  location?: string;
  pincode?: number;
  bidOpenPlace?: string;
  tendererClass?: string;
  invitingOfficer: string;
  invitingOfficerAddress: string;
  documents: PreTenderDocumentForm[];
}

export interface DropdownMasterResponseForm {
  id: number;
  category: string;
  code: string;
  displayName: string;
  description?: string;
  displayOrder: number;
  active: boolean;
  createdBy?: string;
  createdAt?: string;
  updatedBy?: string;
  updatedAt?: string;
}

export interface DropdownMasterForm {
  category: string;
  code: string;
  displayName: string;
  description?: string;
  displayOrder: number;
  active?: boolean;
}

export interface IntegrationResult {
  transactionId: number;
  correlationId: string;
  portalCode: string;
  operation: string;
  status: string;
  jobAccepted: boolean;
  remoteAccepted: boolean;
  externalReference?: string;
  message: string;
  timestamp: string;
}

export interface IntegrationJobResponseForm {
  id: number;
  operation: string;
  portalCode: string;
  preTenderId: number;
  workId: number;
  correlationId: string;
  idempotencyKey?: string;
  status: string;
  attemptCount: number;
  externalReference?: string;
  errorCode?: string;
  sanitizedErrorMessage?: string;
  createdAt: string;
  updatedAt?: string;
  completedAt?: string;
}

export interface AuditEventResponseForm {
  id: number;
  transactionId: number;
  operation: string;
  portalCode: string;
  actor: string;
  correlationId: string;
  fromStatus?: string;
  toStatus: string;
  eventTimestamp: string;
  sanitizedErrorCode?: string;
  sanitizedMessage?: string;
  externalReference?: string;

  // Presentation fields matching Screenshot 5
  remark?: string;
  name?: string;
  employeeName?: string;
  designation?: string;
  timestamp?: string;
}

export interface ConfirmDialogData {
  title: string;
  message: string;
  confirmButtonText?: string;
  cancelButtonText?: string;
  isDanger?: boolean;
}

export interface UploadBoqDialogData {
  workNumber: string;
  uniqueFileNo: string;
  tenderReferenceNumber: string;
}

export interface PortalResponse {
  statusCode: number;
  message: string;
  externalReference?: string;
  rawPayload?: string;
  timestamp?: string;
}
