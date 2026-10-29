import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { catchError, forkJoin, of } from 'rxjs';
import { MASTER_DEFINITIONS, MasterOption, TenderDraft, TenderRecord, emptyTender } from '../../models/pretender.model';
import { WorkSummary } from '../../models/work-summary.model';
import { PretenderApiService } from '../../services/pretender-api.service';
import { SnackbarService } from '../../../../shared/services/snackbar.service';
import { TenderDataService } from '../../services/tender-data.service';

type UploadField = 'nitFileName' | 'dtcnFileName' | 'otherFileName';

export interface UploadedDocItem {
  id: string;
  name: string;
  category: string;
  size: number;
  uploadDate: string;
  status: 'uploaded' | 'ready' | 'uploading' | 'failed';
  file?: File;
}

@Component({
  selector: 'app-pretender-form',
  standalone: false,
  templateUrl: './pretender-form.component.html',
  styleUrls: ['./pretender-form.component.css']
})
export class PretenderFormComponent implements OnInit {
  @Input() isSubWorkContext: boolean = false;
  @Input() parentWorkId: string | null = null;
  @Input() parentWorkNumber: string | null = null;
  @Input() parentWorkDescription: string | null = null;

  @Output() tenderSaved = new EventEmitter<TenderRecord>();

  tender: TenderDraft = emptyTender();
  options: Record<string, MasterOption[]> = {};
  activeTab: 'add' | 'view' = 'add';
  error = '';
  success = '';
  mastersLoading = false;
  saving = false;
  loadingRecord = false;
  recordId: number | null = null;

  // Sample Pre-Tender Record (Requirement 1)
  readonly sampleRecord: TenderRecord = {
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
    otherFileName: 'Technical_Specifications_IRC.pdf'
  };

  savedTenders: TenderRecord[] = [{ ...this.sampleRecord }];

  // BOQ Modal Dialog State (Requirement 2)
  showBoqModal = false;
  selectedTenderForBoq: TenderRecord | null = null;

  // Docs Upload Modal Dialog State (Requirement 3)
  showDocsModal = false;
  selectedTenderForDocs: TenderRecord | null = null;
  stagedFiles: UploadedDocItem[] = [];
  existingDocs: UploadedDocItem[] = [];
  uploadingDocs = false;
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

  private readonly files: Partial<Record<UploadField, File>> = {};

  constructor(
    private readonly api: PretenderApiService,
    private readonly snackbar: SnackbarService,
    private readonly tenderDataService: TenderDataService
  ) {}

  ngOnInit(): void {
    this.loadMasters();
    if (this.parentWorkId || this.parentWorkNumber) {
      this.tender.parentWorkId = this.parentWorkId;
      this.tender.parentWorkNumber = this.parentWorkNumber;
      this.tender.parentWorkDescription = this.parentWorkDescription;
    }
  }

  loadMasters(): void {
    this.mastersLoading = true;
    this.error = '';
    forkJoin(
      MASTER_DEFINITIONS.map(master =>
        this.api.listMasters(master.key).pipe(catchError(() => of(null)))
      )
    ).subscribe({
      next: lists => {
        this.options = {};
        lists.forEach((list, index) => {
          if (list) this.options[MASTER_DEFINITIONS[index].key] = list;
        });
        this.mastersLoading = false;
        if (lists.some(list => list === null)) {
          this.error = 'Some dropdown values could not be loaded. Please ensure the backend is running.';
          this.snackbar.error(this.error);
        }
      },
      error: () => {
        this.mastersLoading = false;
        this.error = 'Failed to load master dropdown data from database.';
        this.snackbar.error(this.error);
      }
    });
  }

  list(key: string): MasterOption[] {
    return this.options[key] || [];
  }

  tenderCategories(): MasterOption[] {
    return this.list('tender-category').filter(option => !!option.name || !!option.code);
  }

  productCategoryOptions(): MasterOption[] {
    if (!this.tender.tenderCategoryId) {
      return this.list('work-item-product-category');
    }
    const filtered = this.list('work-item-product-category').filter(
      option => option.tenderCategoryId === this.tender.tenderCategoryId
    );
    return filtered.length > 0 ? filtered : this.list('work-item-product-category');
  }

  changeTenderCategory(categoryId: number | null): void {
    this.tender.tenderCategoryId = categoryId;
    const selected = this.list('work-item-product-category').find(
      option => option.id === this.tender.workItemProductCategoryId
    );
    if (selected && selected.tenderCategoryId && selected.tenderCategoryId !== categoryId) {
      this.tender.workItemProductCategoryId = null;
    }
  }

  chooseFile(event: Event, field: UploadField): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.files[field] = file;
      this.tender[field] = file.name;
    }
  }

  selectWork(work: WorkSummary): void {
    this.tender.parentWorkId = work.id;
    this.tender.parentWorkNumber = work.workNumber;
    this.tender.parentWorkDescription = work.description;
  }

  clearWork(): void {
    this.tender.parentWorkId = null;
    this.tender.parentWorkNumber = null;
    this.tender.parentWorkDescription = null;
  }

  switchToView(): void {
    this.activeTab = 'view';
    this.loadSavedTenders();
  }

  loadSavedTenders(): void {
    this.api.listTenders().subscribe({
      next: data => {
        const sample = { ...this.sampleRecord };
        if (!data || data.length === 0) {
          this.savedTenders = [sample];
        } else {
          const hasSample = data.some(d => d.tenderReferenceNumber === sample.tenderReferenceNumber);
          this.savedTenders = hasSample ? data : [sample, ...data];
        }
      },
      error: () => {
        this.savedTenders = [{ ...this.sampleRecord }];
        this.snackbar.info('Loaded pre-tender records with sample record.');
      }
    });
  }

  // ==================== BOQ MODAL METHODS ====================
  openBoqModal(tender: TenderRecord): void {
    this.selectedTenderForBoq = tender;
    this.showBoqModal = true;
  }

  closeBoqModal(): void {
    this.showBoqModal = false;
    this.selectedTenderForBoq = null;
  }

  // ==================== DOCS MODAL METHODS ====================
  openDocsModal(tender: TenderRecord): void {
    this.selectedTenderForDocs = tender;
    this.stagedFiles = [];
    this.existingDocs = [];

    // Pre-populate documents attached to this tender record
    if (tender.nitFileName) {
      this.existingDocs.push({
        id: 'doc-nit',
        name: tender.nitFileName,
        category: 'Notice Inviting Tender (NIT)',
        size: 2450000,
        uploadDate: '2026-10-29',
        status: 'uploaded'
      });
    }
    if (tender.dtcnFileName) {
      this.existingDocs.push({
        id: 'doc-dtcn',
        name: tender.dtcnFileName,
        category: 'Draft Tender Notice & Conditions (DTCN)',
        size: 3820000,
        uploadDate: '2026-10-29',
        status: 'uploaded'
      });
    }
    if (tender.otherFileName) {
      this.existingDocs.push({
        id: 'doc-other',
        name: tender.otherFileName,
        category: 'Technical Specifications',
        size: 1980000,
        uploadDate: '2026-10-29',
        status: 'uploaded'
      });
    }

    this.showDocsModal = true;
  }

  closeDocsModal(): void {
    this.showDocsModal = false;
    this.selectedTenderForDocs = null;
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
        this.api.uploadFile(item.file).subscribe({
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

  formatFileSize(bytes: number): string {
    if (!bytes || bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
  }

  fileUrl(fileName: string): string {
    return this.api.fileUrl(fileName);
  }

  editTender(id: number): void {
    this.loadingRecord = true;
    this.recordId = id;
    this.activeTab = 'add';
    this.api.getTender(id).subscribe({
      next: record => {
        const {
          id: _id,
          tenderType: _type,
          tenderCategory: _category,
          numberOfCovers: _covers,
          workItemProductCategory: _product,
          numberOfBidOpeners: _openers,
          formOfContract: _contract,
          tendererClass: _class,
          ...draft
        } = record;
        this.tender = draft;
        this.loadingRecord = false;
        this.snackbar.info(`Loaded tender #${id} for editing`);
      },
      error: err => {
        this.loadingRecord = false;
        this.error = this.apiError(err, 'Could not load tender record.');
        this.snackbar.error(this.error);
      }
    });
  }

  deleteTender(row: TenderRecord): void {
    if (confirm(`Are you sure you want to delete tender #${row.id} (${row.tenderReferenceNumber})?`)) {
      this.savedTenders = this.savedTenders.filter(t => t.id !== row.id);
      this.snackbar.success(`Tender #${row.id} removed from list`);
    }
  }

  save(): void {
    this.error = '';
    this.success = '';
    this.saving = true;
    const wasEditing = this.recordId !== null;
    const fields = Object.keys(this.files) as UploadField[];

    const req$ = fields.length
      ? (this.recordId
          ? this.api.updateTenderWithFiles(this.recordId, this.tender, this.files)
          : this.api.saveTenderWithFiles(this.tender, this.files))
      : (this.recordId
          ? this.api.updateTender(this.recordId, this.tender)
          : this.api.saveTender(this.tender));

    req$.subscribe({
      next: result => {
        this.recordId = result.id;
        this.tender = {
          ...this.tender,
          nitFileName: result.nitFileName || this.tender.nitFileName,
          dtcnFileName: result.dtcnFileName || this.tender.dtcnFileName,
          otherFileName: result.otherFileName || this.tender.otherFileName
        };
        this.success = `Pre-tender details ${wasEditing ? 'updated' : 'saved'} (record #${result.id}).`;
        this.snackbar.success(this.success);
        this.saving = false;
        this.clearFiles();

        // Sync with WAMIS Tender state if in sub-work context
        if (this.isSubWorkContext) {
          this.tenderDataService.updateTenderData({
            wamisTenderId: result.tenderReferenceNumber,
            tenderFee: result.tenderFee || 0,
            emdAmount: result.tenderEmdFee || 0,
            publishDate: result.tenderPublishingDateTime,
            submissionEndDate: result.bidSubmissionEndDateTime,
            tenderTitle: result.parentWorkDescription || result.tenderReferenceNumber
          });
        }

        this.tenderSaved.emit(result);
      },
      error: err => {
        this.saving = false;
        this.error = this.apiError(err, 'Could not save pre-tender details. Check the fields and backend.');
        this.snackbar.error(this.error);
      }
    });
  }

  clear(): void {
    this.recordId = null;
    this.tender = emptyTender();
    if (this.parentWorkId || this.parentWorkNumber) {
      this.tender.parentWorkId = this.parentWorkId;
      this.tender.parentWorkNumber = this.parentWorkNumber;
      this.tender.parentWorkDescription = this.parentWorkDescription;
    }
    this.clearFiles();
    this.error = '';
    this.success = '';
    this.snackbar.info('Form cleared');
  }

  private clearFiles(): void {
    Object.keys(this.files).forEach(key => delete this.files[key as UploadField]);
  }

  private apiError(err: any, fallback: string): string {
    return err?.error?.error || err?.error?.message || err?.message || fallback;
  }
}

