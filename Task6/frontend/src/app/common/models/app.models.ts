import { APP_CONSTANTS } from '../constants/app.constants';

/** A document of a saved Pre-Tender, as listed in the documents dialog (GET /pre-tenders/{id}/documents). */
export interface PreTenderDocumentResponse {
  id: number;
  docUniqueId: number;
  docCode: string;
  docName: string;
  docDescription?: string;
  docMimeType: string;
  docSize: number;
  docPriority: number;
  // true when the file itself is stored and can be downloaded
  uploaded: boolean;
  uploadedAt?: string;
}

/** A main work (WMS > Work and Sub Work > Work Details). */
export interface WorkDetail {
  id: number;
  registrationNumber: string;
  workNumber: string;
  nameOfWork: string;
  workCategory: string;
  programType: string;
  workType: string;
  district: string;
  scheme: string;
  budgetYear: string;
}

/** A sub work of a main work (WMS > Work and Sub Work > Sub Work Details); one tender is raised per sub work. */
export interface SubWorkDetail {
  id: number;
  workId: number;
  registrationNumber: string;
  subWorkNumber: string;
  description: string;
  workCategory: string;
  programType: string;
  workType: string;
  scheme: string;
}

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
  productCategory?: string;
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
  productCategory?: string;
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
  // the backend's answer: status is an IntegrationStatus (FAILED, REJECTED, ACCEPTED, ...)
  status?: string;
  correlationId?: string;
  statusCode?: number;
  message: string;
  externalReference?: string;
  rawPayload?: string;
  timestamp?: string;
}

/** Bidder detail inside Award of Contract (AOC) */
export interface AocBidderDetail {
  sNo: number;
  bidId: string;
  bidderName: string;
  gstin: string;
  evaluationScore?: number;
  scoreGrade?: string;
  rejectionReason?: string;
  quotedValue?: number;
  bidSubmittedDate: string;
  qualified: boolean;
}

/** Document attachment associated with AOC */
export interface AocDocumentDetail {
  docType: 'BOQ' | 'LOA' | 'NEGO' | 'TIEBREAK' | 'BOQCHART' | string;
  bId: string;
  docCode: string;
  fileName: string;
  fileSize?: string;
  downloadUrl?: string;
}

/** AOC Record matching GePNIC Specification and PPTX Slide 48 & 49 */
export interface AocRecord {
  id: number;
  uniqueFileNo: string;
  tenderRefNo: string;
  tenderId: string;
  workItemId: string;
  tenderDescription: string;
  tenderType: string;
  publishDate: string;
  contractDate: string;
  contractNo: string;
  orgId: string;
  organizationName: string;
  currency: string;
  remarks: string;
  noOfBids: number;
  aocCreatedDate: string;
  isInsertedInAoc: boolean;
  qualifiedBidders: AocBidderDetail[];
  nonQualifiedBidders: AocBidderDetail[];
  documents: AocDocumentDetail[];
  rawJsonResponse?: any;
}

/** Corrigendum Record retrieved by published date */
export interface CorrigendumRecord {
  id: number;
  tenderRefNo: string;
  uniqueFileNo?: string;
  corrigendumRef: string;
  corrigendumType: string;
  publishedDate: string;
  description: string;
  status: string;
  docCode?: string;
  docName?: string;
}

/** Negotiation Record for post-tender bidder negotiation */
export interface NegotiationRecord {
  id: number;
  workNumber: string;
  tenderRefNo: string;
  l1BidderName: string;
  bidderRank?: string;
  bidderScore?: number;
  originalQuotedValue: number;
  negotiatedValue: number;
  savingsPercent: number;
  negotiationDate: string;
  meetingVenue: string;
  committeeRemarks: string;
  status: 'APPROVED' | 'PENDING' | 'REJECTED';
  negoDocName?: string;
  negoDocCode?: string;
}

/** DB parameters controlling tab visibility on Post-Tender screen */
export interface PostTenderDbParameters {
  enableCorrigendumTab: boolean;
  enableNegotiationTab: boolean;
}
