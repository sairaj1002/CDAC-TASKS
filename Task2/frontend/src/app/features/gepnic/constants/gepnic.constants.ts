/**
 * Frontend centralized constants for GePNIC integration and WAMIS modules.
 */
export const GEPNIC_CONSTANTS = {
  BACKEND_API_BASE: 'http://localhost:8080/api/gepnic',

  ROUTES: {
    SUB_WORK: 'sub-work',
    PRE_TENDER: 'pretender',
    TENDER_PUSH: 'tender-push',
    TENDER_CORRIGENDUM_PULL: 'tender-corrigendum-pull',
    AWARDING: 'awarding'
  },

  SYSTEM_NAMES: {
    WAMIS: 'WAMIS',
    GEPNIC: 'GePNIC'
  },

  // Primary WAMIS Vertical Modules Rail: Strictly WMS and Integration
  RAIL_MODULES: [
    { id: 'WMS', name: 'WMS', icon: 'folder' },
    { id: 'Integration', name: 'Integration', icon: 'folder', active: true }
  ],

  // Integration Module Submenu Items (Under "Gepnic")
  GEPNIC_NAV_ITEMS: [
    { label: 'Tender Push', route: '/tender-push', icon: 'send' },
    { label: 'Tender & Corrigendum', route: '/tender-corrigendum-pull', icon: 'sync_alt' },
    { label: 'Awarding', route: '/awarding', icon: 'workspace_premium' }
  ],

  // WMS Module Submenu Items (Under "Works")
  WMS_NAV_ITEMS: [
    { label: 'Sub Work', route: '/sub-work', icon: 'assignment' }
  ],

  // Sub Work Details Reusable Options
  SUB_WORK_OPTIONS: {
    WORK_CATEGORIES: ['Civil Works', 'Electrical Works', 'Roads & Bridges', 'Public Buildings'],
    PROGRAM_TYPES: ['Construction', 'Special Repairs including RRR', 'Maintenance', 'Other'],
    WORK_TYPES: ['Maintenance', 'Original Construction', 'Widening / Strengthening', 'Reconstruction'],
    DISTRICTS: ['Pune', 'Mumbai Suburban', 'Nagpur', 'Thane', 'Nashik', 'Aurangabad'],
    SCHEMES: [
      'Mukhya Mantri Gram Sadak Yojana (MMGSY)',
      'State Highway Improvement (SHI)',
      'Central Road Fund (CRF)'
    ],
    ASSETS: [
      'State Highway SH-27',
      'Major District Road MDR-12',
      'Rural Road RR-09',
      'District Hospital Complex'
    ],
    SECONDARY_TABS: [
      'Work Type',
      'Account Heads',
      'AA & ES',
      'Technical Sanction',
      'Pretender',
      'Awarding',
      'Work Balances',
      'Audit Details'
    ],
    TS_STATUSES: ['Sanctioned', 'Under Revision', 'Pending Compliance']
  },

  // Integration Operations
  OPERATIONS: {
    TENDER_PUSH: 'TENDER_PUSH',
    TENDER_PULL: 'TENDER_PULL',
    CORRIGENDUM_PULL: 'CORRIGENDUM_PULL',
    DOCUMENT_UPLOAD: 'DOCUMENT_UPLOAD',
    AOC_PULL: 'AOC_PULL'
  },

  OPERATION_OPTIONS: [
    { value: '', label: 'All Operations' },
    { value: 'TENDER_PUSH', label: 'Tender Push' },
    { value: 'TENDER_PULL', label: 'Tender Pull' },
    { value: 'CORRIGENDUM_PULL', label: 'Corrigendum Pull' },
    { value: 'DOCUMENT_UPLOAD', label: 'Document Upload' },
    { value: 'AOC_PULL', label: 'AOC Pull' }
  ],

  // Transaction Statuses
  STATUS: {
    PENDING: 'PENDING',
    IN_PROGRESS: 'IN_PROGRESS',
    SUCCESS: 'SUCCESS',
    FAILED: 'FAILED',
    REJECTED: 'REJECTED',
    RETRIED: 'RETRIED'
  },

  STATUS_OPTIONS: [
    { value: '', label: 'All Statuses' },
    { value: 'SUCCESS', label: 'SUCCESS' },
    { value: 'FAILED', label: 'FAILED' },
    { value: 'IN_PROGRESS', label: 'IN_PROGRESS' },
    { value: 'RETRIED', label: 'RETRIED' }
  ],

  // Document Categories / Codes
  DOCUMENT_CODES: [
    { value: 'NITDOC', label: 'NIT Document (.pdf)' },
    { value: 'BOQDOC', label: 'BOQ Document (.xls, .xlsx)' },
    { value: 'ADDDOC', label: 'Additional Document (.pdf, .rar, .jpg)' }
  ],

  // System Diagnostics Verification Scopes
  VERIFICATION_SCOPES: [
    'All Metadata & Documents',
    'Document Covers Only',
    'Envelope Checksum Only'
  ],

  // Diagnostics 7-Stage Flow
  DIAGNOSTIC_STAGES: [
    { id: 1, name: '1. WAMIS JSON' },
    { id: 2, name: '2. Mapped GePNIC' },
    { id: 3, name: '3. Validation' },
    { id: 4, name: '4. Encrypted Envelope' },
    { id: 5, name: '5. Response Envelope' },
    { id: 6, name: '6. Decrypted Response' },
    { id: 7, name: '7. Final Result' }
  ],

  // Environment Options
  ENVIRONMENTS: [
    { value: 'dev', label: 'Development (Synthetic diagnostics enabled)' },
    { value: 'test', label: 'Test / Staging' },
    { value: 'prod', label: 'Production (Hardened security)' }
  ]
};
