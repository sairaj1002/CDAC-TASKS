import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { APP_CONSTANTS } from '../../../common/constants/app.constants';
import { DropdownMasterResponseForm, PreTenderDocumentForm, PreTenderForm, PreTenderResponseForm } from '../../../common/models/app.models';
import { DropdownService } from '../../../common/services/dropdown.service';
import { PreTenderService } from '../../../common/services/pretender.service';
import { NotificationService } from '../../../common/services/notification.service';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog';

@Component({
  selector: 'app-pretender-form',
  standalone: false,
  templateUrl: './pretender-form.html',
})
export class PreTenderFormChildComponent implements OnInit {
  // Primary Tabs: Add | View
  activeTab: 'add' | 'view' = 'add';

  // 1. Work Search Component (Matches Image 2)
  isSearchCollapsed = false;
  searchIdOperator = '=';
  searchWorkId = '';
  searchCategory = '';
  searchTenderType = '';
  searchValueOperator = '=';
  searchTenderValue: number | null = null;
  searchFormContract = '';
  searchLocation = '';
  searchStatus = '';
  searchKeyword = '';

  availableWorks: PreTenderResponseForm[] = [];
  selectedWork: PreTenderResponseForm | null = null;
  matchedWorks: PreTenderResponseForm[] = [];

  // Work Flag Checkboxes
  isEmergencyWork = false;
  isDepartmentalWork = false;
  isLumpsumWork = false;

  // BOQ Evaluation Flag Checkboxes
  allowItemwiseEvaluation = false;
  allowMultiCurrencyForBoq = false;
  allowMultiCurrencyForFee = false;
  automaticEvaluation = false;

  // 3. Pre-Tender Critical Dates (bound to HTML datetime-local inputs)
  publishingDateLocal = '2026-11-05T10:00';
  docDownloadStartDateLocal = '2026-11-05T11:00';
  seekClarificationStartDateLocal = '2026-11-05T12:00';
  seekClarificationEndDateLocal = '2026-11-15T17:00';
  bidSubmissionStartDateLocal = '2026-11-12T10:00';
  bidSubmissionEndDateLocal = '2026-11-20T17:00';
  bidOpenDateLocal = '2026-11-21T11:00';

  // Form State
  isEditMode = false;
  editingId: number | null = null;
  saving = false;
  submittingGePNIC = false;

  // 5. Document Upload State
  attachedDocuments: PreTenderDocumentForm[] = [];
  nitFileName = 'tender-notice.pdf';
  dtcnFileName = 'dtcn-specification.pdf';
  boqFileName = 'financial-boq.xls';

  // Dynamic Dropdowns fetched from PostgreSQL master tables
  tenderCategories: DropdownMasterResponseForm[] = [];
  productCategories: DropdownMasterResponseForm[] = [];
  tenderTypes: DropdownMasterResponseForm[] = [];
  formsOfContract: DropdownMasterResponseForm[] = [];
  tendererClasses: DropdownMasterResponseForm[] = [];
  bidOpenersList: DropdownMasterResponseForm[] = [];

  formData: PreTenderForm = this.getInitialFormData();

  @Input() set editingTender(tender: PreTenderResponseForm | null) {
    if (tender) {
      this.populateForm(tender);
      this.activeTab = 'add';
    } else {
      this.resetForm();
    }
  }

  @Output() formSubmitted = new EventEmitter<void>();
  @Output() formCancelled = new EventEmitter<void>();

  constructor(
    private dropdownService: DropdownService,
    private preTenderService: PreTenderService,
    private notificationService: NotificationService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadDropdownMasters();
    this.loadAvailableWorks();
    this.initDates();
  }

  loadDropdownMasters(): void {
    this.dropdownService.getAllGrouped().subscribe({
      next: (groups) => {
        this.tenderCategories = groups[APP_CONSTANTS.DROPDOWN_CATEGORIES.TENDER_CATEGORY] || [];
        this.productCategories = groups[APP_CONSTANTS.DROPDOWN_CATEGORIES.PRODUCT_CATEGORY] || [];
        this.tenderTypes = groups[APP_CONSTANTS.DROPDOWN_CATEGORIES.TENDER_TYPE] || [];
        this.formsOfContract = groups[APP_CONSTANTS.DROPDOWN_CATEGORIES.FORM_OF_CONTRACT] || [];
        this.tendererClasses = groups[APP_CONSTANTS.DROPDOWN_CATEGORIES.TENDERER_CLASS] || [];
        this.bidOpenersList = groups[APP_CONSTANTS.DROPDOWN_CATEGORIES.BID_OPENERS] || [];
      },
      error: () => {
        this.tenderCategories = [
          { id: 1, category: 'TENDER_CATEGORY', code: 'WORKS', displayName: 'Works', displayOrder: 1, active: true },
          { id: 2, category: 'TENDER_CATEGORY', code: 'GOODS', displayName: 'Goods', displayOrder: 2, active: true },
          { id: 3, category: 'TENDER_CATEGORY', code: 'SERVICES', displayName: 'Services', displayOrder: 3, active: true },
        ];
        this.tenderTypes = [
          { id: 4, category: 'TENDER_TYPE', code: 'NCB', displayName: 'National Competitive Bid', displayOrder: 1, active: true },
          { id: 5, category: 'TENDER_TYPE', code: 'OPEN', displayName: 'Open Tender', displayOrder: 2, active: true },
        ];
        this.formsOfContract = [
          { id: 6, category: 'FORM_OF_CONTRACT', code: 'PERCENTAGE', displayName: 'Percentage', displayOrder: 1, active: true },
          { id: 7, category: 'FORM_OF_CONTRACT', code: 'ITEM_RATE', displayName: 'Item Rate', displayOrder: 2, active: true },
        ];
        this.productCategories = [
          { id: 8, category: 'PRODUCT_CATEGORY', code: 'CIVIL_ROADS', displayName: 'Civil Works - Roads', displayOrder: 1, active: true },
          { id: 9, category: 'PRODUCT_CATEGORY', code: 'BRIDGES', displayName: 'Bridges', displayOrder: 2, active: true },
        ];
        this.tendererClasses = [
          { id: 10, category: 'TENDERER_CLASS', code: 'CLASS_A', displayName: 'A or equivalent', displayOrder: 1, active: true },
          { id: 11, category: 'TENDERER_CLASS', code: 'CLASS_B', displayName: 'Class B', displayOrder: 2, active: true },
        ];
        this.bidOpenersList = [
          { id: 12, category: 'BID_OPENERS', code: '2_OFF_2', displayName: '2 Off 2', displayOrder: 1, active: true },
          { id: 13, category: 'BID_OPENERS', code: '3_OFF_3', displayName: '3 Off 3', displayOrder: 2, active: true },
        ];
      },
    });
  }

  loadAvailableWorks(): void {
    this.preTenderService.getAll().subscribe({
      next: (works) => {
        this.availableWorks = works || [];
        if (works && works.length > 0 && !this.selectedWork) {
          this.selectedWork = works[0];
        }
      },
      error: () => {
        this.availableWorks = [];
      },
    });
  }

  toggleSearchCollapse(): void {
    this.isSearchCollapsed = !this.isSearchCollapsed;
  }

  onExecuteSearch(): void {
    let results = [...this.availableWorks];

    // Filter by ID with operator
    if (this.searchWorkId && this.searchWorkId.trim()) {
      const targetId = Number(this.searchWorkId.trim());
      if (!isNaN(targetId)) {
        if (this.searchIdOperator === '=') {
          results = results.filter((w) => w.id === targetId || w.workId === targetId);
        } else if (this.searchIdOperator === '>') {
          results = results.filter((w) => (w.workId || w.id) > targetId);
        } else if (this.searchIdOperator === '<') {
          results = results.filter((w) => (w.workId || w.id) < targetId);
        }
      } else {
        const idStr = this.searchWorkId.trim().toLowerCase();
        results = results.filter((w) => w.uniqueFileNo?.toLowerCase().includes(idStr));
      }
    }

    // Filter by Work Category
    if (this.searchCategory && this.searchCategory !== 'Any') {
      results = results.filter((w) => w.category?.toLowerCase() === this.searchCategory.toLowerCase());
    }

    // Filter by Tender Type
    if (this.searchTenderType && this.searchTenderType !== 'Any') {
      results = results.filter((w) => w.tenderType?.toLowerCase() === this.searchTenderType.toLowerCase());
    }

    // Filter by Tender Value with operator
    if (this.searchTenderValue != null && !isNaN(this.searchTenderValue)) {
      if (this.searchValueOperator === '=') {
        results = results.filter((w) => w.tenderValue === this.searchTenderValue);
      } else if (this.searchValueOperator === '>=') {
        results = results.filter((w) => w.tenderValue >= (this.searchTenderValue || 0));
      } else if (this.searchValueOperator === '<=') {
        results = results.filter((w) => w.tenderValue <= (this.searchTenderValue || 0));
      }
    }

    // Filter by Work Type / Form of Contract
    if (this.searchFormContract && this.searchFormContract !== 'Any') {
      results = results.filter((w) => w.formContract?.toLowerCase() === this.searchFormContract.toLowerCase());
    }

    // Filter by Scheme / Location
    if (this.searchLocation && this.searchLocation !== 'Any') {
      results = results.filter((w) => w.location?.toLowerCase().includes(this.searchLocation.toLowerCase()));
    }

    // Filter by Status
    if (this.searchStatus && this.searchStatus !== 'Any') {
      results = results.filter((w) => w.status?.toLowerCase() === this.searchStatus.toLowerCase());
    }

    // Filter by Keyword
    if (this.searchKeyword && this.searchKeyword.trim()) {
      const kw = this.searchKeyword.trim().toLowerCase();
      results = results.filter((w) =>
        (w.tenderTitle && w.tenderTitle.toLowerCase().includes(kw)) ||
        (w.tenderReferenceNo && w.tenderReferenceNo.toLowerCase().includes(kw))
      );
    }

    this.matchedWorks = results;

    if (results.length === 1) {
      this.selectWork(results[0]);
      this.notificationService.showSuccess(`Work #${results[0].id} (${results[0].uniqueFileNo}) selected and loaded into Pre-Tender form.`);
    } else if (results.length > 1) {
      this.notificationService.showInfo(`Found ${results.length} matching works. Select one from the list below to load.`);
    } else {
      this.notificationService.showInfo('No works matched the search criteria. You can create a new Pre-Tender record.');
    }
  }

  onResetSearch(): void {
    this.searchIdOperator = '=';
    this.searchWorkId = '';
    this.searchCategory = '';
    this.searchTenderType = '';
    this.searchValueOperator = '=';
    this.searchTenderValue = null;
    this.searchFormContract = '';
    this.searchLocation = '';
    this.searchStatus = '';
    this.searchKeyword = '';
    this.matchedWorks = [];
    this.notificationService.showInfo('Search filters reset.');
  }

  onSelectWorkById(id: number): void {
    const work = this.availableWorks.find((w) => w.id === id);
    if (work) {
      this.selectWork(work);
      this.notificationService.showSuccess(`Work #${work.id} (${work.uniqueFileNo}) loaded into Pre-Tender form.`);
    }
  }

  selectWork(work: PreTenderResponseForm): void {
    this.selectedWork = work;
    this.formData.workId = work.workId || 101;
    this.formData.uniqueFileNo = work.uniqueFileNo || '3/9434/1';
    this.formData.tenderTitle = work.tenderTitle || '';
    this.formData.tenderDescription = work.tenderDescription || '';
    this.formData.category = work.category || 'Works';
    this.formData.location = work.location || 'Civil Works - Roads';
    this.formData.tenderType = work.tenderType || 'National Competitive Bid';
    this.formData.formContract = work.formContract || 'Percentage';
    this.formData.tenderValue = work.tenderValue || 39748169;
    this.formData.feeAmount = work.feeAmount || 10000;
    this.formData.emdAmount = work.emdAmount || 397482;
  }

  onDocumentFileSelected(event: Event, docType: string): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      if (docType === 'NIT') {
        this.nitFileName = file.name;
        this.upsertDocument('NITDOC', file.name, 'Notice Inviting Tender', file.size, '.pdf');
      } else if (docType === 'DTCN') {
        this.dtcnFileName = file.name;
        this.upsertDocument('DTCNDOC', file.name, 'Detailed Tender Call Notice', file.size, '.pdf');
      } else if (docType === 'BOQ') {
        this.boqFileName = file.name;
        this.upsertDocument('BOQDOC', file.name, 'Financial Bill of Quantities', file.size, '.xls');
      }
      this.notificationService.showSuccess(`${docType} document attached: ${file.name}`);
    }
  }

  private upsertDocument(docCode: string, name: string, desc: string, size: number, mime: string): void {
    const existingIdx = this.attachedDocuments.findIndex((d) => d.docCode === docCode);
    const newDoc: PreTenderDocumentForm = {
      docUniqueId: existingIdx >= 0 ? this.attachedDocuments[existingIdx].docUniqueId : this.attachedDocuments.length + 1,
      docCode,
      docName: name,
      docDescription: desc,
      docMimeType: mime,
      docSize: size,
      docPriority: existingIdx >= 0 ? existingIdx + 1 : this.attachedDocuments.length + 1,
      isMandatory: true,
    };
    if (existingIdx >= 0) {
      this.attachedDocuments[existingIdx] = newDoc;
    } else {
      this.attachedDocuments.push(newDoc);
    }
    this.formData.documents = [...this.attachedDocuments];
  }

  removeDocument(index: number): void {
    const doc = this.attachedDocuments[index];
    this.attachedDocuments.splice(index, 1);
    this.formData.documents = [...this.attachedDocuments];
    this.notificationService.showInfo(`Removed document "${doc.docName}".`);
  }

  private initDates(): void {
    this.publishingDateLocal = this.toLocalDatetimeString(this.formData.publishingDate);
    this.docDownloadStartDateLocal = this.toLocalDatetimeString(this.formData.docDownloadStartDate);
    this.seekClarificationStartDateLocal = this.toLocalDatetimeString(this.formData.seekClarificationStartDate);
    this.seekClarificationEndDateLocal = this.toLocalDatetimeString(this.formData.seekClarificationEndDate);
    this.bidSubmissionStartDateLocal = this.toLocalDatetimeString(this.formData.bidSubmissionStartDate);
    this.bidSubmissionEndDateLocal = this.toLocalDatetimeString(this.formData.bidSubmissionEndDate);
    this.bidOpenDateLocal = this.toLocalDatetimeString(this.formData.bidOpenDate);
  }

  private syncDatesToFormData(): void {
    this.formData.publishingDate = this.toBackendIsoString(this.publishingDateLocal);
    this.formData.docDownloadStartDate = this.toBackendIsoString(this.docDownloadStartDateLocal);
    this.formData.seekClarificationStartDate = this.toBackendIsoString(this.seekClarificationStartDateLocal);
    this.formData.seekClarificationEndDate = this.toBackendIsoString(this.seekClarificationEndDateLocal);
    this.formData.bidSubmissionStartDate = this.toBackendIsoString(this.bidSubmissionStartDateLocal);
    this.formData.bidSubmissionEndDate = this.toBackendIsoString(this.bidSubmissionEndDateLocal);
    this.formData.bidOpenDate = this.toBackendIsoString(this.bidOpenDateLocal);

    // Sync BOQ flags
    this.formData.allowItemwiseEvaluation = this.allowItemwiseEvaluation;
    this.formData.allowMultiCurrencyForBoq = this.allowMultiCurrencyForBoq;
    this.formData.allowMultiCurrencyForFee = this.allowMultiCurrencyForFee;
    this.formData.automaticEvaluation = this.automaticEvaluation;
    this.formData.documents = this.attachedDocuments;
  }

  isFormValid(): boolean {
    return !!(
      this.formData.tenderReferenceNo &&
      this.formData.tenderTitle &&
      this.formData.tenderType &&
      this.formData.formContract &&
      this.formData.tenderValue &&
      this.formData.invitingOfficer &&
      this.formData.invitingOfficerAddress
    );
  }

  onSave(): void {
    this.syncDatesToFormData();
    if (!this.isFormValid()) {
      this.notificationService.showError('Please fill in all mandatory fields (marked with *).');
      return;
    }

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: this.isEditMode ? 'Confirm Update Pre-Tender' : 'Confirm Save Pre-Tender',
        message: `Are you sure you want to ${this.isEditMode ? 'update' : 'save'} Pre-Tender "${this.formData.tenderTitle}"?`,
        confirmText: this.isEditMode ? 'Yes, Save' : 'Yes, Save',
        cancelText: 'Cancel',
        isDestructive: false,
      },
      width: '400px',
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.saving = true;
        if (this.isEditMode && this.editingId != null) {
          this.preTenderService.update(this.editingId, this.formData).subscribe({
            next: (res) => {
              this.saving = false;
              this.notificationService.showSuccess(`Pre-Tender "${res.tenderTitle}" updated successfully.`);
              this.formSubmitted.emit();
              this.loadAvailableWorks();
              this.resetForm();
            },
            error: (err) => {
              this.saving = false;
              this.notificationService.showError('Update failed: ' + (err.error?.message || err.message));
            },
          });
        } else {
          this.preTenderService.create(this.formData).subscribe({
            next: (res) => {
              this.saving = false;
              this.notificationService.showSuccess(`Pre-Tender saved successfully. Unique File No: ${res.uniqueFileNo}`);
              this.formSubmitted.emit();
              this.loadAvailableWorks();
              this.resetForm();
            },
            error: (err) => {
              this.saving = false;
              this.notificationService.showError('Save failed: ' + (err.error?.message || err.message));
            },
          });
        }
      }
    });
  }

  onReset(): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Confirm Reset',
        message: 'Are you sure you want to reset all Pre-Tender fields?',
        confirmText: 'Reset Form',
        cancelText: 'Cancel',
        isDestructive: false,
      },
      width: '400px',
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.resetForm();
        this.notificationService.showInfo('Form fields reset to default values.');
      }
    });
  }

  onSubmitToGepnic(): void {
    if (!this.editingId && !this.selectedWork) {
      this.notificationService.showError('Please save the Pre-Tender record first before dispatching to GePNIC.');
      return;
    }
    const targetId = this.editingId || (this.selectedWork ? this.selectedWork.id : null);
    if (!targetId) return;

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: APP_CONSTANTS.CONFIRMATION.GEPNIC_PUSH,
      width: '400px',
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.submittingGePNIC = true;
        this.preTenderService.submitToGepnic(targetId).subscribe({
          next: (res) => {
            this.submittingGePNIC = false;
            this.notificationService.showSuccess(`Pre-Tender dispatched to GePNIC portal! Status: ${res.status}`);
            this.loadAvailableWorks();
          },
          error: (err) => {
            this.submittingGePNIC = false;
            this.notificationService.showError('GePNIC dispatch failed: ' + (err.error?.message || err.message));
          },
        });
      }
    });
  }

  populateForm(tender: PreTenderResponseForm): void {
    this.isEditMode = true;
    this.editingId = tender.id;
    this.selectedWork = tender;
    this.formData = {
      uniqueFileNo: tender.uniqueFileNo,
      tenderReferenceNo: tender.tenderReferenceNo,
      workId: tender.workId || 101,
      officeId: tender.officeId || 10,
      orgId: tender.orgId || 2297,
      tenderTitle: tender.tenderTitle,
      tenderDescription: tender.tenderDescription || '',
      tenderType: tender.tenderType,
      formContract: tender.formContract,
      category: tender.category,
      coversCount: tender.coversCount || 2,
      bidOpeners: tender.bidOpeners || '2 Off 2',
      tenderValue: tender.tenderValue || 0,
      feeAmount: tender.feeAmount || 0,
      emdAmount: tender.emdAmount || 0,
      publishingDate: tender.publishingDate || '2026-11-05T10:00:00',
      docDownloadStartDate: tender.docDownloadStartDate || '2026-11-05T11:00:00',
      seekClarificationStartDate: tender.seekClarificationStartDate || '2026-11-05T12:00:00',
      seekClarificationEndDate: tender.seekClarificationEndDate || '2026-11-15T17:00:00',
      bidSubmissionStartDate: tender.bidSubmissionStartDate || '2026-11-12T10:00:00',
      bidSubmissionEndDate: tender.bidSubmissionEndDate || '2026-11-20T17:00:00',
      bidOpenDate: tender.bidOpenDate || '2026-11-21T11:00:00',
      preBidMeeting: tender.preBidMeeting || false,
      preBidMeetingPlace: tender.preBidMeetingPlace || 'O/o CCE CRWC BBSR',
      preBidMeetingAddress: tender.preBidMeetingAddress || 'O/o CCE CRWC BBSR',
      bidOpenPlace: tender.bidOpenPlace || 'Division Conference Hall',
      location: tender.location || 'Civil Works - Roads',
      pincode: tender.pincode || 751013,
      tendererClass: tender.tendererClass || 'A or equivalent',
      invitingOfficer: tender.invitingOfficer || 'Executive Engineer',
      invitingOfficerAddress: tender.invitingOfficerAddress || 'O/o CCE CRWC BBSR',
      allowItemwiseEvaluation: tender.allowItemwiseEvaluation || false,
      allowMultiCurrencyForBoq: tender.allowMultiCurrencyForBoq || false,
      allowMultiCurrencyForFee: tender.allowMultiCurrencyForFee || false,
      automaticEvaluation: tender.automaticEvaluation || false,
      documents: tender.documents || [],
    };
    this.allowItemwiseEvaluation = !!tender.allowItemwiseEvaluation;
    this.allowMultiCurrencyForBoq = !!tender.allowMultiCurrencyForBoq;
    this.allowMultiCurrencyForFee = !!tender.allowMultiCurrencyForFee;
    this.automaticEvaluation = !!tender.automaticEvaluation;
    this.attachedDocuments = tender.documents && tender.documents.length > 0 ? [...tender.documents] : this.getDefaultDocs();
    this.initDates();
  }

  resetForm(): void {
    this.isEditMode = false;
    this.editingId = null;
    this.selectedWork = null;
    this.formData = this.getInitialFormData();
    this.isEmergencyWork = false;
    this.isDepartmentalWork = false;
    this.isLumpsumWork = false;
    this.allowItemwiseEvaluation = false;
    this.allowMultiCurrencyForBoq = false;
    this.allowMultiCurrencyForFee = false;
    this.automaticEvaluation = false;
    this.attachedDocuments = this.getDefaultDocs();
    this.initDates();
  }

  onEditFromView(tender: PreTenderResponseForm): void {
    this.populateForm(tender);
    this.activeTab = 'add';
  }

  private getDefaultDocs(): PreTenderDocumentForm[] {
    return [
      {
        docUniqueId: 1,
        docCode: 'NITDOC',
        docName: 'tender-notice.pdf',
        docDescription: 'Notice Inviting Tender',
        docMimeType: '.pdf',
        docSize: 35000,
        docPriority: 1,
        isMandatory: true,
      },
      {
        docUniqueId: 2,
        docCode: 'BOQDOC',
        docName: 'financial-boq.xls',
        docDescription: 'Bill of Quantities',
        docMimeType: '.xls',
        docSize: 28000,
        docPriority: 2,
        isMandatory: true,
      },
    ];
  }

  private getInitialFormData(): PreTenderForm {
    return {
      uniqueFileNo: '3/9434/1',
      tenderReferenceNo: 'Tender Online CC 05 of 2023',
      workId: 101,
      officeId: 10,
      orgId: 2297,
      tenderTitle: 'Improvement of Banapur main canal Antarkiyari-Kosarada road in the Dist. of Khordha under Special Central Assistance to States Capital Expenditure (CAPEX) for the year 2022-23',
      tenderDescription: 'Construction, widening and metalling of canal road stretch.',
      tenderType: 'National Competitive Bid',
      formContract: 'Percentage',
      category: 'Works',
      coversCount: 2,
      bidOpeners: '2 Off 2',
      tenderValue: 39748169,
      feeAmount: 10000,
      emdAmount: 397482,
      publishingDate: '2026-11-05T10:00:00',
      docDownloadStartDate: '2026-11-05T11:00:00',
      seekClarificationStartDate: '2026-11-05T12:00:00',
      seekClarificationEndDate: '2026-11-15T17:00:00',
      bidSubmissionStartDate: '2026-11-12T10:00:00',
      bidSubmissionEndDate: '2026-11-20T17:00:00',
      bidOpenDate: '2026-11-21T11:00:00',
      bidValidityDays: 90,
      deliveryPeriodDays: 365,
      preBidMeeting: false,
      preBidMeetingPlace: 'O/o CCE CRWC BBSR',
      preBidMeetingAddress: 'O/o CCE CRWC BBSR',
      bidOpenPlace: 'Division Conference Hall',
      location: 'Civil Works - Roads',
      pincode: 751013,
      tendererClass: 'A or equivalent',
      invitingOfficer: 'Executive Engineer',
      invitingOfficerAddress: 'O/o CCE CRWC BBSR',
      allowItemwiseEvaluation: false,
      allowMultiCurrencyForBoq: false,
      allowMultiCurrencyForFee: false,
      automaticEvaluation: false,
      documents: this.getDefaultDocs(),
    };
  }

  private toLocalDatetimeString(isoString?: string): string {
    if (!isoString) return '';
    if (isoString.includes('/')) {
      const parts = isoString.split(' ');
      const dmy = parts[0].split('/');
      if (dmy.length === 3) {
        const year = dmy[2];
        const month = dmy[1].padStart(2, '0');
        const day = dmy[0].padStart(2, '0');
        const time = parts[1] ? parts[1].substring(0, 5) : '00:00';
        return `${year}-${month}-${day}T${time}`;
      }
    }
    return isoString.substring(0, 16);
  }

  private toBackendIsoString(localDatetime?: string): string {
    if (!localDatetime) return '2026-11-05T10:00:00';
    if (localDatetime.length === 16) {
      return localDatetime + ':00';
    }
    return localDatetime;
  }
}
