export interface HrmsResponse {
  employeeId: number;
  employeeCode: string;
  employeeName: string;
  designation: string;
  department: string;
  email: string;
  mobileNo: string;
  joiningDate: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}

/** Payload for POST / PUT (server generates employeeId, createdAt, updatedAt) */
export type HrmsRequest = Omit<HrmsResponse, 'employeeId' | 'createdAt' | 'updatedAt'>;
