/** Constants shared by frontend features. */
export const COMMON_API = {
  BASE_URL: 'http://localhost:8080/api',
} as const;

export const COMMON_UI = {
  SNACKBAR_DURATION_SUCCESS: 5000,
  SNACKBAR_DURATION_WARNING: 6000,
  SNACKBAR_DURATION_ERROR: 7000,
  SNACKBAR_HORIZONTAL_POSITION: 'center' as const,
  SNACKBAR_VERTICAL_POSITION: 'top' as const,
  SNACKBAR_PANEL_CLASS: 'custom-feedback-snackbar',
} as const;
