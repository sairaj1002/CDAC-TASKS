import { Component, inject, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { forkJoin } from 'rxjs';
import { getApiErrorMessage } from '../../api-response';
import {
  AuditDetail,
  RoadAuditRevision,
  RoadComponentFormValue,
  RoadComponentRequest,
  RoadEnumOption,
  RoadModel,
  RoadService,
} from '../services/road.service';
import {
  isRoadNonPlan,
  ROAD_AUDIT,
  ROAD_MESSAGES,
  ROAD_UI,
  ROAD_VALIDATION,
} from '../../constant/roadconstant';

export type {
  AuditDetail,
  RoadAuditRevision,
  RoadComponentFormValue,
  RoadComponentRequest,
  RoadEnumOption,
  RoadModel,
};

@Component({
  selector: 'app-road-component',
  standalone: false,
  templateUrl: './road-component.html',
})
export class RoadComponent implements OnInit {
  private readonly roadService = inject(RoadService);
  private readonly formBuilder = inject(FormBuilder).nonNullable;
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  // State
  roadComponents: RoadModel[] = [];
  componentToEdit: RoadModel | null = null;
  componentToDelete: RoadModel | null = null;
  saving = false;
  auditLoading = false;

  // Option lists
  natureOfWorks: RoadEnumOption[] = [];
  proposedRoadTypes: RoadEnumOption[] = [];
  workPlanTypes: RoadEnumOption[] = [];
  cdWorkTypes: RoadEnumOption[] = [];
  repairMaintenanceTypes: RoadEnumOption[] = [];

  // Form
  readonly componentForm = this.formBuilder.group({
    natureOfWork: ['', Validators.required],
    proposedRoadType: ['', Validators.required],
    length: new FormControl<number | null>(null, [
      Validators.required,
      Validators.min(ROAD_VALIDATION.MIN_LENGTH),
    ]),
    width: new FormControl<number | null>(null, Validators.min(ROAD_VALIDATION.MIN_WIDTH)),
    workPlanType: ['', Validators.required],
    cdWorkType: ['', Validators.required],
    repairMaintenanceType: [''],
  });

  // Dialogs
  @ViewChild('detailsDialog') detailsDialogTemplate!: TemplateRef<unknown>;
  @ViewChild('auditDialog') auditDialogTemplate!: TemplateRef<unknown>;
  @ViewChild('deleteConfirmDialog') deleteConfirmDialogTemplate!: TemplateRef<unknown>;

  selectedDetailsComponent: RoadModel | null = null;
  selectedAuditDetails: AuditDetail[] = [];
  auditPageIndex: number = ROAD_AUDIT.DEFAULT_PAGE_INDEX;
  auditPageSize: number = ROAD_AUDIT.DEFAULT_PAGE_SIZE;
  readonly auditColumns: string[] = ROAD_AUDIT.COLUMNS;

  ngOnInit(): void {
    this.loadTableData();
  }

  loadTableData(): void {
    forkJoin({
      components: this.roadService.getComponents(),
      natureOfWorks: this.roadService.getNatureOfWorks(),
      proposedRoadTypes: this.roadService.getProposedRoadTypes(),
      workPlanTypes: this.roadService.getWorkPlanTypes(),
      cdWorkTypes: this.roadService.getCdWorkTypes(),
      repairMaintenanceTypes: this.roadService.getRepairMaintenanceTypes(),
    }).subscribe({
      next: (result) => {
        this.roadComponents = this.sortById(result.components);
        this.natureOfWorks = result.natureOfWorks;
        this.proposedRoadTypes = result.proposedRoadTypes;
        this.workPlanTypes = result.workPlanTypes;
        this.cdWorkTypes = result.cdWorkTypes;
        this.repairMaintenanceTypes = result.repairMaintenanceTypes;
        this.loadAuditDetails(this.roadComponents);
      },
      error: (error: unknown) => this.showError(error, ROAD_MESSAGES.LOAD_ERROR),
    });
  }

  loadComponents(): void {
    this.loadTableData();
  }

  loadAuditDetails(components: RoadModel[] = this.roadComponents): void {
    if (!components || components.length === 0) {
      return;
    }
    components.forEach((component) => {
      const id = component.scopeId ?? component.id;
      if (id !== undefined && id !== null) {
        this.roadService.getAuditHistory(id).subscribe({
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

  get serialNumber(): number {
    const editingIndex = this.componentToEdit
      ? this.roadComponents.findIndex(
          (c) => (c.scopeId ?? c.id) === (this.componentToEdit?.scopeId ?? this.componentToEdit?.id),
        )
      : -1;
    return editingIndex >= 0 ? editingIndex + 1 : this.roadComponents.length + 1;
  }

  get showRepairMaintenanceColumn(): boolean {
    const currentPlan = this.componentForm.controls.workPlanType.value;
    return (
      isRoadNonPlan(currentPlan) ||
      isRoadNonPlan(this.componentToEdit?.workPlanType) ||
      this.roadComponents.some((component) => isRoadNonPlan(component.workPlanType))
    );
  }

  onWorkPlanChange(workPlanType: string): void {
    const isNonPlan = isRoadNonPlan(workPlanType);
    const repairMaintenanceType = this.componentForm.controls.repairMaintenanceType;
    if (isNonPlan) {
      repairMaintenanceType.setValidators(Validators.required);
    } else {
      repairMaintenanceType.clearValidators();
      repairMaintenanceType.setValue('');
    }
    repairMaintenanceType.updateValueAndValidity();
  }

  canSave(): boolean {
    return (
      !this.saving &&
      this.natureOfWorks.length > 0 &&
      this.proposedRoadTypes.length > 0 &&
      this.workPlanTypes.length > 0 &&
      this.cdWorkTypes.length > 0 &&
      this.repairMaintenanceTypes.length > 0
    );
  }

  submitForm(): void {
    if (this.componentForm.invalid) {
      this.componentForm.markAllAsTouched();
      this.showWarning(ROAD_MESSAGES.FORM_INVALID_WARNING);
      return;
    }
    this.saveComponent(this.componentForm.getRawValue());
  }

  saveComponent(formValue: RoadComponentFormValue): void {
    const editing = this.componentToEdit;
    const isNonPlan = isRoadNonPlan(formValue.workPlanType);
    const request: RoadComponentRequest = {
      ...formValue,
      repairMaintenanceType: isNonPlan ? formValue.repairMaintenanceType : null,
      officeId: editing?.officeId ?? null,
      workId: editing?.workId ?? null,
    };
    this.saving = true;

    const saveRequest = editing
      ? this.roadService.updateComponent(editing.scopeId ?? editing.id!, request)
      : this.roadService.createComponent(request);

    saveRequest.subscribe({
      next: (component) => {
        this.roadComponents = this.sortById(
          editing
            ? this.roadComponents.map((existing) =>
                (existing.scopeId ?? existing.id) === (component.scopeId ?? component.id)
                  ? component
                  : existing,
              )
            : [...this.roadComponents, component],
        );
        this.cancelEdit();
        this.saving = false;
        this.loadAuditDetails([component]);
        this.showSuccess(editing ? ROAD_MESSAGES.UPDATED_SUCCESS : ROAD_MESSAGES.CREATED_SUCCESS);
      },
      error: (error: unknown) => {
        this.saving = false;
        this.showError(error, ROAD_MESSAGES.SAVE_ERROR);
      },
    });
  }

  editComponent(component: RoadModel): void {
    this.componentToEdit = component;
    this.componentForm.patchValue({
      natureOfWork: this.optionValue(this.natureOfWorks, component.natureOfWork),
      proposedRoadType: this.optionValue(this.proposedRoadTypes, component.proposedRoadType),
      length: component.length,
      width: component.width,
      workPlanType: this.optionValue(this.workPlanTypes, component.workPlanType),
      cdWorkType: this.optionValue(this.cdWorkTypes, component.cdWorkType),
      repairMaintenanceType: component.repairMaintenanceType
        ? this.optionValue(this.repairMaintenanceTypes, component.repairMaintenanceType)
        : '',
    });
    this.onWorkPlanChange(this.componentForm.controls.workPlanType.value);
  }

  cancelEdit(): void {
    this.componentToEdit = null;
    this.componentForm.reset();
    this.onWorkPlanChange('');
  }

  resetForm(): void {
    this.componentForm.reset();
    this.onWorkPlanChange('');
  }

  promptDelete(component: RoadModel): void {
    this.componentToDelete = component;
    const dialogRef = this.dialog.open(this.deleteConfirmDialogTemplate, {
      width: ROAD_UI.CONFIRM_DIALOG_WIDTH,
      panelClass: ROAD_UI.PANEL_CONFIRM,
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
    this.roadService.deleteComponent(id).subscribe({
      next: () => {
        this.roadComponents = this.roadComponents.filter(
          (component) => (component.scopeId ?? component.id) !== id,
        );
        if ((this.componentToEdit?.scopeId ?? this.componentToEdit?.id) === id) {
          this.cancelEdit();
        }
        if (this.roadComponents.length > 0 && this.roadComponents[0].auditDetails) {
          this.selectedAuditDetails = this.roadComponents[0].auditDetails;
        } else {
          this.selectedAuditDetails = [];
        }
        this.showSuccess(ROAD_MESSAGES.DELETED_SUCCESS);
      },
      error: (error: unknown) => this.showError(error, ROAD_MESSAGES.DELETE_ERROR),
    });
  }

  openDetailsDialog(component: RoadModel): void {
    this.selectedDetailsComponent = component;
    this.dialog.open(this.detailsDialogTemplate, {
      width: ROAD_UI.DETAILS_DIALOG_WIDTH,
      maxWidth: ROAD_UI.DIALOG_MAX_WIDTH,
      maxHeight: ROAD_UI.DIALOG_MAX_HEIGHT,
      panelClass: ROAD_UI.PANEL_DETAILS_DIALOG,
    });
  }

  openAuditDialog(target?: RoadModel | AuditDetail[] | null): void {
    const component =
      target && !Array.isArray(target) && 'id' in target ? (target as RoadModel) : null;
    const initialDetails = Array.isArray(target) ? target : component?.auditDetails ?? [];

    this.selectedAuditDetails = initialDetails;
    this.auditPageIndex = ROAD_AUDIT.DEFAULT_PAGE_INDEX;
    this.auditPageSize = ROAD_AUDIT.DEFAULT_PAGE_SIZE;
    this.auditLoading = Boolean(component && (!initialDetails || initialDetails.length === 0));

    this.dialog.open(this.auditDialogTemplate, {
      width: ROAD_UI.DETAILS_DIALOG_WIDTH,
      maxWidth: ROAD_UI.DIALOG_MAX_WIDTH,
      maxHeight: ROAD_UI.DIALOG_MAX_HEIGHT,
      autoFocus: false,
      restoreFocus: true,
      panelClass: ROAD_UI.PANEL_COMPONENT_DIALOG,
    });

    if (component) {
      const id = component.scopeId ?? component.id;
      this.roadService.getAuditHistory(id).subscribe({
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

  private optionValue(
    options: { label: string; value?: string; code?: string }[],
    labelOrCode: string,
  ): string {
    if (!labelOrCode) {
      return '';
    }
    const found = options.find(
      (option) =>
        option.label?.toLowerCase() === labelOrCode.toLowerCase() ||
        (option.value && option.value.toLowerCase() === labelOrCode.toLowerCase()) ||
        (option.code && option.code.toLowerCase() === labelOrCode.toLowerCase()),
    );
    return found ? (found.value ?? found.code ?? labelOrCode) : labelOrCode;
  }

  private showSuccess(message: string): void {
    this.snackBar.open(message, ROAD_UI.SNACKBAR_ACTION_CLOSE, {
      duration: ROAD_UI.SNACKBAR_DURATION_SUCCESS,
      horizontalPosition: ROAD_UI.SNACKBAR_POSITION_HORIZONTAL,
      verticalPosition: ROAD_UI.SNACKBAR_POSITION_VERTICAL,
      panelClass: ROAD_UI.SNACKBAR_PANEL_SUCCESS,
    });
  }

  private showWarning(message: string): void {
    this.snackBar.open(message, ROAD_UI.SNACKBAR_ACTION_CLOSE, {
      duration: ROAD_UI.SNACKBAR_DURATION_WARNING,
      horizontalPosition: ROAD_UI.SNACKBAR_POSITION_HORIZONTAL,
      verticalPosition: ROAD_UI.SNACKBAR_POSITION_VERTICAL,
      panelClass: ROAD_UI.SNACKBAR_PANEL_WARNING,
    });
  }

  private showError(error: unknown, fallbackMessage: string): void {
    const message = getApiErrorMessage(error, fallbackMessage);
    this.snackBar.open(message, ROAD_UI.SNACKBAR_ACTION_CLOSE, {
      duration: ROAD_UI.SNACKBAR_DURATION_ERROR,
      horizontalPosition: ROAD_UI.SNACKBAR_POSITION_HORIZONTAL,
      verticalPosition: ROAD_UI.SNACKBAR_POSITION_VERTICAL,
      panelClass: ROAD_UI.SNACKBAR_PANEL_ERROR,
    });
  }

  private sortById(components: RoadModel[]): RoadModel[] {
    return [...components].sort(
      (left, right) => (left.scopeId ?? left.id ?? 0) - (right.scopeId ?? right.id ?? 0),
    );
  }

  private mapRevisionsToAuditDetails(
    revisions: RoadAuditRevision[],
    component: RoadModel,
  ): AuditDetail[] {
    if (!revisions || revisions.length === 0) {
      return [
        {
          remark: ROAD_AUDIT.DEFAULT_REMARK_CREATED,
          name: component.natureOfWork || ROAD_AUDIT.DEFAULT_FALLBACK_NAME,
          employeeName: ROAD_AUDIT.DEFAULT_EMPLOYEE_NAME,
          designation: ROAD_AUDIT.DEFAULT_DESIGNATION,
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
        : ROAD_AUDIT.DEFAULT_REMARK_REVISION;
      const name = rev.snapshot?.natureOfWork || component.natureOfWork || ROAD_AUDIT.DEFAULT_FALLBACK_NAME;

      return {
        remark: `${type} (Rev #${rev.revisionNumber})`,
        name,
        employeeName: ROAD_AUDIT.DEFAULT_EMPLOYEE_NAME,
        designation: ROAD_AUDIT.DEFAULT_DESIGNATION,
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
