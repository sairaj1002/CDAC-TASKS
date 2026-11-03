import { ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { OthersRecord, OthersService, UnitOption } from '../services/others.service';
import { AuditDetails } from '../../building/components/audit-details/audit-details';
import { WorkContext } from '../../../../core/services/work-context';
import { ConfirmDeleteDialog } from '../../../../shared/components/confirm-delete-dialog';
import { FeedbackSnackBarService } from '../../../../shared/services/feedback-snackbar.service';
import { OTHERS_MESSAGES, OTHERS_UI, OTHERS_VALIDATION } from '../constants/others-constants';

@Component({ selector: 'app-others', standalone: false, templateUrl: './others.html' })
export class Others implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(OthersService);
  private readonly dialog = inject(MatDialog);
  private readonly feedback = inject(FeedbackSnackBarService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly workContext = inject(WorkContext);
  @ViewChild('draftDescription') private draftDescription?: ElementRef<HTMLInputElement>;
  units: UnitOption[] = [];
  records: OthersRecord[] = [];
  loading = false;
  loadingUnits = false;
  saving = false;
  errorMessage = '';
  editingId: number | null = null;
  adding = false;
  pageIndex = 0;
  pageSize: number = OTHERS_UI.DEFAULT_PAGE_SIZE;
  totalElements = 0;
  totalPages = 0;
  readonly pageSizeOptions = OTHERS_UI.PAGE_SIZE_OPTIONS;
  readonly form: FormGroup = this.createForm();
  ngOnInit(): void { this.loadUnits(); this.loadRecords(); }
  private createForm(): FormGroup { return this.fb.group({ description: ['', [Validators.required, Validators.maxLength(OTHERS_VALIDATION.DESCRIPTION_MAX_LENGTH)]], unitId: [null, Validators.required], quantity: [null, [Validators.required, Validators.min(OTHERS_VALIDATION.QUANTITY_MIN)]] }); }
  loadUnits(): void { this.loadingUnits = true; this.service.getUnits().subscribe({ next: u => { this.units = u; this.loadingUnits = false; this.cdr.markForCheck(); }, error: () => { this.errorMessage = OTHERS_MESSAGES.UNITS_LOAD_ERROR; this.loadingUnits = false; this.cdr.markForCheck(); } }); }
  loadRecords(page = this.pageIndex): void {
    this.loading = true;
    this.service.getOthers(page, this.pageSize).subscribe({ next: p => { this.records = p.content ?? []; this.totalElements = p.totalElements ?? 0; this.totalPages = p.totalPages ?? 0; this.pageIndex = p.number ?? page; this.pageSize = p.size ?? this.pageSize; if (this.totalElements > 0 && this.pageIndex === 0 && this.adding) this.adding = false; else if (this.totalElements === 0 && this.editingId === null) this.adding = true; this.loading = false; this.cdr.markForCheck(); }, error: e => { this.errorMessage = e?.error?.message ?? OTHERS_MESSAGES.LOAD_ERROR; this.loading = false; this.cdr.markForCheck(); } });
  }
  startAdd(): void { if (this.editingId !== null) return; if (!this.adding) { this.adding = true; this.form.reset(); this.errorMessage = ''; } setTimeout(() => this.draftDescription?.nativeElement.focus()); }
  cancelAdd(): void {
    this.form.reset();
    this.errorMessage = '';
    this.adding = true;
  }
  resetForm(): void {
    this.form.reset();
    this.errorMessage = '';
  }
  edit(row: OthersRecord): void { if (this.saving) return; this.adding = false; this.editingId = row.id; this.form.setValue({ description: row.description, unitId: row.unit.id, quantity: row.quantity }); this.errorMessage = ''; }
  cancelEdit(): void { this.editingId = null; this.form.reset(); }
  save(): void {
    if (this.form.invalid || this.saving) {
      this.form.markAllAsTouched();
      if (this.editingId === null && this.form.invalid) this.showError('Please fill in all required fields before adding an item.');
      return;
    }
    const v = this.form.getRawValue(); const id = this.editingId;
    const request = { description: String(v.description).trim(), unitId: Number(v.unitId), quantity: Number(v.quantity), ...(id === null && this.workContext.workId !== null ? { workId: this.workContext.workId } : {}) };
    this.saving = true; this.errorMessage = '';
    const op = id === null ? this.service.createOther(request) : this.service.updateOther(id, request);
    op.subscribe({ next: row => { if (id === null) { this.workContext.workId = row.workId ?? this.workContext.workId; this.records = [row, ...this.records].slice(0, this.pageSize); this.totalElements++; this.adding = true; } else { this.records = this.records.map(r => r.id === row.id ? row : r); this.editingId = null; } this.form.reset(); this.saving = false; this.showSuccess(id === null ? OTHERS_MESSAGES.CREATED_SUCCESS : OTHERS_MESSAGES.UPDATED_SUCCESS); this.cdr.markForCheck(); }, error: e => { this.showError(e?.status === 0 ? OTHERS_MESSAGES.BACKEND_UNAVAILABLE : e?.error?.message ?? e?.message ?? OTHERS_MESSAGES.SAVE_ERROR); this.saving = false; this.cdr.markForCheck(); } });
  }
  delete(row: OthersRecord): void { if (this.saving) return; this.dialog.open(ConfirmDeleteDialog, { width: OTHERS_UI.CONFIRM_DIALOG_WIDTH, panelClass: OTHERS_UI.DELETE_PANEL_CLASS, data: { title: 'Delete Record', message: `Are you sure you want to delete "${row.description}"?` } }).afterClosed().subscribe(confirmed => { if (confirmed) this.confirmDelete(row); }); }
  private confirmDelete(row: OthersRecord): void { this.errorMessage = ''; this.service.deleteOther(row.id).subscribe({ next: () => { this.records = this.records.filter(r => r.id !== row.id); this.totalElements = Math.max(0, this.totalElements - 1); if (!this.records.length && this.pageIndex > 0) this.pageIndex--; this.showSuccess(OTHERS_MESSAGES.DELETED_SUCCESS); this.loadRecords(this.pageIndex); }, error: e => this.showError(e?.error?.message ?? OTHERS_MESSAGES.DELETE_ERROR) }); }
  viewAudit(row: OthersRecord): void { this.service.getAuditHistory(row.id).subscribe({ next: auditDetails => this.dialog.open(AuditDetails, { width: OTHERS_UI.AUDIT_DIALOG_WIDTH, maxWidth: OTHERS_UI.DIALOG_MAX_WIDTH, maxHeight: OTHERS_UI.DIALOG_MAX_HEIGHT, autoFocus: false, panelClass: OTHERS_UI.AUDIT_PANEL_CLASS, data: { building: row, auditDetails } }), error: e => this.errorMessage = e?.error?.message ?? OTHERS_MESSAGES.AUDIT_LOAD_ERROR }); }
  changePage(page: number): void { if (page >= 0 && page < this.totalPages && page !== this.pageIndex) this.loadRecords(page); }
  changePageSize(size: string): void { this.pageSize = Number(size); this.loadRecords(0); }
  get serialOffset(): number { return this.pageIndex * this.pageSize; }
  private showSuccess(message: string): void { this.feedback.success(message); }
  private showError(message: string): void { this.feedback.error(message); }
}
