export interface EmbResponse {
  embId: number;
  workRegNo: number;
  measurementNo: string;
  measurementDate: string;
  description: string;
  quantity: number;
  unit: string;
  rate: number;
  amount: number;
  createdAt: string;
  updatedAt: string;
}

/** Payload for POST / PUT (server generates embId, createdAt, updatedAt) */
export type EmbRequest = Omit<EmbResponse, 'embId' | 'createdAt' | 'updatedAt'>;
