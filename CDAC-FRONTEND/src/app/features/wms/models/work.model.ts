export interface WorkResponse {
  workRegNo: number;
  workDetails: string;
  actualStartDate: string | null;
  contractorName: string;
  stipulatedStartDate: string;
  stipulatedEndDate: string;
  createdAt: string;
  updatedAt: string;
}

export interface WorkAuditResponse {
  revisionNumber: number;
  revisionDate: string;
  revisionType: 'CREATED' | 'UPDATED' | 'DELETED';
  legacyRevision: number | null;
  work: WorkResponse;
  createdBy: string | null;
  modifiedBy: string | null;
  userId: number | null;
  userName: string | null;
  ipAddress: string | null;
  macAddress: string | null;
}

export interface WorkReportSummary {
  totalRecords: number;
  contractorLabels: string[];
  contractorCounts: number[];
}

export interface WorkReportFilters {
  fromDate: string | null;
  toDate: string | null;
  contractor: string;
}

export interface WorkImportRow {
  rowNumber: number;
  workDetails: string;
  actualStartDate: string | null;
  contractorName: string;
  stipulatedStartDate: string;
  stipulatedEndDate: string;
}

export interface WorkImportError {
  rowNumber: number;
  message: string;
}

export interface WorkImportPreview {
  validRows: WorkImportRow[];
  errors: WorkImportError[];
}

export interface WorkImportResult {
  importedCount: number;
  errors: WorkImportError[];
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

/** Payload for POST / PUT (server generates workRegNo, createdAt, updatedAt). */
export type WorkRequest = Omit<WorkResponse, 'workRegNo' | 'createdAt' | 'updatedAt'>;
