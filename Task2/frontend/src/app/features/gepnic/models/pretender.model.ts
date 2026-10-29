export interface MasterOption {
  id: number;
  name: string;
  code?: string | null;
  description?: string | null;
  bovalue?: number | null;
  tenderCategoryId?: number | null;
  tenderCategoryName?: string | null;
  sourceRowKey?: string | null;
}

export type MasterFieldKey = 'name' | 'code' | 'description' | 'bovalue' | 'tenderCategoryId';

export interface MasterFieldDefinition {
  key: MasterFieldKey;
  label: string;
  type: 'text' | 'number' | 'tender-category';
  required: boolean;
}

export interface MasterDefinition {
  key: string;
  label: string;
  fields: MasterFieldDefinition[];
}

export interface MasterOptionDraft {
  name: string;
  code: string;
  description: string;
  bovalue: number | null;
  tenderCategoryId: number | null;
}

export interface TenderDraft {
  procurementType: string;
  tenderReferenceNumber: string;
  parentWorkId: string | null;
  parentWorkNumber: string | null;
  parentWorkDescription: string | null;
  tenderTypeId: number | null;
  numberOfCoversId: number | null;
  tenderCategoryId: number | null;
  workItemProductCategoryId: number | null;
  numberOfBidOpenersId: number | null;
  formOfContractId: number | null;
  bidValidityDays: number | null;
  cover1Description: string;
  cover2Description: string;
  deliveryPeriodDays: number | null;
  tendererClassId: number | null;
  emdExemption: boolean;
  paperCostExemption: boolean;
  tenderFee: number | null;
  tenderEmdFee: number | null;
  preBidMeeting: boolean;
  preBidMeetingAddress: string;
  tenderPublishingDateTime: string;
  documentDownloadStartDateTime: string;
  seekClarificationStartDateTime: string;
  seekClarificationEndDateTime: string;
  bidSubmissionStartDateTime: string;
  bidSubmissionEndDateTime: string;
  bidOpeningDateTime: string;
  nitFileName: string;
  dtcnFileName: string;
  otherFileName: string;
}

export interface TenderRecord extends TenderDraft {
  id: number;
  tenderType: string;
  tenderCategory: string;
  numberOfCovers: string;
  workItemProductCategory: string;
  numberOfBidOpeners: string;
  formOfContract: string;
  tendererClass: string;
}

const field = (key: MasterFieldKey, label: string, type: MasterFieldDefinition['type'] = 'text', required = true): MasterFieldDefinition => ({
  key,
  label,
  type,
  required
});

export const MASTER_DEFINITIONS: MasterDefinition[] = [
  { key: 'tender-type', label: 'Tender Type', fields: [field('code', 'Tender Type Code'), field('description', 'Description')] },
  { key: 'number-of-covers', label: 'Number of Covers', fields: [field('name', 'Number of Covers')] },
  { key: 'tender-category', label: 'Tender Category', fields: [field('code', 'Category'), field('description', 'Description')] },
  { key: 'work-item-product-category', label: 'Work Item Product Category', fields: [field('name', 'Product Category'), field('tenderCategoryId', 'Tender Category', 'tender-category')] },
  { key: 'number-of-bid-openers', label: 'Number of Bid Openers', fields: [field('code', 'Bid Opener Type'), field('bovalue', 'Bid Opener Value', 'number'), field('description', 'Description')] },
  { key: 'form-of-contract', label: 'Form of Contract', fields: [field('name', 'Form of Contract')] },
  { key: 'tenderer-class', label: 'Tenderer Class', fields: [field('code', 'Tenderer Class Code'), field('description', 'Description')] },
];

export const emptyTender = (): TenderDraft => ({
  procurementType: 'Open Tender',
  tenderReferenceNumber: '',
  parentWorkId: null,
  parentWorkNumber: null,
  parentWorkDescription: null,
  tenderTypeId: null,
  numberOfCoversId: null,
  tenderCategoryId: null,
  workItemProductCategoryId: null,
  numberOfBidOpenersId: null,
  formOfContractId: null,
  bidValidityDays: null,
  cover1Description: '',
  cover2Description: '',
  deliveryPeriodDays: null,
  tendererClassId: null,
  emdExemption: false,
  paperCostExemption: false,
  tenderFee: null,
  tenderEmdFee: null,
  preBidMeeting: false,
  preBidMeetingAddress: '',
  tenderPublishingDateTime: '',
  documentDownloadStartDateTime: '',
  seekClarificationStartDateTime: '',
  seekClarificationEndDateTime: '',
  bidSubmissionStartDateTime: '',
  bidSubmissionEndDateTime: '',
  bidOpeningDateTime: '',
  nitFileName: '',
  dtcnFileName: '',
  otherFileName: '',
});

