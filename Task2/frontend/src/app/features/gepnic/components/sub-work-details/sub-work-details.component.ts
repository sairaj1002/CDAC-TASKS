import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { TenderDataService, SharedTenderData } from '../../services/tender-data.service';
import { SnackbarService } from '../../../../shared/services/snackbar.service';
import { DialogService } from '../../../../shared/services/dialog.service';
import { GEPNIC_CONSTANTS } from '../../constants/gepnic.constants';

export interface ContractAwardingRecord {
  id: number;
  registrationNo: string;
  status: string;
  contractAgreementNo: string;
  aaAmount: number;
  tsAmount: number;
  totalAmountExclusiveGst: number;
  totalAmountWithGst: number;
  item: string;
  auditDetails: string;
  attachmentsCount: number;
}

export interface SubWorkViewRecord {
  id: number;
  registrationNumber: string;
  subWorkName: string;
  workCategory: string;
  stipulatedStartDate: string;
  stipulatedEndDate: string;
  startDate: string;
  endDate: string;
  contractorName: string;
  agreementAmt: number;
  gstAgreementAmt: number;
  progressiveExpenditure: number;
  status?: string;
  closeStatus?: string;
  auditDetailsStatus?: string;
  hasReport?: boolean;
  hasAttachment?: boolean;
}

@Component({
  selector: 'app-sub-work-details',
  standalone: false,
  templateUrl: './sub-work-details.component.html'
})
export class SubWorkDetailsComponent implements OnInit {

  @Output() workSubmitted = new EventEmitter<any>();

  // Primary Tabs (Add | View)
  primaryTab: 'Add' | 'View' = 'Add';

  // Form Model matching Sub Work specifications
  formData = {
    registrationNumber: '',
    work: '',
    subWorkNumber: '',
    subWorkDescription: '',
    workCategory: '',
    programType: '',
    workType: '',
    district: '',
    scheme: '',
    isEmergency: false,
    isDepartmental: false,
    isLumpsum: false,
    remark: '',
    asset: ''
  };

  // Secondary Tabs: Work Type, Account Heads, AA & ES, Technical Sanction, Pretender, Awarding, Work Balances, Audit Details
  secondaryTabs: string[] = [...GEPNIC_CONSTANTS.SUB_WORK_OPTIONS.SECONDARY_TABS];
  activeSecondaryTab: string = 'Pretender';

  // Secondary tabs slated for future build
  futureTabs: string[] = [
    'Work Type',
    'Account Heads',
    'AA & ES',
    'Technical Sanction',
    'Work Balances',
    'Audit Details'
  ];

  // Shared Tender Push Data
  sharedTender!: SharedTenderData;

  // --- Awarding Contract Records State (Matches Image 4) ---
  searchContractQuery: string = '';
  contractPageSize: number = 10;
  contractPageIndex: number = 1;

  // Contract records (defaults empty matching image "No Contract records to display!")
  contractRecords: ContractAwardingRecord[] = [];

  // Selected contract record for view details
  selectedContractRecord: ContractAwardingRecord | null = null;
  showContractDetailsModal: boolean = false;

  // --- Sub Work View Demo Entries (Matches Image 2) ---
  subWorkRecords: SubWorkViewRecord[] = [
    {
      id: 1,
      registrationNumber: '2026100014/1',
      subWorkName: 'Repair, renovation and special repairs to existing Government buildings, including waterproofing, flooring and painting',
      workCategory: 'Construction Machinery',
      stipulatedStartDate: '',
      stipulatedEndDate: '',
      startDate: '-',
      endDate: '-',
      contractorName: '',
      agreementAmt: 0.00,
      gstAgreementAmt: 0.00,
      progressiveExpenditure: 0.00,
      hasReport: true,
      hasAttachment: true
    },
    {
      id: 2,
      registrationNumber: '2026100014/2',
      subWorkName: 'Electrical installations, illumination wiring and sub-station repair works for administrative block',
      workCategory: 'Electrical Works',
      stipulatedStartDate: '2026-04-01',
      stipulatedEndDate: '2026-10-31',
      startDate: '2026-04-15',
      endDate: '2026-10-15',
      contractorName: 'M/s Vidyut Power Controls',
      agreementAmt: 3450000.00,
      gstAgreementAmt: 4071000.00,
      progressiveExpenditure: 1200000.00,
      hasReport: true,
      hasAttachment: true
    },
    {
      id: 3,
      registrationNumber: '2026100014/3',
      subWorkName: 'Reconstruction of boundary walls and internal drainage channels in civil court premises',
      workCategory: 'Civil Works',
      stipulatedStartDate: '2026-05-10',
      stipulatedEndDate: '2026-11-30',
      startDate: '-',
      endDate: '-',
      contractorName: 'Apex Infra Projects',
      agreementAmt: 2150000.00,
      gstAgreementAmt: 2537000.00,
      progressiveExpenditure: 0.00,
      hasReport: true,
      hasAttachment: true
    }
  ];

  // Dropdown options from centralized constants
  workCategories = [...GEPNIC_CONSTANTS.SUB_WORK_OPTIONS.WORK_CATEGORIES];
  programTypes = [...GEPNIC_CONSTANTS.SUB_WORK_OPTIONS.PROGRAM_TYPES];
  workTypes = [...GEPNIC_CONSTANTS.SUB_WORK_OPTIONS.WORK_TYPES];
  districts = [...GEPNIC_CONSTANTS.SUB_WORK_OPTIONS.DISTRICTS];
  schemes = [...GEPNIC_CONSTANTS.SUB_WORK_OPTIONS.SCHEMES];
  assets = [...GEPNIC_CONSTANTS.SUB_WORK_OPTIONS.ASSETS];

  constructor(
    private tenderDataService: TenderDataService,
    private snackbar: SnackbarService,
    private dialog: DialogService
  ) {}

  ngOnInit(): void {
    this.tenderDataService.getTenderData().subscribe(data => {
      this.sharedTender = { ...data };
    });
  }

  setPrimaryTab(tab: 'Add' | 'View'): void {
    this.primaryTab = tab;
  }

  setSecondaryTab(tab: string): void {
    this.activeSecondaryTab = tab;
  }

  isFutureTab(tab: string): boolean {
    return this.futureTabs.includes(tab);
  }

  clearField(field: keyof typeof this.formData): void {
    (this.formData as any)[field] = '';
  }

  // --- Awarding Actions ---
  refreshContracts(): void {
    this.snackbar.info('Refreshing contract records...');
  }

  onAddAwarding(): void {
    this.snackbar.info('Add Awarding dialog will open here');
  }

  viewContractDetails(record: ContractAwardingRecord): void {
    this.selectedContractRecord = record;
    this.showContractDetailsModal = true;
  }

  closeContractDetails(): void {
    this.showContractDetailsModal = false;
    this.selectedContractRecord = null;
  }

  exportContractCsv(): void {
    this.snackbar.success('Exporting contract records to CSV...');
  }

  // --- Sub Work View Actions ---
  editSubWork(row: SubWorkViewRecord): void {
    this.snackbar.info(`Editing sub work ${row.registrationNumber}`);
  }

  deleteSubWork(row: SubWorkViewRecord): void {
    this.dialog.confirm({
      title: 'Delete Sub Work?',
      message: `Are you sure you want to delete sub work record ${row.registrationNumber}?`,
      okText: 'Ok',
      cancelText: 'Cancel'
    }).subscribe(confirmed => {
      if (confirmed) {
        this.subWorkRecords = this.subWorkRecords.filter(r => r.id !== row.id);
        this.snackbar.success(`Sub work ${row.registrationNumber} deleted.`);
      }
    });
  }

  closeSubWork(row: SubWorkViewRecord): void {
    this.snackbar.info(`Closing sub work ${row.registrationNumber}`);
  }

  viewAuditDetails(row: SubWorkViewRecord): void {
    this.dialog.showDetails(`Audit Details: ${row.registrationNumber}`, {
      'Registration Number': row.registrationNumber,
      'Sub Work Name': row.subWorkName,
      'Work Category': row.workCategory,
      'Agreement Amount': `₹ ${row.agreementAmt.toLocaleString()}`,
      'Audit Status': 'Verified against DSR 2026-27 & Clean'
    });
  }

  viewReport(row: SubWorkViewRecord): void {
    this.snackbar.success(`Generating report for ${row.registrationNumber}...`);
  }

  viewAttachments(row: SubWorkViewRecord): void {
    this.snackbar.info(`Viewing attachments for ${row.registrationNumber}`);
  }

  // Form Reset and Submission
  resetForm(): void {
    this.formData = {
      registrationNumber: '',
      work: '',
      subWorkNumber: '',
      subWorkDescription: '',
      workCategory: '',
      programType: '',
      workType: '',
      district: '',
      scheme: '',
      isEmergency: false,
      isDepartmental: false,
      isLumpsum: false,
      remark: '',
      asset: ''
    };
  }

  isFormValid(): boolean {
    return !!(
      this.formData.work &&
      this.formData.subWorkDescription &&
      this.formData.workCategory &&
      this.formData.programType &&
      this.formData.workType &&
      this.formData.district &&
      this.formData.scheme
    );
  }

  saveWork(): void {
    if (this.isFormValid()) {
      this.workSubmitted.emit({
        ...this.formData,
        tenderData: this.sharedTender
      });
      this.snackbar.success('Sub Work Details saved successfully');
    }
  }
}
