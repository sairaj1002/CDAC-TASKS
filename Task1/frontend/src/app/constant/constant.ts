/**
 * Centralized constant barrel file for the entire application.
 * Re-exports road, bridge, dropdown, and common application constants.
 */

export * from './bridgeconstant';
export * from './roadconstant';

import * as BridgeConst from './bridgeconstant';
import * as RoadConst from './roadconstant';

// =========================================================================
// DROPDOWN ADMIN CONSTANTS
// =========================================================================
export const DROPDOWN_ADMIN_CONSTANTS = {
  CODE_PATTERN: /^[A-Za-z0-9_]+$/,
  DEFAULT_CATEGORY: 'ALL',
  DEFAULT_DISPLAY_ORDER: 1,
  CONFIRM_DIALOG_WIDTH: '380px',
  CONFIRM_DIALOG_PANEL: 'confirm-dialog-overlay-panel',

  MESSAGES: {
    REQUIRED_FIELDS_WARNING: 'Please enter all required fields.',
    CATEGORIES_LOAD_ERROR: 'Failed to load dropdown categories.',
    OPTIONS_LOAD_ERROR: 'Failed to load dropdown options.',
    UPDATE_ERROR: 'Failed to update dropdown option.',
    CREATE_ERROR: 'Failed to create dropdown option.',
    STATUS_ERROR: 'Failed to change status.',
    DELETE_ERROR: 'Cannot delete dropdown option.',
    createdSuccess: (label: string) => `Dropdown option '${label}' created successfully.`,
    updatedSuccess: (label: string) => `Dropdown option '${label}' updated successfully.`,
    activatedSuccess: (label: string) => `Option '${label}' activated.`,
    deactivatedSuccess: (label: string) => `Option '${label}' deactivated.`,
    deletedSuccess: (label: string) => `Dropdown option '${label}' deleted successfully.`,
  },

  API_URL: 'http://localhost:8080/api/dropdowns',
  CATEGORIES_ENDPOINT: '/categories',

  DEFAULT_PREINIT_CATEGORIES: [
    'NATURE_OF_WORK',
    'ROAD_TYPE',
    'WORK_PLAN_TYPE',
    'CD_WORK_TYPE',
    'REPAIR_MAINTENANCE_TYPE',
    'BRIDGE_ACTIVITY',
  ] as const,
} as const;

// =========================================================================
// COMMON / SHARED APPLICATION CONSTANTS
// =========================================================================
export const COMMON_CONSTANTS = {
  API_BASE_URL: 'http://localhost:8080/api',
  SNACKBAR_ACTION: 'Close',
  SNACKBAR_DURATION_SUCCESS: 5000,
  SNACKBAR_DURATION_WARNING: 6000,
  SNACKBAR_DURATION_ERROR: 7000,
  SNACKBAR_PANEL_SUCCESS: ['snackbar-big', 'snackbar-success'] as string[],
  SNACKBAR_PANEL_WARNING: ['snackbar-big', 'snackbar-warning'] as string[],
  SNACKBAR_PANEL_ERROR: ['snackbar-big', 'snackbar-error'] as string[],
  SNACKBAR_HORIZONTAL_CENTER: 'center' as const,
  SNACKBAR_VERTICAL_TOP: 'top' as const,
};

// Namespaced exports
export const BRIDGE_CONSTANTS = BridgeConst;
export const ROAD_CONSTANTS = RoadConst;
