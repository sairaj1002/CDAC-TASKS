import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { TenderDataService, SharedTenderData } from '../../services/tender-data.service';
import { SnackbarService } from '../../../../shared/services/snackbar.service';
import { GEPNIC_CONSTANTS } from '../../constants/gepnic.constants';

export interface NonSchBoqItem {
  srNo: number;
  schName: string;
  goodsService: string;
  itemCode: string;
  itemDesc: string;
  hsnSac: string;
  uom: string;
  coefficient: string;
  quantity: number;
  type: string;
  rate?: number;
  amount?: number;
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

  // Form Model matching Image 1
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

  // Secondary Tabs: STRICTLY Technical Sanction, BOQ, Tender, NIT, Work Balances
  secondaryTabs: string[] = [...GEPNIC_CONSTANTS.SUB_WORK_OPTIONS.SECONDARY_TABS];
  activeSecondaryTab: string = 'Technical Sanction';

  // Shared Tender Push Data
  sharedTender!: SharedTenderData;

  // ==============================================================
  // BOQ ITEM DETAILS STATE (Matches Screenshot 2)
  // ==============================================================
  boqActiveSubTab: 'non-sch' | 'sch' = 'non-sch';
  commonHsnSac: string = '';
  quotePercentType: string = 'At par';
  quotePercentVal: string = '';
  roundOffAmount: number = 0;

  boqTableItems: NonSchBoqItem[] = [
    {
      srNo: 1,
      schName: '',
      goodsService: '',
      itemCode: '',
      itemDesc: '',
      hsnSac: '',
      uom: '',
      coefficient: '1.0',
      quantity: 0,
      type: 'At par',
      rate: 215,
      amount: 0
    }
  ];

  // --- Technical Sanction Data ---
  tsData = {
    tsNumber: 'TS/PWD/PUNE/2026/089',
    tsDate: '2026-10-15',
    tsAmount: 6660150,
    sanctioningAuthority: 'Chief Engineer, PWD Pune Region',
    tsStatus: 'Sanctioned',
    headOfAccount: '5054-04-800-01-02 State Highway Plan',
    remarks: 'Approved in accordance with IRC specifications and DSR 2026-27'
  };

  // --- Work Balances Data ---
  workBalanceData = {
    aaAmount: 7500000,
    tsAmount: 6660150,
    tenderAmount: 6660150,
    cumulativeExpenditure: 0,
    balancePhysicalWork: 6660150,
    uncommittedBalance: 839850
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

  clearField(field: keyof typeof this.formData): void {
    (this.formData as any)[field] = '';
  }

  // ==============================================================
  // BOQ Item Details Methods (Matches Screenshot 2)
  // ==============================================================
  addCommonHsnToAll(): void {
    if (!this.commonHsnSac.trim()) {
      this.snackbar.info('Please enter common HSN/SAC');
      return;
    }
    this.boqTableItems.forEach(item => item.hsnSac = this.commonHsnSac.trim());
    this.snackbar.success(`Applied HSN/SAC "${this.commonHsnSac}" to all items`);
  }

  addQuoteToAll(): void {
    this.boqTableItems.forEach(item => item.type = this.quotePercentType);
    this.snackbar.success(`Applied Quote% "${this.quotePercentType}" to all items`);
  }

  addBoqRow(): void {
    this.boqTableItems.push({
      srNo: this.boqTableItems.length + 1,
      schName: '',
      goodsService: '',
      itemCode: '',
      itemDesc: '',
      hsnSac: this.commonHsnSac || '',
      uom: '',
      coefficient: '1.0',
      quantity: 0,
      type: this.quotePercentType || 'At par',
      rate: 0,
      amount: 0
    });
    this.snackbar.info(`Added row ${this.boqTableItems.length}`);
  }

  removeBoqRow(index: number): void {
    if (this.boqTableItems.length > 1) {
      const removed = this.boqTableItems.splice(index, 1);
      this.boqTableItems.forEach((item, idx) => item.srNo = idx + 1);
      this.snackbar.info(`Removed row ${removed[0]?.srNo}`);
    } else {
      this.snackbar.info('At least one item row is required in the schedule');
    }
  }

  downloadBoqFormat(): void {
    this.snackbar.success('BOQ Schedule Excel format template downloaded');
  }

  triggerBoqUpload(): void {
    this.snackbar.info('Select BOQ Excel template to import');
  }

  saveBoqItemDetails(): void {
    const total = this.totalNonScheduledAmount;
    this.tenderDataService.updateTenderData({
      totalBoqAmount: total,
      estimatedCost: total
    });
    this.tsData.tsAmount = total;
    this.workBalanceData.tsAmount = total;
    this.workBalanceData.tenderAmount = total;
    this.workBalanceData.balancePhysicalWork = total;
    this.workBalanceData.uncommittedBalance = this.workBalanceData.aaAmount - total;
    this.snackbar.success(`Item Details saved. Agreement Amount: ₹ ${this.agreementAmountWithGst.toLocaleString()}`);
  }

  get totalNonScheduledAmount(): number {
    return this.boqTableItems.reduce((acc, curr) => {
      const qty = Number(curr.quantity) || 0;
      const rate = Number(curr.rate) || 0;
      return acc + (qty * rate);
    }, 0);
  }

  get totalNonScheduledAmountWithGst(): number {
    return Math.round(this.totalNonScheduledAmount * 1.18 * 100) / 100;
  }

  get agreementAmount(): number {
    return this.totalNonScheduledAmount + (Number(this.roundOffAmount) || 0);
  }

  get agreementAmountWithGst(): number {
    return this.totalNonScheduledAmountWithGst + (Number(this.roundOffAmount) || 0);
  }

  // --- Tender Actions ---
  saveTender(): void {
    this.tenderDataService.updateTenderData(this.sharedTender);
    this.snackbar.success('Tender parameters synchronized with GePNIC push payload');
  }

  resetTender(): void {
    const curr = this.tenderDataService.getCurrentValue();
    this.sharedTender = { ...curr };
    this.snackbar.info('Tender fields refreshed from push state');
  }

  // --- NIT Notice File Upload Handler ---
  onNitFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      this.sharedTender.uploadedNoticeFile = file.name;
      this.tenderDataService.updateTenderData({ uploadedNoticeFile: file.name });
      this.snackbar.success(`NIT notice file "${file.name}" attached successfully`);
    }
  }

  clearNitFile(): void {
    this.sharedTender.uploadedNoticeFile = '';
    this.tenderDataService.updateTenderData({ uploadedNoticeFile: '' });
  }

  saveNit(): void {
    this.tenderDataService.updateTenderData(this.sharedTender);
    this.snackbar.success(`NIT ${this.sharedTender.nitId} saved and synchronized`);
  }

  // --- Technical Sanction Actions ---
  saveTs(): void {
    this.snackbar.success(`Technical Sanction ${this.tsData.tsNumber} saved successfully`);
  }

  // --- Work Balances Actions ---
  saveWorkBalances(): void {
    this.snackbar.success('Work balances reconciled and saved with WAMIS ledger');
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
        boqItems: this.boqTableItems,
        tsData: this.tsData,
        tenderData: this.sharedTender,
        workBalances: this.workBalanceData
      });
      this.snackbar.success('Sub Work Details saved successfully');
    }
  }
}
