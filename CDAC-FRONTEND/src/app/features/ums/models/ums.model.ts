export interface UmsResponse {
  userId: number;
  username: string;
  fullName: string;
  email: string;
  role: string;
  status: string;
  lastLoginAt: string | null;
  createdAt: string;
  updatedAt: string;
}

/** Payload for POST / PUT (server generates userId, createdAt, updatedAt) */
export type UmsRequest = Omit<UmsResponse, 'userId' | 'createdAt' | 'updatedAt'>;
