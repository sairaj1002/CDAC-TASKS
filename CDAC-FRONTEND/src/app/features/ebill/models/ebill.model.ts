export interface EbillResponse {
  ebillId: number;
  billNo: string;
  workRegNo: number;
  contractorName: string;
  billAmount: number;
  billDate: string;
  paymentStatus: string;
  createdAt: string;
  updatedAt: string;
}

/** Payload for POST / PUT (server generates ebillId, createdAt, updatedAt) */
export type EbillRequest = Omit<EbillResponse, 'ebillId' | 'createdAt' | 'updatedAt'>;
