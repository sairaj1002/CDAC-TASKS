export interface MisResponse {
  misId: number;
  reportName: string;
  reportType: string;
  description: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}

/** Payload for POST / PUT (server generates misId, createdAt, updatedAt) */
export type MisRequest = Omit<MisResponse, 'misId' | 'createdAt' | 'updatedAt'>;
