/**
 * Centralized constants for Bridge Component features.
 * Holds validation patterns, limits, error/success messages, dropdown categories,
 * activity types, audit defaults, dialog configurations, snackbar settings, and endpoints.
 */

// =========================================================================
// VALIDATION PATTERNS & FIELD LIMITS
// =========================================================================
export const BRIDGE_VALIDATION = {
  MAX_RIVER_NALLAH_BARRIER: 150,
  MAX_APPROVAL_ROAD: 150,
  MAX_OTHER_ACTIVITY_DESC: 500,
  MIN_WIDTH: 0,
  MIN_LENGTH: 0,
  MIN_NUMBER_OF_SPANS: 0,
  MIN_SPAN_LENGTH: 0,
  PATTERN_NON_WHITESPACE: /\S/,
  PATTERN_INTEGER_ONLY: /^\d+$/,
} as const;

// =========================================================================
// VALIDATION & UI MESSAGES
// =========================================================================
export const BRIDGE_MESSAGES = {
  // Validation messages
  RIVER_NALLAH_BARRIER_REQUIRED: 'River/Nallah/Barrier is required.',
  APPROVAL_ROAD_REQUIRED: 'Approval road is required.',
  BRIDGE_ACTIVITY_REQUIRED: 'Bridge activity is required.',
  OTHER_ACTIVITY_DESC_REQUIRED: 'Other activity description is required for Other Activity.',

  // Feedback notifications
  FORM_INVALID_WARNING: 'Please fill in all required fields marked in red before saving.',
  CREATED_SUCCESS: 'Bridge component created successfully.',
  UPDATED_SUCCESS: 'Bridge component updated successfully.',
  DELETED_SUCCESS: 'Bridge component deleted successfully.',
  SAVE_ERROR: 'Bridge component could not be saved.',
  DELETE_ERROR: 'Bridge component could not be deleted.',
  LOAD_ERROR: 'Bridge data could not be loaded.',
  ACTIVITIES_LOAD_ERROR: 'Bridge activities could not be loaded.',
} as const;

// =========================================================================
// DROPDOWN CATEGORIES & ACTIVITY TYPES
// =========================================================================
export const BRIDGE_CATEGORIES = {
  BRIDGE_ACTIVITY: 'BRIDGE_ACTIVITY',
} as const;

export const BRIDGE_ACTIVITIES = {
  OTHER_ACTIVITY_LABEL: 'Other Activity',
  OTHER_ACTIVITY_CODE: 'OTHER_ACTIVITY',
  REPAIR_TO_BRIDGE_LABEL: 'Repair-to-Bridge',
  REPAIR_TO_BRIDGE_CODE: 'REPAIR_TO_BRIDGE',
  NEW_CONSTRUCTION_LABEL: 'New Construction',
  NEW_CONSTRUCTION_CODE: 'NEW_CONSTRUCTION',
} as const;

export function isBridgeOtherActivity(activity: string | null | undefined): boolean {
  if (!activity) return false;
  const trimmed = activity.trim();
  return trimmed === BRIDGE_ACTIVITIES.OTHER_ACTIVITY_LABEL || trimmed === BRIDGE_ACTIVITIES.OTHER_ACTIVITY_CODE;
}

// =========================================================================
// AUDIT TABLE DEFAULTS
// =========================================================================
export const BRIDGE_AUDIT = {
  COLUMNS: ['remark', 'name', 'employeeName', 'designation', 'timestamp'] as string[],
  DEFAULT_EMPLOYEE_NAME: 'Admin',
  DEFAULT_DESIGNATION: 'Executive Engineer',
  DEFAULT_FALLBACK_NAME: 'Bridge',
  DEFAULT_REMARK_CREATED: 'Created',
  DEFAULT_REMARK_REVISION: 'Revision',
  DEFAULT_PAGE_SIZE: 5,
  DEFAULT_PAGE_INDEX: 0,
} as const;

// =========================================================================
// DIALOG & SNACKBAR UI CONFIGURATION
// =========================================================================
export const BRIDGE_UI = {
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
export const BRIDGE_API = {
  BASE_URL: 'http://localhost:8080/api',
  ENDPOINT: '/bridge-components',
  AUDIT_SUBPATH: '/audit',
} as const;
