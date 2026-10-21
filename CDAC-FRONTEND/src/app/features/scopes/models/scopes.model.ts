export const BRIDGE_ACTIVITIES = [
  { value: 'REPAIR_TO_WORK', label: 'Repair-to-work' },
  { value: 'NEW_ACTIVITY', label: 'New Activity' },
  { value: 'MAINTENANCE', label: 'Maintenance' },
  { value: 'OTHER_ACTIVITY', label: 'Other Activity' },
] as const;

export type BridgeActivity = (typeof BRIDGE_ACTIVITIES)[number]['value'];

export interface WorkScopeRequest {
  nameOfRiverNallahBarrier: string;
  width: number;
  length: number;
  approachRoad: string;
  numberOfSpans: number;
  spanLength: number;
  bridgeActivity: BridgeActivity;
}

export interface WorkScopeResponse extends WorkScopeRequest {
  scopeId: number;
}

export interface WorkDetailsRequest {
  subWorkDetails: string;
  workScopes: WorkScopeRequest[];
}

export interface WorkDetailsResponse {
  workId: number;
  subWorkDetails: string;
  workScopes: WorkScopeResponse[];
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}
