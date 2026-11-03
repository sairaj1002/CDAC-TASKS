import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { APP_CONSTANTS } from '../../../common/constants/app.constants';
import {
  DropdownMasterResponseForm,
  PreTenderForm,
  PreTenderResponseForm,
  SubWorkDetail,
  WorkDetail,
} from '../../../common/models/app.models';
import { DropdownService } from '../../../common/services/dropdown.service';
import { PreTenderService } from '../../../common/services/pretender.service';
import { WorkService } from '../../../common/services/work.service';
import { NotificationService } from '../../../common/services/notification.service';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog';

@Component({
  selector: 'app-pretender-form',
  changeDetection: ChangeDetectionStrategy.Eager,
  standalone: false,
  templateUrl: './pretender-form.html',
})
export class PreTenderFormChildComponent implements OnInit {
  @Input() set editingTender(tender: PreTenderResponseForm | null) {
    if (tender) {
      this.isEditMode = true;
      this.populateForm(tender);
    } else {
      this.resetForm();
    }
  }

  @Output() formSubmitted = new EventEmitter<void>();
  @Output() formCancelled = new EventEmitter<void>();

  isEditMode = false;
  editingId: number | null = null;
  saving = false;

  // A tender is raised against one sub work: the work is searched first, then one of its sub works
  workQuery: string | WorkDetail = '';
  workOptions: WorkDetail[] = [];
  selectedWork: WorkDetail | null = null;
  subWorkQuery: string | SubWorkDetail = '';
  subWorkOptions: SubWorkDetail[] = [];
  selectedSubWork: SubWorkDetail | null = null;
  // the work description filled in from the chosen sub work (replaced when another sub work is chosen)
  private prefilledTitle = '';

  // Dynamic dropdown options from backend
  tenderTypes: DropdownMasterResponseForm[] = [];
  formsOfContract: DropdownMasterResponseForm[] = [];
  tenderCategories: DropdownMasterResponseForm[] = [];
  productCategories: DropdownMasterResponseForm[] = [];
  tendererClasses: DropdownMasterResponseForm[] = [];
  bidOpenersList: DropdownMasterResponseForm[] = [];
  divisionOffices: DropdownMasterResponseForm[] = [];

  formData: PreTenderForm = {
    uniqueFileNo: '',
    tenderReferenceNo: '',
    workId: 101,
    officeId: 10,
    orgId: 2297,
    tenderTitle: '',
    tenderDescription: '',
    // master dropdowns start empty: the user must choose each one
    tenderType: '',
    formContract: '',
    category: '',
    productCategory: '',
    coversCount: 2,
    bidOpeners: '',
    tenderValue: 500000,
    feeAmount: 1000,
    emdAmount: 10000,
    publishingDate: '2026-11-05T10:00:00',
    docDownloadStartDate: '2026-11-05T11:00:00',
    seekClarificationStartDate: '2026-11-06T10:00:00',
    seekClarificationEndDate: '2026-11-10T17:00:00',
    preBidMeeting: false,
    preBidMeetingPlace: 'O/o CCE CRWC BBSR',
    preBidMeetingAddress: 'O/o CCE CRWC BBSR',
    bidSubmissionStartDate: '2026-11-12T10:00:00',
    bidSubmissionEndDate: '2026-11-20T17:00:00',
    bidOpenDate: '2026-11-21T11:00:00',
    bidValidityDays: 90,
    deliveryPeriodDays: 365,
    location: 'Khordha',
    pincode: 751013,
    bidOpenPlace: 'Division Office',
    tendererClass: '',
    invitingOfficer: 'Executive Engineer',
    invitingOfficerAddress: 'PWD Division Office',
    documents: [
      {
        docUniqueId: 1,
        docCode: 'NITDOC',
        docName: 'tender-notice.pdf',
        docDescription: 'Notice Inviting Tender',
        docMimeType: '.pdf',
        docSize: 35000,
        docPriority: 1,
      },
      {
        docUniqueId: 2,
        docCode: 'BOQDOC',
        docName: 'financial-boq.xls',
        docDescription: 'Bill of Quantities',
        docMimeType: '.xls',
        docSize: 28000,
        docPriority: 2,
      },
    ],
  };

  constructor(
    private dropdownService: DropdownService,
    private preTenderService: PreTenderService,
    private workService: WorkService,
    private notificationService: NotificationService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadDropdownMasters();
    this.onWorkQuery('');
  }

  /** The tender details stay disabled until a sub work is chosen (an edited tender already has its work). */
  get detailsLocked(): boolean {
    return !this.isEditMode && !this.selectedSubWork;
  }

  readonly workLabel = (work: WorkDetail | string | null): string =>
    typeof work === 'string' ? work : work ? `${work.registrationNumber} - ${work.nameOfWork}` : '';

  readonly subWorkLabel = (subWork: SubWorkDetail | string | null): string =>
    typeof subWork === 'string' ? subWork : subWork ? `${subWork.registrationNumber} - ${subWork.description}` : '';

  onWorkQuery(query: string | WorkDetail): void {
    // typing after a work was chosen starts a new search
    if (typeof query !== 'string') {
      return;
    }
    if (this.selectedWork) {
      this.clearWork(false);
    }
    this.workService.searchWorks(query).subscribe((works) => (this.workOptions = works));
  }

  onWorkSelected(work: WorkDetail): void {
    this.selectedWork = work;
    this.formData.workId = work.id;
    this.clearSubWork();
  }

  clearWork(resetQuery = true): void {
    this.selectedWork = null;
    if (resetQuery) {
      this.workQuery = '';
      this.onWorkQuery('');
    }
    this.clearSubWork();
    this.subWorkOptions = [];
  }

  onSubWorkQuery(query: string | SubWorkDetail): void {
    if (typeof query !== 'string' || !this.selectedWork) {
      return;
    }
    this.selectedSubWork = null;
    this.workService.searchSubWorks(this.selectedWork.id, query).subscribe((subWorks) => (this.subWorkOptions = subWorks));
  }

  onSubWorkSelected(subWork: SubWorkDetail): void {
    this.selectedSubWork = subWork;
    // the sub work description is the usual work description of its tender; a text the user typed is kept
    if (!this.formData.tenderTitle || this.formData.tenderTitle === this.prefilledTitle) {
      this.formData.tenderTitle = subWork.description;
      this.prefilledTitle = subWork.description;
    }
  }

  clearSubWork(): void {
    this.subWorkQuery = '';
    this.selectedSubWork = null;
    // a description that only came from the previous sub work goes with it
    if (this.prefilledTitle && this.formData.tenderTitle === this.prefilledTitle) {
      this.formData.tenderTitle = '';
    }
    this.prefilledTitle = '';
    this.onSubWorkQuery('');
  }

  // Product categories belong to a tender category (carried in the option description)
  get filteredProductCategories(): DropdownMasterResponseForm[] {
    const matching = this.productCategories.filter((opt) => opt.description === this.formData.category);
    return matching.length ? matching : this.productCategories;
  }

  onTenderCategoryChange(): void {
    this.formData.productCategory = '';
  }

  loadDropdownMasters(): void {
    this.dropdownService.getAllGrouped().subscribe({
      next: (groups) => {
        this.tenderTypes = groups[APP_CONSTANTS.DROPDOWN_CATEGORIES.TENDER_TYPE] || [];
        this.formsOfContract = groups[APP_CONSTANTS.DROPDOWN_CATEGORIES.FORM_OF_CONTRACT] || [];
        this.tenderCategories = groups[APP_CONSTANTS.DROPDOWN_CATEGORIES.TENDER_CATEGORY] || [];
        this.productCategories = groups[APP_CONSTANTS.DROPDOWN_CATEGORIES.PRODUCT_CATEGORY] || [];
        this.tendererClasses = groups[APP_CONSTANTS.DROPDOWN_CATEGORIES.TENDERER_CLASS] || [];
        this.bidOpenersList = groups[APP_CONSTANTS.DROPDOWN_CATEGORIES.BID_OPENERS] || [];
        this.divisionOffices = groups[APP_CONSTANTS.DROPDOWN_CATEGORIES.DIVISION_OFFICE] || [];
      },
      error: () => {
        this.notificationService.showWarning('Could not load some master dropdowns from backend. Using standard defaults.');
      },
    });
  }

  private populateForm(tender: PreTenderResponseForm): void {
    this.editingId = tender.id;
    this.formData = {
      ...this.formData,
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
      productCategory: tender.productCategory || '',
      coversCount: tender.coversCount,
      bidOpeners: tender.bidOpeners,
      tendererClass: tender.tendererClass,
      tenderValue: tender.tenderValue,
      feeAmount: tender.feeAmount,
      emdAmount: tender.emdAmount,
      publishingDate: tender.publishingDate,
      docDownloadStartDate: tender.docDownloadStartDate,
      bidSubmissionStartDate: tender.bidSubmissionStartDate,
      bidSubmissionEndDate: tender.bidSubmissionEndDate,
      bidOpenDate: tender.bidOpenDate,
      documents: tender.documents || this.formData.documents,
    };
    // show the work of the edited tender (its sub work is not stored with the tender yet)
    this.selectedSubWork = null;
    this.workService.getWork(this.formData.workId).subscribe((work) => (this.selectedWork = work ?? null));
  }

  resetForm(): void {
    this.isEditMode = false;
    this.editingId = null;
    const rnd = Math.floor(Math.random() * 9000) + 1000;
    this.formData.uniqueFileNo = `3/${rnd}/1`;
    this.formData.tenderReferenceNo = `Tender Online CC ${rnd} of 2026`;
    this.formData.tenderTitle = '';
    this.formData.tenderDescription = '';
    this.formData.tenderType = '';
    this.formData.formContract = '';
    this.formData.category = '';
    this.formData.productCategory = '';
    this.formData.bidOpeners = '';
    this.formData.tendererClass = '';
  }

  onSubmit(): void {
    if (this.detailsLocked) {
      this.notificationService.showError('Please select the work and the sub work for this tender.');
      return;
    }
    if (
      !this.formData.uniqueFileNo ||
      !this.formData.tenderReferenceNo ||
      !this.formData.tenderTitle ||
      !this.formData.tenderType ||
      !this.formData.formContract ||
      !this.formData.category ||
      !this.formData.productCategory ||
      !this.formData.bidOpeners ||
      !this.formData.tendererClass
    ) {
      this.notificationService.showError('Please fill in all mandatory fields marked with an asterisk (*).');
      return;
    }

    const confirmData = this.isEditMode
      ? APP_CONSTANTS.CONFIRMATION.UPDATE
      : {
          title: 'Save Pre-Tender',
          message: 'Are you sure you want to register this new Pre-Tender work record?',
          confirmButtonText: 'Save Record',
          cancelButtonText: 'Cancel',
        };

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: confirmData,
      width: '400px',
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.saving = true;
        if (this.isEditMode && this.editingId) {
          this.preTenderService.update(this.editingId, this.formData).subscribe({
            next: () => {
              this.saving = false;
              this.notificationService.showSuccess('Pre-Tender record updated successfully.');
              this.formSubmitted.emit();
            },
            error: (err) => {
              this.saving = false;
              this.notificationService.showError('Update failed: ' + (err.error?.message || err.message));
            },
          });
        } else {
          this.preTenderService.create(this.formData).subscribe({
            next: () => {
              this.saving = false;
              this.notificationService.showSuccess('Pre-Tender record created successfully.');
              this.formSubmitted.emit();
            },
            error: (err) => {
              this.saving = false;
              this.notificationService.showError('Creation failed: ' + (err.error?.message || err.message));
            },
          });
        }
      }
    });
  }

  onValidate(): void {
    if (this.editingId) {
      this.preTenderService.validate(this.editingId).subscribe({
        next: () => {
          this.notificationService.showSuccess('Pre-Tender business validation passed. All invariants satisfied!');
        },
        error: (err) => {
          this.notificationService.showError('Validation failed: ' + (err.error?.message || err.message));
        },
      });
    } else {
      this.notificationService.showInfo('Save the record first to run comprehensive database rule validation.');
    }
  }

  onCancel(): void {
    this.formCancelled.emit();
  }
}

