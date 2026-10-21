export interface AmsResponse {
  amsId: number;
  assetCode: string;
  assetName: string;
  assetType: string;
  description: string;
  location: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}

/** Payload for POST / PUT (server generates amsId, createdAt, updatedAt) */
export type AmsRequest = Omit<AmsResponse, 'amsId' | 'createdAt' | 'updatedAt'>;
