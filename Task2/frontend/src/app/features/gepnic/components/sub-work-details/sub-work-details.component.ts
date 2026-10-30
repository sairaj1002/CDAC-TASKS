import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { TenderDataService, SharedTenderData } from '../../services/tender-data.service';
import { SnackbarService } from '../../../../shared/services/snackbar.service';
import { GEPNIC_CONSTANTS } from '../../constants/gepnic.constants';

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

  // --- Awarding Working Tab Data ---
  awardingData = {
    workOrderNumber: 'WO/PWD/PUNE/2026/112',
    awardingDate: '2026-11-01',
    awardedAgency: 'M/s Infra Construction Ltd.',
    tenderRefNo: 'CE/PWD/PUNE/2026/044',
    agreementNumber: 'AGR-2026-091',
    acceptedBidValue: 6660150,
    performanceSecurity: 333000,
    completionPeriodMonths: 12,
    awardingStatus: 'In Progress'
  };

  // Dropdown options from centralized constants
  workCategories = [...GEPNIC_CONSTANTS.SUB_WORK_OPTIONS.WORK_CATEGORIES];
  programTypes = [...GEPNIC_CONSTANTS.SUB_WORK_OPTIONS.PROGRAM_TYPES];
  workTypes = [...GEPNIC_CONSTANTS.SUB_WORK_OPTIONS.WORK_TYPES];
  districts = [...GEPNIC_CONSTANTS.SUB_WORK_OPTIONS.DISTRICTS];
  schemes = [...GEPNIC_CONSTANTS.SUB_WORK_OPTIONS.SCHEMES];
  assets = [...GEPNIC_CONSTANTS.SUB_WORK_OPTIONS.ASSETS];

  constructor(
    private tenderDataService: TenderDataService,
    private snackbar: SnackbarService
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
  saveAwarding(): void {
    this.snackbar.success('Awarding and contract details saved successfully');
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
        awardingData: this.awardingData,
        tenderData: this.sharedTender
      });
      this.snackbar.success('Sub Work Details saved successfully');
    }
  }
}
