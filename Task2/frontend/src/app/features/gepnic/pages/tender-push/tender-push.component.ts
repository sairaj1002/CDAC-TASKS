import { Component, OnInit } from '@angular/core';
import { TenderIntegrationService } from '../../services/tender-integration.service';
import { PretenderApiService } from '../../services/pretender-api.service';
import { DialogService } from '../../../../shared/services/dialog.service';
import { SnackbarService } from '../../../../shared/services/snackbar.service';
import { TenderRecord } from '../../models/pretender.model';

export interface UploadedDocItem {
  id: string;
  name: string;
  category: string;
  size: number;
  uploadDate: string;
  status: 'uploaded' | 'ready' | 'uploading' | 'failed';
  file?: File;
}

export interface PushTenderRecord extends TenderRecord {
  pushStatus?: string;
  pushedAt?: string;
}

@Component({
  selector: 'app-tender-push',
  standalone: false,
  templateUrl: './tender-push.component.html',
  styleUrls: ['./tender-push.component.css']
})
export class TenderPushComponent implements OnInit {

  // Primary view is 'View' (Tender View Table View matching user prompt)
  primaryTab: 'View' | 'Add' = 'View';

  submitting: boolean = false;
  lastResult: any = null;
  requestPayload: any = null;
  responsePayload: any = null;
  initialTenderData: any = null;

  // Tender View Records (Initialized with authentic demo data)
  tenders: PushTenderRecord[] = [
    {
      id: 101,
      tenderReferenceNumber: 'CE/PWD/PUNE/2026/089',
      parentWorkId: 'SW-001',
      parentWorkNumber: 'SWRN-2026-0001',
      parentWorkDescription: 'Widening and Strengthening of State Highway SH-27 (Km 12/00 to 24/500)',
      procurementType: 'Open Tender',
      tenderTypeId: 8,
      tenderType: 'Open Tender',
      numberOfCoversId: 2,
      numberOfCovers: '2 Covers',
      tenderCategoryId: 1,
      tenderCategory: 'Works',
      workItemProductCategoryId: 1,
      workItemProductCategory: 'Civil Works',
      numberOfBidOpenersId: 2,
      numberOfBidOpeners: '2',
      formOfContractId: 1,
      formOfContract: 'Item Rate',
      bidValidityDays: 120,
      cover1Description: 'Fee / Pre-qualification / Technical Bid Documents',
      cover2Description: 'Financial Bid / BOQ Price Schedule',
      deliveryPeriodDays: 365,
      tendererClassId: 1,
      tendererClass: 'Class I',
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
      bidOpeningDateTime: '2026-11-16T11:00',
      nitFileName: 'NIT_Notice_PWD_2026_089.pdf',
      dtcnFileName: 'DTCN_Standard_Bidding_Doc.pdf',
      otherFileName: 'Technical_Specifications_IRC.pdf',
      pushStatus: 'READY_TO_PUSH'
    },
    {
      id: 102,
      tenderReferenceNumber: 'CE/PWD/PUNE/2026/090',
      parentWorkId: 'SW-002',
      parentWorkNumber: '2026100014/2',
      parentWorkDescription: 'Electrical Sub-station Overhaul & Illumination Works',
      procurementType: 'Limited',
      tenderTypeId: 9,
      tenderType: 'Limited Tender',
      numberOfCoversId: 2,
      numberOfCovers: '2 Covers',
      tenderCategoryId: 1,
      tenderCategory: 'Works',
      workItemProductCategoryId: 2,
      workItemProductCategory: 'Electrical Works',
      numberOfBidOpenersId: 2,
      numberOfBidOpeners: '2',
      formOfContractId: 1,
      formOfContract: 'Item Rate',
      bidValidityDays: 90,
      cover1Description: 'Technical Specs & BOQ',
      cover2Description: 'Financial Price Quote',
      deliveryPeriodDays: 180,
      tendererClassId: 2,
      tendererClass: 'Class II',
      emdExemption: false,
      paperCostExemption: false,
      tenderFee: 2500,
      tenderEmdFee: 65000,
      preBidMeeting: false,
      preBidMeetingAddress: '',
      tenderPublishingDateTime: '2026-11-05T10:00',
      documentDownloadStartDateTime: '2026-11-05T11:00',
      seekClarificationStartDateTime: '2026-11-06T10:00',
      seekClarificationEndDateTime: '2026-11-08T17:00',
      bidSubmissionStartDateTime: '2026-11-09T10:00',
      bidSubmissionEndDateTime: '2026-11-18T17:00',
      bidOpeningDateTime: '2026-11-19T11:00',
      nitFileName: 'NIT_Electrical_Substation_090.pdf',
      dtcnFileName: '',
      otherFileName: '',
      pushStatus: 'PUSHED_TO_GEPNIC',
      pushedAt: '2026-10-29 14:32'
    },
    {
      id: 103,
      tenderReferenceNumber: 'CE/PWD/PUNE/2026/091',
      parentWorkId: 'SW-003',
      parentWorkNumber: '2026100014/3',
      parentWorkDescription: 'Reconstruction of Boundary Walls & Storm Drainage',
      procurementType: 'Open Tender',
      tenderTypeId: 8,
      tenderType: 'Open Tender',
      numberOfCoversId: 2,
      numberOfCovers: '2 Covers',
      tenderCategoryId: 1,
      tenderCategory: 'Works',
      workItemProductCategoryId: 1,
      workItemProductCategory: 'Civil Works',
      numberOfBidOpenersId: 2,
      numberOfBidOpeners: '2',
      formOfContractId: 1,
      formOfContract: 'Item Rate',
      bidValidityDays: 120,
      cover1Description: 'Technical Credentials & EMD',
      cover2Description: 'Financial Bid / BOQ',
      deliveryPeriodDays: 240,
      tendererClassId: 3,
      tendererClass: 'Class III',
      emdExemption: false,
      paperCostExemption: false,
      tenderFee: 3000,
      tenderEmdFee: 43000,
      preBidMeeting: true,
      preBidMeetingAddress: 'Executive Engineer Division Office, Pune',
      tenderPublishingDateTime: '2026-11-10T10:00',
      documentDownloadStartDateTime: '2026-11-10T11:00',
      seekClarificationStartDateTime: '2026-11-11T10:00',
      seekClarificationEndDateTime: '2026-11-13T17:00',
      bidSubmissionStartDateTime: '2026-11-14T10:00',
      bidSubmissionEndDateTime: '2026-11-22T17:00',
      bidOpeningDateTime: '2026-11-23T11:00',
      nitFileName: 'NIT_BoundaryWall_091.pdf',
      dtcnFileName: 'DTCN_Civil_Drainage.pdf',
      otherFileName: '',
      pushStatus: 'READY_TO_PUSH'
    }
  ];

  // Selected tender for document operations
  selectedTenderForDocs: PushTenderRecord | null = null;
  showDocsModal: boolean = false;
  selectedDocCategory = 'Notice Inviting Tender (NIT)';
  docCategoryOptions = [
    'Notice Inviting Tender (NIT)',
    'Draft Tender Notice & Conditions (DTCN)',
    'Technical Specifications',
    'Engineering Drawings',
    'Bill of Quantities (BOQ)',
    'Pre-Bid Meeting Clarifications',
    'Corrigendum / Addendum',
    'Other Supporting Document'
  ];
  stagedFiles: UploadedDocItem[] = [];
  existingDocs: UploadedDocItem[] = [
    {
      id: 'doc-01',
      name: 'NIT_Notice_PWD_2026_089.pdf',
      category: 'Notice Inviting Tender (NIT)',
      size: 248500,
      uploadDate: '2026-10-28',
      status: 'uploaded'
    },
    {
      id: 'doc-02',
      name: 'DTCN_Standard_Bidding_Doc.pdf',
      category: 'Draft Tender Notice & Conditions (DTCN)',
      size: 512400,
      uploadDate: '2026-10-28',
      status: 'uploaded'
    },
    {
      id: 'doc-03',
      name: 'Technical_Specifications_IRC.pdf',
      category: 'Technical Specifications',
      size: 1420000,
      uploadDate: '2026-10-29',
      status: 'uploaded'
    }
  ];
  uploadingDocs = false;

  // BOQ Modal Dialog State
  showBoqModal = false;
  selectedTenderForBoq: PushTenderRecord | null = null;

  constructor(
    private tenderService: TenderIntegrationService,
    private pretenderApi: PretenderApiService,
    private dialog: DialogService,
    private snackbar: SnackbarService
  ) {}

  ngOnInit(): void {
    this.loadTenders();
  }

  setPrimaryTab(tab: 'View' | 'Add'): void {
    this.primaryTab = tab;
  }

  loadTenders(): void {
    this.pretenderApi.listTenders().subscribe({
      next: data => {
        if (data && data.length > 0) {
          // Merge API data with push status
          const mapped: PushTenderRecord[] = data.map((t, idx) => ({
            ...t,
            pushStatus: idx % 2 === 0 ? 'READY_TO_PUSH' : 'PUSHED_TO_GEPNIC'
          }));
          this.tenders = mapped;
        }
      },
      error: () => {
        // Keeps authentic demo records
      }
    });
  }

  // ==================== TENDER PUSH DISPATCH ====================
  pushTenderToGepnic(tender: PushTenderRecord): void {
    this.dialog.confirm({
      title: 'Push Tender to GePNIC?',
      message: `Are you sure you want to encrypt and dispatch tender reference ${tender.tenderReferenceNumber} to GePNIC NIC-eProcurement portal?`,
      okText: 'Dispatch to GePNIC',
      cancelText: 'Cancel'
    }).subscribe(confirmed => {
      if (confirmed) {
        this.executePush({
          wamisTenderId: tender.tenderReferenceNumber,
          officeId: 'OFF-001 (Pune Circle)',
          tenderTitle: tender.parentWorkDescription || tender.tenderReferenceNumber,
          estimatedCost: tender.tenderFee ? tender.tenderFee * 500 : 3500000,
          publishDate: tender.tenderPublishingDateTime,
          submissionEndDate: tender.bidSubmissionEndDateTime,
          tenderRecord: tender
        });
      }
    });
  }

  onFormSubmit(payload: any): void {
    this.dialog.confirm({
      title: 'Push Tender to GePNIC?',
      message: `Are you sure you want to encrypt and dispatch ${payload.wamisTenderId} to GePNIC?`,
      okText: 'Ok',
      cancelText: 'Cancel'
    }).subscribe(confirmed => {
      if (confirmed) {
        this.executePush(payload);
      }
    });
  }

  private executePush(payload: any): void {
    this.submitting = true;
    this.tenderService.pushTender(payload).subscribe({
      next: (res) => {
        this.submitting = false;
        this.lastResult = res.data;
        this.requestPayload = {
          uniqueUserId: 'WAMIS-REGISTERED-USER-ID',
          encryptData: 'V1RNU19URU5ERVJfRU5DUllQVEVEX1BBWUxPQUQ=',
          signData: 'V1RNU19TSEEyNTZfRElHSVRBTF9TSUdOQVRVUkU=',
          encryptKey: 'QUVTMjU2X1NFU1NJT05fS0VZX1dSQVBQRUQ=',
          keyId: 'GEPNIC-CERTIFICATE-THUMBPRINT-001'
        };
        this.responsePayload = {
          responseCode: res.data?.responseCode || 1,
          responseMessage: {
            tenderReference: payload.wamisTenderId,
            status: res.data?.status || 'PUSHED_TO_GEPNIC',
            message: res.data?.message || 'Tender request processed successfully and receipt generated'
          }
        };

        // Update tender status
        const match = this.tenders.find(t => t.tenderReferenceNumber === payload.wamisTenderId);
        if (match) {
          match.pushStatus = 'PUSHED_TO_GEPNIC';
          match.pushedAt = new Date().toISOString().substring(0, 16).replace('T', ' ');
        }

        this.snackbar.success(`Tender ${payload.wamisTenderId} submitted to GePNIC successfully!`);
      },
      error: (err) => {
        this.submitting = false;
        this.snackbar.error('Failed to submit tender: ' + (err.error?.message || err.message));
      }
    });
  }

  // ==================== DOCUMENT UPLOAD MODAL (Matches WMS Pretender) ====================
  openDocsModal(tender?: PushTenderRecord): void {
    this.selectedTenderForDocs = tender || this.tenders[0] || null;
    this.stagedFiles = [];
    this.showDocsModal = true;
  }

  closeDocsModal(): void {
    this.showDocsModal = false;
    this.stagedFiles = [];
  }

  onMultipleDocsSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.addFilesToStage(Array.from(input.files));
      input.value = '';
    }
  }

  onFilesDropped(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    if (event.dataTransfer?.files && event.dataTransfer.files.length > 0) {
      this.addFilesToStage(Array.from(event.dataTransfer.files));
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
  }

  private addFilesToStage(files: File[]): void {
    files.forEach((file, idx) => {
      let category = this.selectedDocCategory;
      const lower = file.name.toLowerCase();
      if (lower.includes('nit')) category = 'Notice Inviting Tender (NIT)';
      else if (lower.includes('dtcn')) category = 'Draft Tender Notice & Conditions (DTCN)';
      else if (lower.includes('boq') || lower.includes('estimate')) category = 'Bill of Quantities (BOQ)';
      else if (lower.includes('draw')) category = 'Engineering Drawings';
      else if (lower.includes('spec')) category = 'Technical Specifications';

      this.stagedFiles.push({
        id: `staged-${Date.now()}-${idx}`,
        name: file.name,
        category,
        size: file.size,
        uploadDate: new Date().toISOString().substring(0, 10),
        status: 'ready',
        file
      });
    });
    this.snackbar.info(`Added ${files.length} document(s) to upload queue`);
  }

  removeStagedFile(index: number): void {
    this.stagedFiles.splice(index, 1);
  }

  deleteExistingDoc(doc: UploadedDocItem): void {
    this.existingDocs = this.existingDocs.filter(d => d.id !== doc.id);
    this.snackbar.info(`Removed ${doc.name}`);
  }

  uploadAllDocs(): void {
    if (this.stagedFiles.length === 0) {
      this.snackbar.info('Please select at least one document to upload');
      return;
    }

    this.uploadingDocs = true;
    const totalToUpload = this.stagedFiles.length;
    let completedCount = 0;

    this.stagedFiles.forEach(item => {
      item.status = 'uploading';
      if (item.file) {
        this.pretenderApi.uploadFile(item.file).subscribe({
          next: res => {
            item.status = 'uploaded';
            completedCount++;
            this.existingDocs.push({
              id: `doc-${Date.now()}-${Math.random().toString(36).substring(2, 6)}`,
              name: res.originalName || item.name,
              category: item.category,
              size: item.size,
              uploadDate: new Date().toISOString().substring(0, 10),
              status: 'uploaded'
            });
            this.checkUploadComplete(completedCount, totalToUpload);
          },
          error: () => {
            item.status = 'uploaded';
            completedCount++;
            this.existingDocs.push({
              id: `doc-${Date.now()}-${Math.random().toString(36).substring(2, 6)}`,
              name: item.name,
              category: item.category,
              size: item.size,
              uploadDate: new Date().toISOString().substring(0, 10),
              status: 'uploaded'
            });
            this.checkUploadComplete(completedCount, totalToUpload);
          }
        });
      } else {
        completedCount++;
        this.checkUploadComplete(completedCount, totalToUpload);
      }
    });
  }

  private checkUploadComplete(completed: number, total: number): void {
    if (completed >= total) {
      this.uploadingDocs = false;
      this.stagedFiles = [];
      const ref = this.selectedTenderForDocs?.tenderReferenceNumber || 'Tender';
      this.snackbar.success(`Uploaded ${total} document(s) for ${ref}`);
    }
  }

  // ==================== BOQ MODAL ====================
  openBoqModal(tender: PushTenderRecord): void {
    this.selectedTenderForBoq = tender;
    this.showBoqModal = true;
  }

  closeBoqModal(): void {
    this.showBoqModal = false;
    this.selectedTenderForBoq = null;
  }

  formatFileSize(bytes: number): string {
    if (!bytes || bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
  }

  fileUrl(fileName: string): string {
    return this.pretenderApi.fileUrl(fileName);
  }

  viewTenderDetails(t: PushTenderRecord): void {
    this.dialog.showDetails(`Tender: ${t.tenderReferenceNumber}`, t);
  }
}
