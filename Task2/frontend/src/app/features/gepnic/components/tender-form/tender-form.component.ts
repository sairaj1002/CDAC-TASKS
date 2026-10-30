import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { catchError, forkJoin, of } from 'rxjs';
import { MASTER_DEFINITIONS, MasterOption, TenderDraft, emptyTender } from '../../models/pretender.model';
import { PretenderApiService } from '../../services/pretender-api.service';
import { SnackbarService } from '../../../../shared/services/snackbar.service';
import { WorkSummary } from '../../models/work-summary.model';
import { WorkDetailRecord } from '../work-detail-table/work-detail-table.component';

@Component({
  selector: 'app-tender-form',
  standalone: false,
  templateUrl: './tender-form.component.html',
  styleUrls: ['./tender-form.component.css']
})
export class TenderFormComponent implements OnInit {

  @Input() initialData: any = null;
  @Output() formSubmit = new EventEmitter<any>();

  tender: TenderDraft = {
    ...emptyTender(),
    tenderReferenceNumber: 'CE/PWD/PUNE/2026/089',
    parentWorkId: 'SW-001',
    parentWorkNumber: 'SWRN-2026-0001',
    parentWorkDescription: 'Widening and Strengthening of State Highway SH-27 (Km 12/00 to 24/500)',
    procurementType: 'Open Tender',
    tenderTypeId: 8,
    numberOfCoversId: 2,
    tenderCategoryId: 1,
    workItemProductCategoryId: 1,
    numberOfBidOpenersId: 2,
    formOfContractId: 1,
    bidValidityDays: 120,
    cover1Description: 'Fee / Pre-qualification / Technical Bid Documents',
    cover2Description: 'Financial Bid / BOQ Price Schedule',
    deliveryPeriodDays: 365,
    tendererClassId: 1,
    emdExemption: false,
    paperCostExemption: false,
    tenderFee: 5000,
    tenderEmdFee: 133200,
    preBidMeeting: true,
    preBidMeetingAddress: 'Superintending Engineer Office, PWD Circle Pune - 411001',
    tenderPublishingDateTime: '2026-11-01T10:00',
    documentDownloadStartDateTime: '2026-11-01T11:00',
    seekClarificationStartDateTime: '2026-11-02T10:00',
    seekClarificationEndDateTime: '2026-11-05T17:00',
    bidSubmissionStartDateTime: '2026-11-06T10:00',
    bidSubmissionEndDateTime: '2026-11-15T17:00',
    bidOpeningDateTime: '2026-11-16T11:00'
  };

  options: Record<string, MasterOption[]> = {};
  mastersLoading = false;
  submitting = false;

  constructor(
    private readonly api: PretenderApiService,
    private readonly snackbar: SnackbarService
  ) {}

  ngOnInit(): void {
    this.loadMasters();
    if (this.initialData) {
      this.tender = { ...this.tender, ...this.initialData };
    }
  }

  loadMasters(): void {
    this.mastersLoading = true;
    forkJoin(
      MASTER_DEFINITIONS.map(master =>
        this.api.listMasters(master.key).pipe(catchError(() => of(null)))
      )
    ).subscribe({
      next: lists => {
        this.options = {};
        lists.forEach((list, index) => {
          if (list && list.length > 0) {
            this.options[MASTER_DEFINITIONS[index].key] = list;
          } else {
            // Supply fallback options if DB table is unseeded
            this.options[MASTER_DEFINITIONS[index].key] = this.getFallbackMasters(MASTER_DEFINITIONS[index].key);
          }
        });
        this.mastersLoading = false;
      },
      error: () => {
        this.mastersLoading = false;
        MASTER_DEFINITIONS.forEach(m => {
          this.options[m.key] = this.getFallbackMasters(m.key);
        });
      }
    });
  }

  list(key: string): MasterOption[] {
    return this.options[key] && this.options[key].length > 0
      ? this.options[key]
      : this.getFallbackMasters(key);
  }

  tenderCategories(): MasterOption[] {
    return this.list('tender-category');
  }

  productCategoryOptions(): MasterOption[] {
    const list = this.list('work-item-product-category');
    if (!this.tender.tenderCategoryId) {
      return list;
    }
    const filtered = list.filter(item => item.tenderCategoryId === this.tender.tenderCategoryId);
    return filtered.length > 0 ? filtered : list;
  }

  changeTenderCategory(categoryId: number | null): void {
    this.tender.tenderCategoryId = categoryId;
    const available = this.productCategoryOptions();
    if (this.tender.workItemProductCategoryId && !available.some(o => o.id === this.tender.workItemProductCategoryId)) {
      this.tender.workItemProductCategoryId = null;
    }
  }

  selectWork(work: WorkSummary): void {
    this.tender.parentWorkId = work.id;
    this.tender.parentWorkNumber = work.workNumber;
    this.tender.parentWorkDescription = work.description;
    this.snackbar.success(`Selected work ${work.workNumber}`);
  }

  clearWork(): void {
    this.tender.parentWorkId = null;
    this.tender.parentWorkNumber = null;
    this.tender.parentWorkDescription = null;
  }

  onWorkDetailSelect(work: WorkDetailRecord): void {
    this.tender.parentWorkNumber = work.workRegistrationNumber;
    this.tender.parentWorkDescription = work.nameOfWork;
    this.snackbar.info(`Work details linked to ${work.workRegistrationNumber}`);
  }

  submit(): void {
    if (!this.tender.tenderReferenceNumber) {
      this.snackbar.error('Tender Reference Number is required.');
      return;
    }
    this.submitting = true;
    this.formSubmit.emit({
      wamisTenderId: this.tender.tenderReferenceNumber,
      officeId: 'OFF-001 (Pune Division)',
      tenderTitle: this.tender.parentWorkDescription || 'Infrastructure Works',
      estimatedCost: this.tender.tenderFee ? this.tender.tenderFee * 100 : 2500000,
      publishDate: this.tender.tenderPublishingDateTime,
      submissionEndDate: this.tender.bidSubmissionEndDateTime,
      tenderData: this.tender
    });
    setTimeout(() => {
      this.submitting = false;
    }, 500);
  }

  resetForm(): void {
    this.tender = emptyTender();
    this.snackbar.info('Tender push form reset');
  }

  private getFallbackMasters(key: string): MasterOption[] {
    switch (key) {
      case 'tender-type':
        return [
          { id: 8, name: 'Open Tender', code: 'OPEN' },
          { id: 9, name: 'Limited Tender', code: 'LIMITED' },
          { id: 10, name: 'Single Tender', code: 'SINGLE' }
        ];
      case 'number-of-covers':
        return [
          { id: 1, name: '1 Cover' },
          { id: 2, name: '2 Covers' },
          { id: 3, name: '3 Covers' },
          { id: 4, name: '4 Covers' }
        ];
      case 'tender-category':
        return [
          { id: 1, name: 'Works', code: 'WORKS' },
          { id: 2, name: 'Goods', code: 'GOODS' },
          { id: 3, name: 'Services', code: 'SERVICES' }
        ];
      case 'work-item-product-category':
        return [
          { id: 1, name: 'Civil Works', tenderCategoryId: 1 },
          { id: 2, name: 'Electrical Works', tenderCategoryId: 1 },
          { id: 3, name: 'Roads & Bridges', tenderCategoryId: 1 },
          { id: 4, name: 'Public Health Engineering', tenderCategoryId: 1 }
        ];
      case 'number-of-bid-openers':
        return [
          { id: 2, name: '2 Bid Openers', code: '2', bovalue: 2 },
          { id: 3, name: '3 Bid Openers', code: '3', bovalue: 3 }
        ];
      case 'form-of-contract':
        return [
          { id: 1, name: 'Item Rate' },
          { id: 2, name: 'Percentage' },
          { id: 3, name: 'Lump-sum' },
          { id: 4, name: 'Turnkey' }
        ];
      case 'tenderer-class':
        return [
          { id: 1, name: 'Class I', code: 'CLASS_1' },
          { id: 2, name: 'Class II', code: 'CLASS_2' },
          { id: 3, name: 'Class III', code: 'CLASS_3' },
          { id: 4, name: 'Class IV', code: 'CLASS_4' }
        ];
      default:
        return [];
    }
  }
}
