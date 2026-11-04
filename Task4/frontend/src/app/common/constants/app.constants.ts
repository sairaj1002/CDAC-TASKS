/**
 * Centralized Application Constants for WAMIS Frontend
 * Strictly maintained for configurable, maintainable, and uniform usage.
 */
export const APP_CONSTANTS = {
  APP_NAME: 'WAMIS',
  APP_FULL_NAME: 'Works & Accounts Management Information System',
  MODULE_NAME: 'GePNIC e-Procurement Pre-Tender Integration',
  ORGANIZATION: 'Centre for Development of Advanced Computing (C-DAC)',
  DEPARTMENT: 'Public Works Department (PWD)',

  API_BASE_URL: 'http://localhost:9090/api',

  ENDPOINTS: {
    PRE_TENDERS: '/pre-tenders',
    PRE_TENDERS_VALIDATE: (id: number) => `/pre-tenders/${id}/validate`,
    PRE_TENDERS_SUBMIT: (id: number) => `/pre-tenders/${id}/integration-jobs`,
    PRE_TENDERS_BATCH: '/pre-tenders/batch',
    
    MASTERS_DROPDOWNS: '/masters/dropdowns',
    MASTERS_BY_CATEGORY: (cat: string) => `/masters/dropdowns/${cat}`,
    
    GEPNIC_TENDERS_PUSH: '/gepnic/tenders/push',
    GEPNIC_DOCS_UPLOAD: '/gepnic/documents/upload',
    GEPNIC_TENDERS_PULL: '/gepnic/tenders/pull',
    GEPNIC_CORRIGENDUM_PULL: '/gepnic/corrigendums/pull',
    GEPNIC_AOC_PULL: '/gepnic/aoc/pull',
    GEPNIC_TRANSACTIONS: '/gepnic/transactions',
    GEPNIC_TRANSACTION_BY_ID: (id: number) => `/gepnic/transactions/${id}`,
    GEPNIC_TRANSACTION_AUDIT: (id: number) => `/gepnic/transactions/${id}/audit`,
    GEPNIC_TRANSACTION_RETRY: (id: number) => `/gepnic/transactions/${id}/retry`,
    GEPNIC_CONFIG: '/gepnic/configuration',

    INTEGRATION_JOB_STATUS: (id: number) => `/integration-jobs/${id}`,
    INTEGRATION_JOB_HISTORY: (id: number) => `/integration-jobs/${id}/history`,
  },

  DROPDOWN_CATEGORIES: {
    TENDER_TYPE: 'TENDER_TYPE',
    FORM_OF_CONTRACT: 'FORM_OF_CONTRACT',
    TENDER_CATEGORY: 'TENDER_CATEGORY',
    PRODUCT_CATEGORY: 'PRODUCT_CATEGORY',
    TENDERER_CLASS: 'TENDERER_CLASS',
    BID_OPENERS: 'BID_OPENERS',
    DOC_CODE: 'DOC_CODE',
    DIVISION_OFFICE: 'DIVISION_OFFICE',
  },

  TABLE_COLUMNS: {
    WORK_LIST: [
      'srNo',
      'workNumber',
      'workDesc',
      'tenderRefNo',
      'tenderType',
      'tenderCategory',
      'tenderAmount',
      'viewJson',
      'viewDetails',
      'documents',
      'action',
      'transfer',
      'sendGepnicData',
      'uploadGepnicFiles',
      'sendBack',
      'finalize',
      'auditDetails',
    ],
    AUDIT_LIST: ['remark', 'name', 'employeeName', 'designation', 'timestamp'],
    DROPDOWN_MASTER_LIST: ['id', 'category', 'code', 'displayName', 'displayOrder', 'active', 'action'],
    TRANSACTION_LIST: ['id', 'operation', 'correlationId', 'status', 'attempts', 'externalRef', 'createdAt', 'action'],
  },

  PAGINATION: {
    DEFAULT_PAGE_SIZE: 5,
    PAGE_SIZE_OPTIONS: [5, 10, 20, 50],
  },

  SNACKBAR: {
    DURATION_MS: 4000,
    PANEL_CLASSES: {
      ERROR: 'wamis-snackbar-error',
      SUCCESS: 'wamis-snackbar-success',
      INFO: 'wamis-snackbar-info',
      WARNING: 'wamis-snackbar-warning',
    },
  },

  CONFIRMATION: {
    DELETE: {
      title: 'Delete Confirmation',
      message: 'Are you sure you want to delete this record? This action cannot be undone.',
      confirmButtonText: 'Delete',
      cancelButtonText: 'Cancel',
    },
    EDIT: {
      title: 'Edit Confirmation',
      message: 'Do you want to proceed with editing this Pre-Tender record?',
      confirmButtonText: 'Proceed',
      cancelButtonText: 'Cancel',
    },
    UPDATE: {
      title: 'Update Confirmation',
      message: 'Are you sure you want to save these modifications?',
      confirmButtonText: 'Save Changes',
      cancelButtonText: 'Cancel',
    },
    GEPNIC_PUSH: {
      title: 'Send Pre-Tender Data to GePNIC',
      message: 'Are you sure you want to encrypt and dispatch this tender data to GePNIC portal?',
      confirmButtonText: 'Submit to GePNIC',
      cancelButtonText: 'Cancel',
    },
    FINALIZE: {
      title: 'Finalize Pre-Tender',
      message: 'Finalizing will lock this Pre-Tender record against further modifications. Proceed?',
      confirmButtonText: 'Finalize',
      cancelButtonText: 'Cancel',
    },
  },
};

