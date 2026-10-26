/**
 * Centralized constants for Road Component features.
 * Holds validation patterns, limits, error/success messages, dropdown categories,
 * plan types, audit defaults, dialog configurations, snackbar settings, and endpoints.
 */

// =========================================================================
// VALIDATION PATTERNS & FIELD LIMITS
// =========================================================================
export const ROAD_VALIDATION = {
  MIN_LENGTH: 0.001,
  MIN_WIDTH: 0.001,
  MAX_LENGTH_TEXT: 50,
  PATTERN_NON_WHITESPACE: /\S/,
  PATTERN_INTEGER: /^\d+$/,
  PATTERN_CODE: /^[A-Za-z0-9_]+$/,
} as const;

// =========================================================================
// VALIDATION & UI MESSAGES
// =========================================================================
export const ROAD_MESSAGES = {
  // Validation messages
  NATURE_OF_WORK_REQUIRED: 'Nature of work is required.',
  NATURE_OF_WORK_MAX: 'Nature of work must not exceed 50 characters.',
  PROPOSED_ROAD_TYPE_REQUIRED: 'Type of proposed road is required.',
  PROPOSED_ROAD_TYPE_MAX: 'Type of proposed road must not exceed 50 characters.',
  LENGTH_REQUIRED: 'Road length is required.',
  LENGTH_MIN: 'Road length must be greater than zero.',
  WIDTH_MIN: 'Road width must be greater than zero.',
  WORK_PLAN_TYPE_REQUIRED: 'Work plan type is required.',
  WORK_PLAN_TYPE_MAX: 'Work plan type must not exceed 50 characters.',
  CD_WORK_TYPE_REQUIRED: 'Type of CD works is required.',
  CD_WORK_TYPE_MAX: 'Type of CD works must not exceed 50 characters.',
  REPAIR_MAINTENANCE_TYPE_REQUIRED: 'Repair/Maintenance Type is required for Non-Plan work.',
  REPAIR_MAINTENANCE_TYPE_MAX: 'Repair/maintenance type must not exceed 50 characters.',

  // Feedback notifications
  FORM_INVALID_WARNING: 'Please fill in all required fields marked in red before saving.',
  CREATED_SUCCESS: 'Road component created successfully.',
  UPDATED_SUCCESS: 'Road component updated successfully.',
  DELETED_SUCCESS: 'Road component deleted successfully.',
  SAVE_ERROR: 'Road component could not be saved.',
  DELETE_ERROR: 'Road component could not be deleted.',
  LOAD_ERROR: 'Road data could not be loaded.',
} as const;

// =========================================================================
// DROPDOWN CATEGORIES & WORK PLAN TYPES
// =========================================================================
export const ROAD_CATEGORIES = {
  NATURE_OF_WORK: 'NATURE_OF_WORK',
  ROAD_TYPE: 'ROAD_TYPE',
  WORK_PLAN_TYPE: 'WORK_PLAN_TYPE',
  CD_WORK_TYPE: 'CD_WORK_TYPE',
  REPAIR_MAINTENANCE_TYPE: 'REPAIR_MAINTENANCE_TYPE',
} as const;

export const ROAD_WORK_PLAN_TYPES = {
  PLAN: 'Plan',
  PLAN_CODE: 'PLAN',
  NON_PLAN: 'Non-Plan',
  NON_PLAN_CODE: 'NON_PLAN',
} as const;

export function isRoadNonPlan(workPlanType: string | null | undefined): boolean {
  if (!workPlanType) return false;
  const trimmed = workPlanType.trim();
  return trimmed === ROAD_WORK_PLAN_TYPES.NON_PLAN || trimmed === ROAD_WORK_PLAN_TYPES.NON_PLAN_CODE;
}

// =========================================================================
// AUDIT TABLE DEFAULTS
// =========================================================================
export const ROAD_AUDIT = {
  COLUMNS: ['remark', 'name', 'employeeName', 'designation', 'timestamp'] as string[],
  DEFAULT_EMPLOYEE_NAME: 'Admin',
  DEFAULT_DESIGNATION: 'Executive Engineer',
  DEFAULT_FALLBACK_NAME: 'Road',
  DEFAULT_REMARK_CREATED: 'Created',
  DEFAULT_REMARK_REVISION: 'Revision',
  DEFAULT_PAGE_SIZE: 5,
  DEFAULT_PAGE_INDEX: 0,
} as const;

// =========================================================================
// DIALOG & SNACKBAR UI CONFIGURATION
// =========================================================================
export const ROAD_UI = {
  CONFIRM_DIALOG_WIDTH: '380px',
  DETAILS_DIALOG_WIDTH: '2000px',
  DIALOG_MAX_WIDTH: '95vw',
  DIALOG_MAX_HEIGHT: '85vh',
  PANEL_CONFIRM: 'confirm-dialog-overlay-panel',
  PANEL_COMPONENT_DIALOG: 'component-dialog',
  PANEL_DETAILS_DIALOG: ['component-dialog', 'component-details-dialog'] as string[],

  SNACKBAR_ACTION_CLOSE: 'Close',
  SNACKBAR_DURATION_SUCCESS: 5000,
  SNACKBAR_DURATION_WARNING: 6000,
  SNACKBAR_DURATION_ERROR: 7000,
  SNACKBAR_PANEL_SUCCESS: ['snackbar-big', 'snackbar-success'] as string[],
  SNACKBAR_PANEL_WARNING: ['snackbar-big', 'snackbar-warning'] as string[],
  SNACKBAR_PANEL_ERROR: ['snackbar-big', 'snackbar-error'] as string[],
  SNACKBAR_POSITION_HORIZONTAL: 'center' as const,
  SNACKBAR_POSITION_VERTICAL: 'top' as const,
};

// =========================================================================
// API ENDPOINTS & ROUTES
// =========================================================================
export const ROAD_API = {
  BASE_URL: 'http://localhost:8080/api',
  ENDPOINT: '/road-components',
  AUDIT_SUBPATH: '/audit',
} as const;
