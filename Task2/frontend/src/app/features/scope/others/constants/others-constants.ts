/** Constants used by the Others component and its shared Unit lookup. */
export const OTHERS_API = {
  ENDPOINT: '/others',
  AUDIT_SUBPATH: '/audit',
} as const;

export const UNIT_API = {
  ENDPOINT: '/units',
} as const;

export const OTHERS_VALIDATION = {
  DESCRIPTION_MAX_LENGTH: 1000,
  QUANTITY_MIN: 0,
} as const;

export const OTHERS_MESSAGES = {
  UNITS_LOAD_ERROR: 'Unable to load units. Please check the backend connection.',
  LOAD_ERROR: 'Unable to load Others records.',
  UNSAVED_ROW: 'This row has unsaved data. Save it before closing.',
  CREATED_SUCCESS: 'Record added successfully.',
  UPDATED_SUCCESS: 'Record updated successfully.',
  DELETED_SUCCESS: 'Record deleted successfully.',
  SAVE_ERROR: 'Unable to save record.',
  DELETE_ERROR: 'Unable to delete record.',
  AUDIT_LOAD_ERROR: 'Unable to load audit details.',
  BACKEND_UNAVAILABLE: 'Cannot reach the backend. Start the backend and PostgreSQL, then try again.',
} as const;

export const OTHERS_UI = {
  DEFAULT_PAGE_SIZE: 10,
  PAGE_SIZE_OPTIONS: [5, 10, 20, 50] as const,
  CONFIRM_DIALOG_WIDTH: '400px',
  AUDIT_DIALOG_WIDTH: '900px',
  DIALOG_MAX_WIDTH: '95vw',
  DIALOG_MAX_HEIGHT: '85vh',
  AUDIT_PANEL_CLASS: 'audit-details-dialog',
  DELETE_PANEL_CLASS: 'delete-confirm-panel',
} as const;
