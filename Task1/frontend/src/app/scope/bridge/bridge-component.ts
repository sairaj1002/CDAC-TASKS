import { Component, inject, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { getApiErrorMessage } from '../../api-response';
import {
  AuditDetail,
  BridgeAuditRevision,
  BridgeComponentFormValue,
  BridgeComponentRequest,
  BridgeModel,
  BridgeService,
} from '../services/bridge.service';
import { DropdownOption } from '../dropdown-admin/dropdown-option';
import {
  BRIDGE_AUDIT,
  BRIDGE_MESSAGES,
  BRIDGE_UI,
  BRIDGE_VALIDATION,
  isBridgeOtherActivity,
} from '../../constant/bridgeconstant';

export type {
  AuditDetail,
  BridgeAuditRevision,
  BridgeComponentFormValue,
  BridgeComponentRequest,
  BridgeModel,
};

@Component({
  selector: 'app-bridge-component',
  standalone: false,
  templateUrl: './bridge-component.html',
})
export class BridgeComponent implements OnInit {
  private readonly bridgeService = inject(BridgeService);
  private readonly formBuilder = inject(FormBuilder).nonNullable;
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  // State
  bridgeComponents: BridgeModel[] = [];
  bridgeActivities: string[] = [];
  bridgeActivityOptions: DropdownOption[] = [];
  componentToEdit: BridgeModel | null = null;
  componentToDelete: BridgeModel | null = null;
  saving = false;
  auditLoading = false;

  // Form
  readonly componentForm = this.formBuilder.group({
    riverNallahBarrier: [
      '',
      [
        Validators.required,
        Validators.pattern(BRIDGE_VALIDATION.PATTERN_NON_WHITESPACE),
        Validators.maxLength(BRIDGE_VALIDATION.MAX_RIVER_NALLAH_BARRIER),
      ],
    ],
    width: new FormControl<number | null>(null, Validators.min(BRIDGE_VALIDATION.MIN_WIDTH)),
    length: new FormControl<number | null>(null, Validators.min(BRIDGE_VALIDATION.MIN_LENGTH)),
    approvalRoad: [
      '',
      [
        Validators.required,
        Validators.pattern(BRIDGE_VALIDATION.PATTERN_NON_WHITESPACE),
        Validators.maxLength(BRIDGE_VALIDATION.MAX_APPROVAL_ROAD),
      ],
    ],
    numberOfSpans: new FormControl<number | null>(null, [
      Validators.min(BRIDGE_VALIDATION.MIN_NUMBER_OF_SPANS),
      Validators.pattern(BRIDGE_VALIDATION.PATTERN_INTEGER_ONLY),
    ]),
    spanLength: new FormControl<number | null>(null, Validators.min(BRIDGE_VALIDATION.MIN_SPAN_LENGTH)),
    bridgeActivity: ['', Validators.required],
    otherActivityDescription: new FormControl('', {
      nonNullable: true,
      validators: Validators.maxLength(BRIDGE_VALIDATION.MAX_OTHER_ACTIVITY_DESC),
    }),
  });

  // Dialogs
  @ViewChild('detailsDialog') detailsDialogTemplate!: TemplateRef<unknown>;
  @ViewChild('auditDialog') auditDialogTemplate!: TemplateRef<unknown>;
  @ViewChild('deleteConfirmDialog') deleteConfirmDialogTemplate!: TemplateRef<unknown>;

  selectedDetailsComponent: BridgeModel | null = null;
  selectedAuditDetails: AuditDetail[] = [];
  auditPageIndex: number = BRIDGE_AUDIT.DEFAULT_PAGE_INDEX;
  auditPageSize: number = BRIDGE_AUDIT.DEFAULT_PAGE_SIZE;
  readonly auditColumns: string[] = BRIDGE_AUDIT.COLUMNS;

  ngOnInit(): void {
    this.loadTableData();
    this.loadActivities();
  }

  get serialNumber(): number {
    const editingIndex = this.componentToEdit
      ? this.bridgeComponents.findIndex(
        (c) => (c.scopeId ?? c.id) === (this.componentToEdit?.scopeId ?? this.componentToEdit?.id),
      )
      : -1;

    return editingIndex >= 0 ? editingIndex + 1 : this.bridgeComponents.length + 1;
  }

  get showOtherActivityDescriptionColumn(): boolean {
    const act = this.componentForm.controls.bridgeActivity.value;
    return (
      isBridgeOtherActivity(act) ||
      this.bridgeComponents.some((component) => isBridgeOtherActivity(component.bridgeActivity))
    );
  }

  onBridgeActivityChange(activity: string): void {
    const isOther = isBridgeOtherActivity(activity);
    const description = this.componentForm.controls.otherActivityDescription;
    if (isOther) {
      description.setValidators(Validators.required);
    } else {
      description.clearValidators();
      description.reset('');
    }
    description.updateValueAndValidity();
  }

  submitForm(): void {
    if (
      (this.bridgeActivities.length === 0 && this.bridgeActivityOptions.length === 0) ||
      this.componentForm.invalid
    ) {
      this.componentForm.markAllAsTouched();
      this.showWarning(BRIDGE_MESSAGES.FORM_INVALID_WARNING);
      return;
    }

    this.saveComponent(this.componentForm.getRawValue());
  }

  saveComponent(formValue: BridgeComponentFormValue): void {
    const editing = this.componentToEdit;
    const isOther = isBridgeOtherActivity(formValue.bridgeActivity);

    const request: BridgeComponentRequest = {
      ...formValue,
      otherActivityDescription: isOther ? (formValue.otherActivityDescription || '') : '',
      officeId: editing?.officeId ?? null,
      workId: editing?.workId ?? null,
    };

    this.saving = true;
    const saveRequest = editing
      ? this.bridgeService.updateComponent(editing.scopeId ?? editing.id!, request)
      : this.bridgeService.createComponent(request);

    saveRequest.subscribe({
      next: (component) => {
        this.bridgeComponents = this.sortById(
          editing
            ? this.bridgeComponents.map((existing) =>
              (existing.scopeId ?? existing.id) === (component.scopeId ?? component.id)
                ? component
                : existing,
            )
            : [...this.bridgeComponents, component],
        );
        this.cancelEdit();
        this.saving = false;
        this.loadAuditDetails([component]);
        this.showSuccess(editing ? BRIDGE_MESSAGES.UPDATED_SUCCESS : BRIDGE_MESSAGES.CREATED_SUCCESS);
      },
      error: (error: unknown) => {
        this.saving = false;
        this.showError(error, BRIDGE_MESSAGES.SAVE_ERROR);
      },
    });
  }

  editComponent(component: BridgeModel): void {
    this.componentToEdit = component;
    this.componentForm.patchValue({
      riverNallahBarrier: component.riverNallahBarrier,
      width: component.width,
      length: component.length,
      approvalRoad: component.approvalRoad,
      numberOfSpans: component.numberOfSpans,
      spanLength: component.spanLength,
      bridgeActivity: this.optionValue(this.bridgeActivityOptions, component.bridgeActivity),
      otherActivityDescription: component.otherActivityDescription ?? '',
    });
    this.onBridgeActivityChange(this.componentForm.controls.bridgeActivity.value);
  }

  cancelEdit(): void {
    this.componentToEdit = null;
    this.componentForm.reset();
    this.onBridgeActivityChange('');
  }

  resetForm(): void {
    this.componentForm.reset();
    this.onBridgeActivityChange('');
  }

  promptDelete(component: BridgeModel): void {
    this.componentToDelete = component;
    const dialogRef = this.dialog.open(this.deleteConfirmDialogTemplate, {
      width: BRIDGE_UI.CONFIRM_DIALOG_WIDTH,
      panelClass: BRIDGE_UI.PANEL_CONFIRM,
      autoFocus: false,
      disableClose: false,
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed && this.componentToDelete) {
        this.deleteComponent(this.componentToDelete.scopeId ?? this.componentToDelete.id);
      }
      this.componentToDelete = null;
    });
  }

  deleteComponent(id: number): void {
    this.bridgeService.deleteComponent(id).subscribe({
      next: () => {
        this.bridgeComponents = this.bridgeComponents.filter(
          (component) => (component.scopeId ?? component.id) !== id,
        );
        if ((this.componentToEdit?.scopeId ?? this.componentToEdit?.id) === id) {
          this.cancelEdit();
        }
        if (this.bridgeComponents.length > 0 && this.bridgeComponents[0].auditDetails) {
          this.selectedAuditDetails = this.bridgeComponents[0].auditDetails;
        } else {
          this.selectedAuditDetails = [];
        }
        this.showSuccess(BRIDGE_MESSAGES.DELETED_SUCCESS);
      },
      error: (error: unknown) => this.showError(error, BRIDGE_MESSAGES.DELETE_ERROR),
    });
  }

  openDetailsDialog(component: BridgeModel): void {
    this.selectedDetailsComponent = component;
    this.dialog.open(this.detailsDialogTemplate, {
      width: BRIDGE_UI.DETAILS_DIALOG_WIDTH,
      maxWidth: BRIDGE_UI.DIALOG_MAX_WIDTH,
      maxHeight: BRIDGE_UI.DIALOG_MAX_HEIGHT,
      panelClass: BRIDGE_UI.PANEL_DETAILS_DIALOG,
    });
  }

  openAuditDialog(target?: BridgeModel | AuditDetail[] | null): void {
    const component =
      target && !Array.isArray(target) && 'id' in target ? (target as BridgeModel) : null;
    const initialDetails = Array.isArray(target) ? target : component?.auditDetails ?? [];

    this.selectedAuditDetails = initialDetails;
    this.auditPageIndex = BRIDGE_AUDIT.DEFAULT_PAGE_INDEX;
    this.auditPageSize = BRIDGE_AUDIT.DEFAULT_PAGE_SIZE;
    this.auditLoading = Boolean(component && (!initialDetails || initialDetails.length === 0));

    this.dialog.open(this.auditDialogTemplate, {
      width: BRIDGE_UI.DETAILS_DIALOG_WIDTH,
      maxWidth: BRIDGE_UI.DIALOG_MAX_WIDTH,
      maxHeight: BRIDGE_UI.DIALOG_MAX_HEIGHT,
      autoFocus: false,
      restoreFocus: true,
      panelClass: BRIDGE_UI.PANEL_COMPONENT_DIALOG,
    });

    if (component) {
      const id = component.scopeId ?? component.id;
      this.bridgeService.getAuditHistory(id).subscribe({
        next: (revisions) => {
          const mapped = this.mapRevisionsToAuditDetails(revisions, component);
          component.auditDetails = mapped;
          this.selectedAuditDetails = mapped;
          this.auditLoading = false;
        },
        error: () => {
          this.auditLoading = false;
          if (this.selectedAuditDetails.length === 0) {
            const fallback = this.mapRevisionsToAuditDetails([], component);
            component.auditDetails = fallback;
            this.selectedAuditDetails = fallback;
          }
        },
      });
    }
  }

  get pagedAuditDetails(): AuditDetail[] {
    const start = this.auditPageIndex * this.auditPageSize;
    return this.selectedAuditDetails.slice(start, start + this.auditPageSize);
  }

  onAuditPageChange(event: PageEvent): void {
    this.auditPageIndex = event.pageIndex;
    this.auditPageSize = event.pageSize;
  }

  loadTableData(): void {
    this.loadComponents();
  }

  loadComponents(): void {
    this.bridgeService.getComponents().subscribe({
      next: (components) => {
        this.bridgeComponents = this.sortById(components);
        this.loadAuditDetails(this.bridgeComponents);
      },
      error: (error: unknown) => this.showError(error, BRIDGE_MESSAGES.LOAD_ERROR),
    });
  }

  loadAuditDetails(components: BridgeModel[] = this.bridgeComponents): void {
    if (!components || components.length === 0) {
      return;
    }
    components.forEach((component) => {
      const id = component.scopeId ?? component.id;
      if (id !== undefined && id !== null) {
        this.bridgeService.getAuditHistory(id).subscribe({
          next: (revisions) => {
            const mapped = this.mapRevisionsToAuditDetails(revisions, component);
            component.auditDetails = mapped;
            if (this.selectedAuditDetails.length === 0) {
              this.selectedAuditDetails = mapped;
            }
          },
          error: () => {
            const fallback = this.mapRevisionsToAuditDetails([], component);
            component.auditDetails = fallback;
            if (this.selectedAuditDetails.length === 0) {
              this.selectedAuditDetails = fallback;
            }
          },
        });
      }
    });
  }

  private loadActivities(): void {
    this.bridgeService.getBridgeActivityOptions().subscribe({
      next: (options) => {
        this.bridgeActivityOptions = options;
        this.bridgeActivities = options.map((opt) => opt.label);
      },
      error: () => {
        this.bridgeService.getBridgeActivities().subscribe({
          next: (activities) => {
            this.bridgeActivities = activities;
          },
          error: (error: unknown) =>
            this.showError(error, BRIDGE_MESSAGES.ACTIVITIES_LOAD_ERROR),
        });
      },
    });
  }

  private optionValue(options: DropdownOption[], labelOrCode: string): string {
    if (!labelOrCode) {
      return '';
    }
    const found = options.find(
      (opt) =>
        opt.label?.toLowerCase() === labelOrCode.toLowerCase() ||
        opt.code?.toLowerCase() === labelOrCode.toLowerCase(),
    );
    return found ? found.code : labelOrCode;
  }

  private showSuccess(message: string): void {
    this.snackBar.open(message, BRIDGE_UI.SNACKBAR_ACTION_CLOSE, {
      duration: BRIDGE_UI.SNACKBAR_DURATION_SUCCESS,
      horizontalPosition: BRIDGE_UI.SNACKBAR_POSITION_HORIZONTAL,
      verticalPosition: BRIDGE_UI.SNACKBAR_POSITION_VERTICAL,
      panelClass: BRIDGE_UI.SNACKBAR_PANEL_SUCCESS,
    });
  }

  private showWarning(message: string): void {
    this.snackBar.open(message, BRIDGE_UI.SNACKBAR_ACTION_CLOSE, {
      duration: BRIDGE_UI.SNACKBAR_DURATION_WARNING,
      horizontalPosition: BRIDGE_UI.SNACKBAR_POSITION_HORIZONTAL,
      verticalPosition: BRIDGE_UI.SNACKBAR_POSITION_VERTICAL,
      panelClass: BRIDGE_UI.SNACKBAR_PANEL_WARNING,
    });
  }

  private showError(error: unknown, fallbackMessage: string): void {
    const message = getApiErrorMessage(error, fallbackMessage);
    this.snackBar.open(message, BRIDGE_UI.SNACKBAR_ACTION_CLOSE, {
      duration: BRIDGE_UI.SNACKBAR_DURATION_ERROR,
      horizontalPosition: BRIDGE_UI.SNACKBAR_POSITION_HORIZONTAL,
      verticalPosition: BRIDGE_UI.SNACKBAR_POSITION_VERTICAL,
      panelClass: BRIDGE_UI.SNACKBAR_PANEL_ERROR,
    });
  }

  private sortById(components: BridgeModel[]): BridgeModel[] {
    return [...components].sort(
      (left, right) => (left.scopeId ?? left.id ?? 0) - (right.scopeId ?? right.id ?? 0),
    );
  }

  private mapRevisionsToAuditDetails(
    revisions: BridgeAuditRevision[],
    component: BridgeModel,
  ): AuditDetail[] {
    if (!revisions || revisions.length === 0) {
      return [
        {
          remark: BRIDGE_AUDIT.DEFAULT_REMARK_CREATED,
          name: component.riverNallahBarrier || BRIDGE_AUDIT.DEFAULT_FALLBACK_NAME,
          employeeName: BRIDGE_AUDIT.DEFAULT_EMPLOYEE_NAME,
          designation: BRIDGE_AUDIT.DEFAULT_DESIGNATION,
          timestamp:
            component.createdAt ||
            component.revisionTimestamp ||
            component.updatedAt ||
            new Date().toISOString(),
        },
      ];
    }

    return revisions.map((rev) => {
      const type = rev.revisionType
        ? rev.revisionType.charAt(0).toUpperCase() + rev.revisionType.slice(1).toLowerCase()
        : BRIDGE_AUDIT.DEFAULT_REMARK_REVISION;
      const name = rev.snapshot?.riverNallahBarrier || component.riverNallahBarrier || BRIDGE_AUDIT.DEFAULT_FALLBACK_NAME;

      return {
        remark: `${type} (Rev #${rev.revisionNumber})`,
        name,
        employeeName: BRIDGE_AUDIT.DEFAULT_EMPLOYEE_NAME,
        designation: BRIDGE_AUDIT.DEFAULT_DESIGNATION,
        timestamp:
          rev.revisionDate ||
          rev.snapshot?.revisionTimestamp ||
          rev.snapshot?.updatedAt ||
          rev.snapshot?.createdAt ||
          null,
      };
    });
  }
}
