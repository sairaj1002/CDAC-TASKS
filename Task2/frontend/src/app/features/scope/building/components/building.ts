import { ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { finalize } from 'rxjs';
import { AuditDetails } from './audit-details/audit-details';
import {
  BuildingRecord,
  BuildingService,
  DropdownOption,
  PageResponse,
} from '../services/building.service';
import { WorkContext } from '../../../../core/services/work-context';
import { ConfirmDeleteDialog } from '../../../../shared/components/confirm-delete-dialog';
import { FeedbackSnackBarService } from '../../../../shared/services/feedback-snackbar.service';
import { BUILDING_CATEGORIES, BUILDING_MESSAGES, BUILDING_UI, BUILDING_VALIDATION } from '../constants/building-constants';

@Component({ selector: 'app-building', standalone: false, templateUrl: './building.html' })
export class Building implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(BuildingService);
  private readonly dialog = inject(MatDialog);
  private readonly feedback = inject(FeedbackSnackBarService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly workContext = inject(WorkContext);
  @ViewChild('draftDescription') private draftDescription?: ElementRef<HTMLInputElement>;
  maintenanceTypes: DropdownOption[] = [];
  buildingActivities: DropdownOption[] = [];
  get filteredBuildingActivities(): DropdownOption[] {
    const selected = String(this.form.controls['maintenanceType']?.value ?? '').toLowerCase();
    if (!selected) return this.buildingActivities.filter((option) => /^(SLR|CR|SDR)\s*\d+/i.test(option.label));
    const prefix = selected.includes('current') || selected.startsWith('cr') ? 'CR ' :
      selected.includes('special') || selected.startsWith('slr') ? 'SLR ' :
      selected.includes('fdr') || selected.startsWith('sdr') || selected.includes('specified') ? 'SDR ' : '';
    return prefix ? this.buildingActivities.filter((option) => option.label.startsWith(prefix)) : this.buildingActivities;
  }
  records: BuildingRecord[] = [];
  loading = false;
  loadingDropdowns = false;
  saving = false;
  errorMessage = '';
  editingId: number | null = null;
  adding = false;
  pageIndex = 0;
  pageSize: number = BUILDING_UI.DEFAULT_PAGE_SIZE;
  totalElements = 0;
  totalPages = 0;
  readonly pageSizeOptions = BUILDING_UI.PAGE_SIZE_OPTIONS;
  readonly form: FormGroup = this.createForm();
  get buildingForm(): FormGroup {
    return this.form;
  }

  onMaintenanceTypeChange(): void {
    this.form.controls['buildingActivity'].reset('');
  }

  ngOnInit(): void {
    this.loadDropdowns();
    this.loadBuildings();
  }
  private createForm(): FormGroup {
    return this.fb.group({
      buildingDescription: ['', [Validators.required, Validators.maxLength(BUILDING_VALIDATION.DESCRIPTION_MAX_LENGTH)]],
      builtUpArea: [null, [Validators.required, Validators.min(BUILDING_VALIDATION.AREA_MIN)]],
      numberOfFloors: [null, [Validators.required, Validators.min(BUILDING_VALIDATION.COUNT_MIN), Validators.pattern(BUILDING_VALIDATION.PATTERN_INTEGER)]],
      numberOfBasements: [
        null,
        [Validators.required, Validators.min(BUILDING_VALIDATION.COUNT_MIN), Validators.pattern(BUILDING_VALIDATION.PATTERN_INTEGER)],
      ],
      maintenanceType: ['', Validators.required],
      buildingActivity: ['', Validators.required],
    });
  }
  loadDropdowns(): void {
    this.loadingDropdowns = true;
    let done = 0;
    const finish = () => {
      if (++done === 2) this.loadingDropdowns = false;
    };
    this.service
      .getOptions(BUILDING_CATEGORIES.MAINTENANCE_TYPE)
      .pipe(finalize(finish))
      .subscribe({
        next: (v) => (this.maintenanceTypes = v),
        error: () => (this.errorMessage = BUILDING_MESSAGES.LOAD_MAINTENANCE_TYPES_ERROR),
      });
    this.service
      .getOptions(BUILDING_CATEGORIES.BUILDING_ACTIVITY)
      .pipe(finalize(finish))
      .subscribe({
        next: (v) => (this.buildingActivities = v),
        error: () => (this.errorMessage = BUILDING_MESSAGES.LOAD_ACTIVITIES_ERROR),
      });
  }
  loadBuildings(page = this.pageIndex): void {
    this.loading = true;
    this.service.getAllBuildings(page, this.pageSize).subscribe({
      next: (result) => {
        if (Array.isArray(result)) {
          this.records = result;
          this.totalElements = result.length;
          this.totalPages = result.length ? 1 : 0;
        } else {
          const p = result as PageResponse<BuildingRecord>;
          this.records = p.content ?? [];
          this.totalElements = p.totalElements ?? 0;
          this.totalPages = p.totalPages ?? 0;
          this.pageIndex = p.number ?? page;
        }
        if (this.totalElements === 0 && this.editingId === null) this.adding = true;
        else if (this.pageIndex === 0 && this.editingId === null) this.adding = false;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (e) => {
        this.errorMessage = e?.error?.message ?? BUILDING_MESSAGES.LOAD_ERROR;
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }
  startAdd(): void {
    if (this.editingId !== null) return;
    if (!this.adding) {
      this.adding = true;
      this.form.reset();
      this.errorMessage = '';
    }
    setTimeout(() => this.draftDescription?.nativeElement.focus());
  }
  cancelAdd(): void {
    this.form.reset();
    this.errorMessage = '';
    this.adding = true;
  }
  resetForm(): void {
    this.form.reset();
    this.errorMessage = '';
  }
  edit(row: BuildingRecord): void {
    if (this.saving) return;
    this.adding = false;
    this.editingId = row.id;
    this.form.patchValue(row);
    this.errorMessage = '';
  }
  cancelEdit(): void {
    this.editingId = null;
    this.form.reset();
    this.errorMessage = '';
  }
  save(): void {
    if (this.form.invalid || this.saving || this.loadingDropdowns) {
      this.form.markAllAsTouched();
      if (this.editingId === null && this.form.invalid) {
        this.showError('Please fill in all required fields before adding the building.');
      }
      return;
    }
    const v = this.form.getRawValue();
    const request = {
      buildingDescription: String(v.buildingDescription).trim(),
      builtUpArea: Number(v.builtUpArea),
      numberOfFloors: Number(v.numberOfFloors),
      numberOfBasements: Number(v.numberOfBasements),
      maintenanceType: v.maintenanceType,
      buildingActivity: v.buildingActivity,
      ...(this.editingId === null && this.workContext.workId !== null
        ? { workId: this.workContext.workId }
        : {}),
    };
    const editingId = this.editingId;
    this.saving = true;
    this.errorMessage = '';
    const op =
      editingId === null
        ? this.service.createBuilding(request)
        : this.service.updateBuilding(editingId, request);
    op.subscribe({
      next: (saved) => {
        if (editingId === null) {
          this.workContext.workId = saved.workId ?? this.workContext.workId;
          this.records = [saved, ...this.records].slice(0, this.pageSize);
          this.totalElements++;
          this.adding = true;
          this.editingId = null;
          this.form.reset();
        } else {
          this.records = this.records.map((r) => (r.id === saved.id ? saved : r));
          this.editingId = null;
          this.form.reset();
        }
        this.saving = false;
        this.showSuccess(editingId === null ? BUILDING_MESSAGES.CREATED_SUCCESS : BUILDING_MESSAGES.UPDATED_SUCCESS);
        this.cdr.markForCheck();
      },
      error: (e) => {
        this.showError(
          e?.status === 0
            ? BUILDING_MESSAGES.BACKEND_UNAVAILABLE
            : (e?.error?.message ?? e?.message ?? BUILDING_MESSAGES.SAVE_ERROR),
        );
        this.saving = false;
        this.cdr.markForCheck();
      },
    });
  }
  delete(row: BuildingRecord): void {
    if (this.saving) return;
    this.dialog.open(ConfirmDeleteDialog, {
      width: BUILDING_UI.CONFIRM_DIALOG_WIDTH,
      panelClass: BUILDING_UI.DELETE_PANEL_CLASS,
      data: { title: 'Delete Building', message: `Are you sure you want to delete "${row.buildingDescription}"?` },
    }).afterClosed().subscribe(confirmed => { if (confirmed) this.confirmDelete(row); });
  }
  private confirmDelete(row: BuildingRecord): void {
    this.errorMessage = '';
    this.service.deleteBuilding(row.id).subscribe({
      next: () => {
        this.records = this.records.filter((r) => r.id !== row.id);
        this.totalElements = Math.max(0, this.totalElements - 1);
        if (!this.records.length && this.pageIndex > 0) this.pageIndex--;
        this.showSuccess(BUILDING_MESSAGES.DELETED_SUCCESS);
        this.loadBuildings(this.pageIndex);
      },
      error: (e) => this.showError(e?.error?.message ?? BUILDING_MESSAGES.DELETE_ERROR),
    });
  }
  viewAudit(row: BuildingRecord): void {
    this.service
      .getAuditHistory(row.id)
      .subscribe({
        next: (auditDetails) =>
          this.dialog.open(AuditDetails, {
            width: BUILDING_UI.AUDIT_DIALOG_WIDTH,
            maxWidth: BUILDING_UI.DIALOG_MAX_WIDTH,
            maxHeight: BUILDING_UI.DIALOG_MAX_HEIGHT,
            autoFocus: false,
            panelClass: BUILDING_UI.AUDIT_PANEL_CLASS,
            data: { building: row, auditDetails },
          }),
        error: (e) => (this.errorMessage = e?.error?.message ?? BUILDING_MESSAGES.AUDIT_LOAD_ERROR),
      });
  }
  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages && page !== this.pageIndex) this.loadBuildings(page);
  }
  changePageSize(size: string): void {
    this.pageSize = Number(size);
    this.loadBuildings(0);
  }
  get serialOffset(): number {
    return this.pageIndex * this.pageSize;
  }
  private showSuccess(message: string): void {
    this.feedback.success(message);
  }
  private showError(message: string): void {
    this.feedback.error(message);
  }
}
