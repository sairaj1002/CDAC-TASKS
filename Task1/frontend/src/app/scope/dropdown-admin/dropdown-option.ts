export interface DropdownOption {
  id: number;
  category: string;
  code: string;
  label: string;
  active: boolean;
  displayOrder: number;
}

export interface SelectOption {
  label: string;
  value: string;
}

export type DropdownCategory =
  | 'ROAD_TYPE'
  | 'PROPOSED_ROAD_TYPE'
  | 'NATURE_OF_WORK'
  | 'WORK_PLAN_TYPE'
  | 'CD_WORK_TYPE'
  | 'REPAIR_MAINTENANCE_TYPE'
  | 'BRIDGE_ACTIVITY'
  | string;

export interface CreateDropdownOptionRequest {
  category: string;
  code: string;
  label: string;
  displayOrder: number;
  active?: boolean;
}

export interface UpdateDropdownOptionRequest {
  label: string;
  displayOrder: number;
  active: boolean;
}
