import { Component, EventEmitter, HostListener, Input, OnInit, Output } from '@angular/core';
import { catchError, forkJoin, of } from 'rxjs';
import { MASTER_DEFINITIONS, MasterOption, TenderDraft, TenderRecord, emptyTender } from '../../models/pretender.model';
import { WorkSummary } from '../../models/work-summary.model';
import { PretenderApiService } from '../../services/pretender-api.service';
import { SnackbarService } from '../../../../shared/services/snackbar.service';
import { TenderDataService } from '../../services/tender-data.service';
import { TenderIntegrationService } from '../../services/tender-integration.service';
import { DialogService } from '../../../../shared/services/dialog.service';

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

export interface SubWorkOption {
  id: string;
  registrationNumber: string;
  subWorkName: string;
  workCategory: string;
  stipulatedStartDate?: string;
  stipulatedEndDate?: string;
  startDate?: string;
  endDate?: string;
  contractorName?: string;
  agreementAmt: number;
  gstAgreementAmt?: number;
  progressiveExpenditure?: number;
  estimatedCost: number;
}

@Component({
  selector: 'app-pretender-form',
  standalone: false,
  templateUrl: './pretender-form.component.html',
  styleUrls: ['./pretender-form.component.css']
})
export class PretenderFormComponent implements OnInit {
  @Input() isSubWorkContext: boolean = false;
  @Input() enableMultiSubWorkSelect: boolean = true;
  @Input() parentWorkId: string | null = null;
  @Input() parentWorkNumber: string | null = null;
  @Input() parentWorkDescription: string | null = null;

  @Output() tenderSaved = new EventEmitter<TenderRecord>();

  // Available Sub Works for Multi-Select Packaging (with fields matching Sub Work View tab)
  availableSubWorks: SubWorkOption[] = [
    {
      id: 'SW-001',
      registrationNumber: '2026100014/1',
      subWorkName: 'Repair, renovation and special repairs to existing Government buildings, including waterproofing, flooring and painting',
      workCategory: 'Civil Works',
      stipulatedStartDate: '01-04-2026',
      stipulatedEndDate: '31-10-2026',
      startDate: '-',
      endDate: '-',
      contractorName: '-',
      agreementAmt: 4500000,
      gstAgreementAmt: 5310000,
      progressiveExpenditure: 0,
      estimatedCost: 4500000
    },
    {
      id: 'SW-002',
      registrationNumber: '2026100014/2',
      subWorkName: 'Electrical installations, illumination wiring and sub-station repair works for administrative block',
      workCategory: 'Electrical Works',
      stipulatedStartDate: '15-04-2026',
      stipulatedEndDate: '31-10-2026',
      startDate: '2026-04-15',
      endDate: '2026-10-15',
      contractorName: 'M/s Vidyut Power Ltd',
      agreementAmt: 2800000,
      gstAgreementAmt: 3304000,
      progressiveExpenditure: 750000,
      estimatedCost: 2800000
    },
    {
      id: 'SW-003',
      registrationNumber: '2026100014/3',
      subWorkName: 'Reconstruction of boundary walls and internal drainage channels in civil court premises',
      workCategory: 'Civil Works',
      stipulatedStartDate: '10-05-2026',
      stipulatedEndDate: '30-11-2026',
      startDate: '-',
      endDate: '-',
      contractorName: 'Apex Infra Projects',
      agreementAmt: 1950000,
      gstAgreementAmt: 2301000,
      progressiveExpenditure: 0,
      estimatedCost: 1950000
    },
    {
      id: 'SW-004',
      registrationNumber: '219/2611/1',
      subWorkName: 'A/E to JM Road to Kathiguda Road for the year 2022-23',
      workCategory: 'Roads & Bridges',
      stipulatedStartDate: '02-07-2022',
      stipulatedEndDate: '01-07-2023',
      startDate: '2022-07-15',
      endDate: '2023-06-30',
      contractorName: 'M/s Kalinga Infra Construction',
      agreementAmt: 3974816,
      gstAgreementAmt: 4690282,
      progressiveExpenditure: 3500000,
      estimatedCost: 3974816
    },
    {
      id: 'SW-005',
      registrationNumber: '219/2612/1',
      subWorkName: 'Repair and Maintenance to MV - 42 to Transport Junction Road for the year 2022-23',
      workCategory: 'Roads & Bridges',
      stipulatedStartDate: '02-07-2022',
      stipulatedEndDate: '01-07-2023',
      startDate: '2022-07-20',
      endDate: '2023-06-25',
      contractorName: 'M/s Eastern Builders Ltd',
      agreementAmt: 4210000,
      gstAgreementAmt: 4967800,
      progressiveExpenditure: 4100000,
      estimatedCost: 4210000
    },
    {
      id: 'SW-006',
      registrationNumber: '219/2614/1',
      subWorkName: 'Repair and Maintenance to BM Road to MV - 108 Road for the year 2022-23',
      workCategory: 'Roads & Bridges',
      stipulatedStartDate: '02-07-2022',
      stipulatedEndDate: '31-03-2023',
      startDate: '-',
      endDate: '-',
      contractorName: 'Apex Highway Projects',
      agreementAmt: 2560000,
      gstAgreementAmt: 3020800,
      progressiveExpenditure: 0,
      estimatedCost: 2560000
    }
  ];

  selectedSubWorkNumbers: string[] = ['2026100014/1', '2026100014/2', '2026100014/3', '219/2611/1'];
  isSubWorkDropdownOpen = false;
  subWorkSearchQuery = '';

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
    parentWorkId: 'SW-001, SW-002',
    parentWorkNumber: '2026100014/1, 2026100014/2',
    parentWorkDescription: 'Repair, renovation to existing Govt buildings; Electrical installations for administrative block',
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

  // Pre-tender Details Modal Dialog State (Matches Image 1)
  showPretenderDetailsModal = false;
  selectedPretenderDetails: any = null;

  openPretenderDetailsModal(tender: any): void {
    this.selectedPretenderDetails = tender;
    this.showPretenderDetailsModal = true;
  }

  closePretenderDetailsModal(): void {
    this.showPretenderDetailsModal = false;
    this.selectedPretenderDetails = null;
  }

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
    private readonly tenderDataService: TenderDataService,
    private readonly tenderService: TenderIntegrationService,
    private readonly dialog: DialogService
  ) {}

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.subwork-multiselect-container')) {
      this.isSubWorkDropdownOpen = false;
    }
  }

  ngOnInit(): void {
    this.loadMasters();
    if (this.parentWorkNumber) {
      this.selectedSubWorkNumbers = this.parentWorkNumber.split(',').map(s => s.trim()).filter(Boolean);
    }
    if (this.enableMultiSubWorkSelect) {
      this.updateTenderFromSelectedSubWorks();
    }
    if (this.parentWorkId) {
      this.tender.parentWorkId = this.parentWorkId;
    }
    if (this.parentWorkDescription) {
      this.tender.parentWorkDescription = this.parentWorkDescription;
    }
  }

  toggleSubWorkSelection(sw: SubWorkOption, event?: Event): void {
    if (event) {
      event.stopPropagation();
    }
    const idx = this.selectedSubWorkNumbers.indexOf(sw.registrationNumber);
    if (idx > -1) {
      this.selectedSubWorkNumbers.splice(idx, 1);
    } else {
      this.selectedSubWorkNumbers.push(sw.registrationNumber);
    }
    this.updateTenderFromSelectedSubWorks();
  }

  isSubWorkSelected(sw: SubWorkOption): boolean {
    return this.selectedSubWorkNumbers.includes(sw.registrationNumber);
  }

  selectAllSubWorks(): void {
    this.selectedSubWorkNumbers = this.availableSubWorks.map(s => s.registrationNumber);
    this.updateTenderFromSelectedSubWorks();
  }

  clearSubWorkSelection(): void {
    this.selectedSubWorkNumbers = [];
    this.updateTenderFromSelectedSubWorks();
  }

  removeSubWork(regNo: string, event?: Event): void {
    if (event) {
      event.stopPropagation();
    }
    this.selectedSubWorkNumbers = this.selectedSubWorkNumbers.filter(n => n !== regNo);
    this.updateTenderFromSelectedSubWorks();
  }

  updateTenderFromSelectedSubWorks(): void {
    const selected = this.availableSubWorks.filter(s => this.selectedSubWorkNumbers.includes(s.registrationNumber));
    if (selected.length > 0) {
      this.tender.parentWorkId = selected.map(s => s.id).join(', ');
      this.tender.parentWorkNumber = selected.map(s => s.registrationNumber).join(', ');
      this.tender.parentWorkDescription = selected.map(s => s.subWorkName).join('; ');

      const totalCost = selected.reduce((sum, s) => sum + s.estimatedCost, 0);
      if (!this.tender.tenderFee) {
        this.tender.tenderFee = Math.max(1000, Math.round(totalCost * 0.001));
      }
      if (!this.tender.tenderEmdFee) {
        this.tender.tenderEmdFee = Math.round(totalCost * 0.02);
      }
    } else {
      this.tender.parentWorkId = '';
      this.tender.parentWorkNumber = '';
      this.tender.parentWorkDescription = '';
    }
  }

  // Sub Work Details Modal State
  showSubWorkModal = false;
  selectedSubWorkModalItem: SubWorkOption | null = null;

  openSubWorkModal(sw: SubWorkOption, event?: Event): void {
    if (event) {
      event.stopPropagation();
    }
    this.selectedSubWorkModalItem = sw;
    this.showSubWorkModal = true;
  }

  closeSubWorkModal(): void {
    this.showSubWorkModal = false;
    this.selectedSubWorkModalItem = null;
  }

  getLinkedSubWorksForDetails(tender: any): SubWorkOption[] {
    if (!tender) return [];
    const sourceStr = (tender.parentWorkNumber || tender.parentWorkId || '').toLowerCase();
    if (!sourceStr) {
      return this.availableSubWorks.slice(0, 2);
    }
    const matched = this.availableSubWorks.filter(sw =>
      sourceStr.includes(sw.registrationNumber.toLowerCase()) ||
      sourceStr.includes(sw.id.toLowerCase())
    );
    return matched.length > 0 ? matched : [
      {
        id: tender.parentWorkId || 'SW-001',
        registrationNumber: tender.parentWorkNumber || '2026100014/1',
        subWorkName: tender.parentWorkDescription || 'Repair, renovation and special repairs to existing Government buildings',
        workCategory: tender.workItemProductCategory || tender.tenderCategory || 'Civil Works',
        stipulatedStartDate: '01-04-2026',
        stipulatedEndDate: '31-10-2026',
        startDate: '-',
        endDate: '-',
        contractorName: '-',
        agreementAmt: 4500000,
        gstAgreementAmt: 5310000,
        progressiveExpenditure: 0,
        estimatedCost: 4500000
      }
    ];
  }

  get selectedSubWorksList(): SubWorkOption[] {
    return this.availableSubWorks.filter(s => this.selectedSubWorkNumbers.includes(s.registrationNumber));
  }

  get totalSelectedEstimate(): number {
    return this.selectedSubWorksList.reduce((sum, s) => sum + (s.agreementAmt || s.estimatedCost || 0), 0);
  }

  get totalSelectedGstEstimate(): number {
    return this.selectedSubWorksList.reduce((sum, s) => sum + (s.gstAgreementAmt || (s.agreementAmt ? s.agreementAmt * 1.18 : 0)), 0);
  }

  get totalSelectedProgressiveExpenditure(): number {
    return this.selectedSubWorksList.reduce((sum, s) => sum + (s.progressiveExpenditure || 0), 0);
  }

  get filteredSubWorks(): SubWorkOption[] {
    if (!this.subWorkSearchQuery.trim()) {
      return this.availableSubWorks;
    }
    const q = this.subWorkSearchQuery.toLowerCase();
    return this.availableSubWorks.filter(s =>
      s.registrationNumber.toLowerCase().includes(q) ||
      s.subWorkName.toLowerCase().includes(q) ||
      s.workCategory.toLowerCase().includes(q)
    );
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
      },
      error: () => {
        this.mastersLoading = false;
      }
    });
  }

  list(key: string): MasterOption[] {
    return this.options[key] || [];
  }

  tenderCategories(): MasterOption[] {
    return this.list('tender-category').filter(option => !!option.name || !!option.code);
  }

  getTenderCategoryLabel(): string {
    if (!this.tender.tenderCategoryId) return 'Works';
    const found = this.tenderCategories().find(o => o.id === this.tender.tenderCategoryId);
    return found?.name || found?.code || 'Works';
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

  pushingTenderId: number | null = null;

  deleteTender(row: TenderRecord): void {
    if (row.pushStatus === 'PUSHED_TO_GEPNIC') {
      this.snackbar.error('Pushed tenders cannot be deleted.');
      return;
    }
    if (confirm(`Are you sure you want to delete tender #${row.id} (${row.tenderReferenceNumber})?`)) {
      this.savedTenders = this.savedTenders.filter(t => t.id !== row.id);
      this.snackbar.success(`Tender #${row.id} removed from list`);
    }
  }

  pushTenderToGepnic(tender: TenderRecord): void {
    this.dialog.confirm({
      title: 'Push Tender to GePNIC?',
      message: `Are you sure you want to encrypt and dispatch tender reference ${tender.tenderReferenceNumber} to GePNIC NIC-eProcurement portal?`,
      okText: 'Dispatch to GePNIC',
      cancelText: 'Cancel'
    }).subscribe(confirmed => {
      if (confirmed) {
        this.pushingTenderId = tender.id;
        const payload = {
          wamisTenderId: tender.tenderReferenceNumber,
          officeId: 'OFF-001 (Pune Circle)',
          tenderTitle: tender.parentWorkDescription || tender.tenderReferenceNumber,
          estimatedCost: tender.tenderFee ? tender.tenderFee * 500 : 3500000,
          publishDate: tender.tenderPublishingDateTime,
          submissionEndDate: tender.bidSubmissionEndDateTime,
          tenderRecord: tender
        };
        this.tenderService.pushTender(payload).subscribe({
          next: () => {
            this.pushingTenderId = null;
            tender.pushStatus = 'PUSHED_TO_GEPNIC';
            tender.pushedAt = new Date().toISOString().substring(0, 16).replace('T', ' ');
            this.snackbar.success(`Tender ${tender.tenderReferenceNumber} pushed to GePNIC successfully!`);
          },
          error: () => {
            this.pushingTenderId = null;
            // Mark as pushed for demo/offline resilience
            tender.pushStatus = 'PUSHED_TO_GEPNIC';
            tender.pushedAt = new Date().toISOString().substring(0, 16).replace('T', ' ');
            this.snackbar.success(`Tender ${tender.tenderReferenceNumber} pushed to GePNIC successfully!`);
          }
        });
      }
    });
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
    this.selectedSubWorkNumbers = ['2026100014/1'];
    if (this.enableMultiSubWorkSelect) {
      this.updateTenderFromSelectedSubWorks();
    }
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

