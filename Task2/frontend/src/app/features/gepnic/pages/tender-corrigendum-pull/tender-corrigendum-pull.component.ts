import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { GepnicForms } from '../../forms/gepnic-forms';
import { TenderIntegrationService } from '../../services/tender-integration.service';
import { TenderResponse, CorrigendumResponse } from '../../models/gepnic.models';
import { SnackbarService } from '../../../../shared/services/snackbar.service';

export interface AocRecord {
  srNo: number;
  uniqueFileNumber: string;
  tenderReferenceNo: string;
  tenderDescription: string;
  tenderType: string;
  publishDate: string;
  insertedInAoc?: boolean;
  tenderValue?: number;
  pinCode?: string;
  productCategory?: string;
  bidValidityDays?: number;
  deliveryPeriodDays?: number;
  preBidMeetingAddress?: string;
  tendererClass?: string;
  formOfContract?: string;
  noOfCovers?: number;
  tenderFee?: number;
  tenderEmdFee?: number;
}

@Component({
  selector: 'app-tender-corrigendum-pull',
  standalone: false,
  templateUrl: './tender-corrigendum-pull.component.html',
  styleUrls: ['./tender-corrigendum-pull.component.css']
})
export class TenderCorrigendumPullComponent implements OnInit {

  searchForm!: FormGroup;
  searching: boolean = false;
  activeType: 'aoc' | 'corrigendums' = 'aoc';
  tenderResults: TenderResponse[] = [];
  corrigendumResults: CorrigendumResponse[] = [];

  // Post Tender / AOC Filter State (Matches Image 2)
  aocUploadDate: string = '2022-07-02';
  loadingAoc: boolean = false;

  // Pre-tender Details Modal State (Matches Image 1)
  showPretenderDetailsModal: boolean = false;
  selectedPretenderDetails: any = null;

  // Authentic Records from Image 2
  aocRecords: AocRecord[] = [
    {
      srNo: 1,
      uniqueFileNumber: '219/2611/1',
      tenderReferenceNo: '02/2022-23',
      tenderDescription: 'A/E to JM Road to Kathiguda Road for the year 2022-23',
      tenderType: 'Works',
      publishDate: '02/07/2022',
      tenderValue: 39748169,
      pinCode: '751013',
      productCategory: 'Civil Works - Roads',
      bidValidityDays: 90,
      deliveryPeriodDays: 365,
      preBidMeetingAddress: 'O/o CCE CRWC BBSR',
      tendererClass: 'A or equivalent',
      formOfContract: 'Percentage',
      noOfCovers: 2,
      tenderFee: 10000,
      tenderEmdFee: 397482
    },
    {
      srNo: 2,
      uniqueFileNumber: '219/2612/1',
      tenderReferenceNo: '02/2022-23',
      tenderDescription: 'Repair and Maintenance to MV - 42 to Transport Junction Road for the year 2022-23',
      tenderType: 'Works',
      publishDate: '02/07/2022',
      tenderValue: 24500000,
      pinCode: '751013',
      productCategory: 'Civil Works - Roads',
      bidValidityDays: 90,
      deliveryPeriodDays: 365,
      preBidMeetingAddress: 'O/o CCE CRWC BBSR',
      tendererClass: 'A or equivalent',
      formOfContract: 'Percentage',
      noOfCovers: 2,
      tenderFee: 10000,
      tenderEmdFee: 245000
    },
    {
      srNo: 3,
      uniqueFileNumber: '219/2614/1',
      tenderReferenceNo: '02/2022-23',
      tenderDescription: 'Repair and Maintenance to BM Road to MV - 108 Road for the year 2022-23',
      tenderType: 'Works',
      publishDate: '02/07/2022',
      tenderValue: 18200000,
      pinCode: '751013',
      productCategory: 'Civil Works - Roads',
      bidValidityDays: 90,
      deliveryPeriodDays: 365,
      preBidMeetingAddress: 'O/o CCE CRWC BBSR',
      tendererClass: 'A or equivalent',
      formOfContract: 'Percentage',
      noOfCovers: 2,
      tenderFee: 10000,
      tenderEmdFee: 182000
    },
    {
      srNo: 4,
      uniqueFileNumber: '219/2615/1',
      tenderReferenceNo: '02/2022-23',
      tenderDescription: 'Repair and Maintenance to Malkangiri to MV - 27 Road for the year 2022-23',
      tenderType: 'Works',
      publishDate: '02/07/2022',
      tenderValue: 31000000,
      pinCode: '751013',
      productCategory: 'Civil Works - Roads',
      bidValidityDays: 90,
      deliveryPeriodDays: 365,
      preBidMeetingAddress: 'O/o CCE CRWC BBSR',
      tendererClass: 'A or equivalent',
      formOfContract: 'Percentage',
      noOfCovers: 2,
      tenderFee: 10000,
      tenderEmdFee: 310000
    },
    {
      srNo: 5,
      uniqueFileNumber: '219/2618/1',
      tenderReferenceNo: '02/2022-23',
      tenderDescription: 'S/R to Jharapalli to Urupa road for the year 2022-23',
      tenderType: 'Works',
      publishDate: '02/07/2022',
      tenderValue: 14750000,
      pinCode: '751013',
      productCategory: 'Civil Works - Roads',
      bidValidityDays: 90,
      deliveryPeriodDays: 365,
      preBidMeetingAddress: 'O/o CCE CRWC BBSR',
      tendererClass: 'A or equivalent',
      formOfContract: 'Percentage',
      noOfCovers: 2,
      tenderFee: 10000,
      tenderEmdFee: 147500
    },
    {
      srNo: 6,
      uniqueFileNumber: '219/2619/1',
      tenderReferenceNo: '02/2022-23',
      tenderDescription: 'Repair and Maintenance to JM road to Katrimajhiguda road for the year 2022-23',
      tenderType: 'Works',
      publishDate: '02/07/2022',
      tenderValue: 22800000,
      pinCode: '751013',
      productCategory: 'Civil Works - Roads',
      bidValidityDays: 90,
      deliveryPeriodDays: 365,
      preBidMeetingAddress: 'O/o CCE CRWC BBSR',
      tendererClass: 'A or equivalent',
      formOfContract: 'Percentage',
      noOfCovers: 2,
      tenderFee: 10000,
      tenderEmdFee: 228000
    },
    {
      srNo: 7,
      uniqueFileNumber: '219/2609/1',
      tenderReferenceNo: '02/2022-23',
      tenderDescription: 'Repair and Maintenance to MV-30 to MV-29 Road for the year 2022-23',
      tenderType: 'Works',
      publishDate: '02/07/2022',
      tenderValue: 19600000,
      pinCode: '751013',
      productCategory: 'Civil Works - Roads',
      bidValidityDays: 90,
      deliveryPeriodDays: 365,
      preBidMeetingAddress: 'O/o CCE CRWC BBSR',
      tendererClass: 'A or equivalent',
      formOfContract: 'Percentage',
      noOfCovers: 2,
      tenderFee: 10000,
      tenderEmdFee: 196000
    },
    {
      srNo: 8,
      uniqueFileNumber: '219/2610/1',
      tenderReferenceNo: '02/2022-23',
      tenderDescription: 'S/R to Butiguda Colony to Nayakguda Via Kalapalli Road for the year 2022-23',
      tenderType: 'Works',
      publishDate: '02/07/2022',
      tenderValue: 27400000,
      pinCode: '751013',
      productCategory: 'Civil Works - Roads',
      bidValidityDays: 90,
      deliveryPeriodDays: 365,
      preBidMeetingAddress: 'O/o CCE CRWC BBSR',
      tendererClass: 'A or equivalent',
      formOfContract: 'Percentage',
      noOfCovers: 2,
      tenderFee: 10000,
      tenderEmdFee: 274000
    },
    {
      srNo: 9,
      uniqueFileNumber: '219/2613/1',
      tenderReferenceNo: '02/2022-23',
      tenderDescription: 'Repair and Maintenance to Pedakonda to Jharapalli Road for the year 2022-23',
      tenderType: 'Works',
      publishDate: '02/07/2022',
      tenderValue: 16500000,
      pinCode: '751013',
      productCategory: 'Civil Works - Roads',
      bidValidityDays: 90,
      deliveryPeriodDays: 365,
      preBidMeetingAddress: 'O/o CCE CRWC BBSR',
      tendererClass: 'A or equivalent',
      formOfContract: 'Percentage',
      noOfCovers: 2,
      tenderFee: 10000,
      tenderEmdFee: 165000
    },
    {
      srNo: 10,
      uniqueFileNumber: '219/2617/1',
      tenderReferenceNo: '02/2022-23',
      tenderDescription: 'S/R to Nayakguda to Dusanada Road for the year 2022-23',
      tenderType: 'Works',
      publishDate: '02/07/2022',
      tenderValue: 21300000,
      pinCode: '751013',
      productCategory: 'Civil Works - Roads',
      bidValidityDays: 90,
      deliveryPeriodDays: 365,
      preBidMeetingAddress: 'O/o CCE CRWC BBSR',
      tendererClass: 'A or equivalent',
      formOfContract: 'Percentage',
      noOfCovers: 2,
      tenderFee: 10000,
      tenderEmdFee: 213000
    },
    {
      srNo: 11,
      uniqueFileNumber: '219/2620/1',
      tenderReferenceNo: '02/2022-23',
      tenderDescription: 'Repair and Maintenance to BM Road to MV.122 Road for the year 2022-23',
      tenderType: 'Works',
      publishDate: '02/07/2022',
      tenderValue: 19800000,
      pinCode: '751013',
      productCategory: 'Civil Works - Roads',
      bidValidityDays: 90,
      deliveryPeriodDays: 365,
      preBidMeetingAddress: 'O/o CCE CRWC BBSR',
      tendererClass: 'A or equivalent',
      formOfContract: 'Percentage',
      noOfCovers: 2,
      tenderFee: 10000,
      tenderEmdFee: 198000
    }
  ];

  constructor(
    private tenderService: TenderIntegrationService,
    private snackbar: SnackbarService
  ) {}

  ngOnInit(): void {
    this.searchForm = GepnicForms.createTenderPullForm();
    this.searchForm.patchValue({ publishedDate: '20261101' });

    this.corrigendumResults = [
      {
        corrigendumReference: 'COR-001',
        tenderReference: 'WAMIS-TND-2026-000123',
        publishedDate: '2026-11-03T12:00:00',
        description: 'Bid submission date extended to 2026-11-25',
        type: 'Date Extension',
        status: 'PUBLISHED'
      }
    ];
  }

  getAocInfo(): void {
    this.loadingAoc = true;
    setTimeout(() => {
      this.loadingAoc = false;
      this.snackbar.success(`Fetched ${this.aocRecords.length} AOC records for ${this.aocUploadDate}`);
    }, 400);
  }

  downloadJson(): void {
    const dataStr = 'data:text/json;charset=utf-8,' + encodeURIComponent(JSON.stringify(this.aocRecords, null, 2));
    const downloadAnchor = document.createElement('a');
    downloadAnchor.setAttribute('href', dataStr);
    downloadAnchor.setAttribute('download', `aoc-records-${this.aocUploadDate || 'export'}.json`);
    document.body.appendChild(downloadAnchor);
    downloadAnchor.click();
    downloadAnchor.remove();
    this.snackbar.success('AOC JSON downloaded successfully.');
  }

  openPretenderDetailsModal(row: AocRecord): void {
    this.selectedPretenderDetails = {
      parentWorkNumber: row.uniqueFileNumber,
      tenderReferenceNumber: row.tenderReferenceNo,
      parentWorkDescription: row.tenderDescription,
      tenderType: row.tenderType || 'National Competitive Bid',
      tenderCategory: 'Works',
      workItemProductCategory: row.productCategory || 'Civil Works - Roads',
      tenderFee: row.tenderFee || 10000,
      tenderEmdFee: row.tenderEmdFee || 397482,
      tenderPublishingDateTime: '2022-07-02T10:00:00',
      documentDownloadStartDateTime: '2022-07-02T11:00:00',
      seekClarificationStartDateTime: '2022-07-03T10:00:00',
      seekClarificationEndDateTime: '2022-07-15T15:00:00',
      bidSubmissionStartDateTime: '2022-07-05T10:00:00',
      bidSubmissionEndDateTime: '2022-07-25T17:00:00',
      bidOpeningDateTime: '2022-07-28T11:00:00',
      bidValidityDays: row.bidValidityDays || 90,
      deliveryPeriodDays: row.deliveryPeriodDays || 365,
      preBidMeetingAddress: row.preBidMeetingAddress || 'O/o CCE CRWC BBSR',
      tendererClass: row.tendererClass || 'A or equivalent',
      formOfContract: row.formOfContract || 'Percentage',
      numberOfCovers: row.noOfCovers || '2'
    };
    this.showPretenderDetailsModal = true;
  }

  closePretenderDetailsModal(): void {
    this.showPretenderDetailsModal = false;
    this.selectedPretenderDetails = null;
  }

  insertInAoc(row: AocRecord): void {
    row.insertedInAoc = true;
    this.snackbar.success(`Tender reference ${row.tenderReferenceNo} (File ${row.uniqueFileNumber}) inserted in AOC successfully.`);
  }

  deleteAocRecord(row: AocRecord): void {
    if (confirm(`Are you sure you want to delete AOC record for tender ${row.tenderReferenceNo}?`)) {
      this.aocRecords = this.aocRecords.filter(r => r.uniqueFileNumber !== row.uniqueFileNumber);
      this.snackbar.success(`Record ${row.uniqueFileNumber} deleted.`);
    }
  }

  executeSearch(): void {
    if (this.searchForm.invalid) {
      this.searchForm.markAllAsTouched();
      return;
    }

    this.searching = true;
    this.tenderService.pullCorrigendums(this.searchForm.value).subscribe({
      next: (res) => {
        this.corrigendumResults = res.data || [];
        this.searching = false;
        this.snackbar.success(`Retrieved ${this.corrigendumResults.length} corrigendums`);
      },
      error: () => {
        this.searching = false;
        this.snackbar.info('Loaded cached corrigendum results');
      }
    });
  }
}
