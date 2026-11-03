/** Constants used by the Building component. */
export const BUILDING_API = {
  ENDPOINT: '/buildings',
  OPTIONS_SUBPATH: '/options',
  AUDIT_SUBPATH: '/audit',
} as const;

export const BUILDING_VALIDATION = {
  DESCRIPTION_MAX_LENGTH: 1000,
  AREA_MIN: 0.01,
  COUNT_MIN: 0,
  PATTERN_INTEGER: /^\d+$/,
} as const;

export const BUILDING_CATEGORIES = {
  MAINTENANCE_TYPE: 'MAINTENANCE_TYPE',
  BUILDING_ACTIVITY: 'BUILDING_ACTIVITY',
} as const;

export const BUILDING_MESSAGES = {
  LOAD_MAINTENANCE_TYPES_ERROR: 'Unable to load maintenance types.',
  LOAD_ACTIVITIES_ERROR: 'Unable to load building activities.',
  LOAD_ERROR: 'Unable to load buildings.',
  UNSAVED_ROW: 'This row has unsaved data. Save it before closing.',
  CREATED_SUCCESS: 'Building added successfully.',
  UPDATED_SUCCESS: 'Building updated successfully.',
  DELETED_SUCCESS: 'Building deleted successfully.',
  SAVE_ERROR: 'Unable to save the building.',
  DELETE_ERROR: 'Unable to delete the building.',
  AUDIT_LOAD_ERROR: 'Unable to load audit details.',
  BACKEND_UNAVAILABLE: 'Cannot reach the backend. Start the backend and PostgreSQL, then try again.',
} as const;

export const BUILDING_UI = {
  DEFAULT_PAGE_SIZE: 10,
  PAGE_SIZE_OPTIONS: [5, 10, 20, 50] as const,
  CONFIRM_DIALOG_WIDTH: '400px',
  AUDIT_DIALOG_WIDTH: '900px',
  DIALOG_MAX_WIDTH: '95vw',
  DIALOG_MAX_HEIGHT: '85vh',
  AUDIT_PANEL_CLASS: 'audit-details-dialog',
  DELETE_PANEL_CLASS: 'delete-confirm-panel',
} as const;
