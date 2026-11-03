/**
 * Centralized constant barrel file for the entire application.
 * Re-exports road, bridge, dropdown, and common application constants.
 */

export * from '../../bridge/models/bridgeconstant';
export * from '../../road/models/roadconstant';

import * as BridgeConst from '../../bridge/models/bridgeconstant';
import * as RoadConst from '../../road/models/roadconstant';

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
    'REPAIR_MAINTENANCE_TYPE',
    'BRIDGE_ACTIVITY',
  ] as const,
} as const;

// =========================================================================
// COMMON / SHARED APPLICATION CONSTANTS
// =========================================================================
// Namespaced exports
export const BRIDGE_CONSTANTS = BridgeConst;
export const ROAD_CONSTANTS = RoadConst;
