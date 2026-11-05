import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { WorkAssignmentService, DropdownItem, WorkAllocationPayload } from './work-assignment.service';
import { SnackbarService } from '../../../common/services/snackbar.service';

@Component({
  selector: 'app-allocation-details',
  templateUrl: './allocation-details.component.html',
  changeDetection: ChangeDetectionStrategy.Default,
  standalone: false
})
export class AllocationDetailsComponent implements OnInit {

  currentWorkId: number | null = null;

  @Input() set workDetails(details: any) {
    if (details && details.workId) {
      this.currentWorkId = details.workId;
      this.populateFromWorkDetails(details);
    } else {
      this.currentWorkId = null;
      if (this.allocationForm && this.rows) {
        this.rows.clear();
      }
      this.usersByRow = {};
      this.cdr.detectChanges();
    }
  }

  allocationForm!: FormGroup;

  // 5 standard roles
  approvalLevels: string[] = [
    'Approver',
    'Verifier',
    'Recorder',
    'Quality Monitor',
    'Independent Quality Monitor'
  ];

  designations: DropdownItem[] = [];
  offices: DropdownItem[] = [];

  // Per-row user lists dependent on (designationId + officeId)
  usersByRow: { [rowIndex: number]: DropdownItem[] } = {};

  constructor(
    private readonly fb: FormBuilder,
    private readonly workAssignmentService: WorkAssignmentService,
    private readonly cdr: ChangeDetectorRef,
    private readonly snackbar: SnackbarService
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    if (!this.allocationForm) {
      this.initForm();
    }
    this.loadDropdowns();
  }

  initForm(): void {
    this.allocationForm = this.fb.group({
      rows: this.fb.array([])
    });
  }

  loadDropdowns(): void {
    this.workAssignmentService.getDesignations().subscribe({
      next: (data) => {
        this.designations = data || [];
        this.cdr.detectChanges();
      },
      error: (err) => this.loadFailed('designations', err)
    });

    this.workAssignmentService.getOffices().subscribe({
      next: (data) => {
        this.offices = data || [];
        this.cdr.detectChanges();
      },
      error: (err) => this.loadFailed('offices', err)
    });
  }

  get rows(): FormArray {
    if (!this.allocationForm) {
      this.initForm();
    }
    return this.allocationForm.get('rows') as FormArray;
  }

  createRow(srNo: number, defaultLevel: string | null = null): FormGroup {
    return this.fb.group({
      srNo: [srNo],
      approvalLevel: [defaultLevel, Validators.required],
      designation: [null, Validators.required],
      office: [null, Validators.required],
      user: [null, Validators.required]
    });
  }

  onDesignationOrOfficeChange(rowIndex: number): void {
    const row = this.rows.at(rowIndex);
    const desigId = row.get('designation')?.value;
    const officeId = row.get('office')?.value;

    row.get('user')?.setValue(null);

    if (desigId && officeId) {
      this.workAssignmentService.getUsers(desigId, officeId).subscribe({
        next: (users) => {
          // Create a new object reference so Angular detects the change in template
          this.usersByRow = { ...this.usersByRow, [rowIndex]: users || [] };
          this.cdr.detectChanges();

          if (users && users.length > 0) {
            // Auto-select the first user by default
            row.get('user')?.setValue(users[0].id);
          } else {
            row.get('user')?.setValue(null);
          }
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.loadFailed('users', err);
          this.usersByRow = { ...this.usersByRow, [rowIndex]: [] };
          row.get('user')?.setValue(null);
          this.cdr.detectChanges();
        }
      });
    } else {
      this.usersByRow = { ...this.usersByRow, [rowIndex]: [] };
      row.get('user')?.setValue(null);
      this.cdr.detectChanges();
    }
  }

  clearField(rowIndex: number, fieldName: string, event?: MouseEvent): void {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    const row = this.rows.at(rowIndex);
    row.get(fieldName)?.setValue(null);
    row.get(fieldName)?.markAsUntouched();

    if (fieldName === 'designation' || fieldName === 'office') {
      this.onDesignationOrOfficeChange(rowIndex);
    } else {
      this.cdr.markForCheck();
      this.cdr.detectChanges();
    }
  }

  populateFromWorkDetails(details: any): void {
    if (!details) return;

    this.rows.clear();
    this.usersByRow = {};

    // CASE A: If existing saved allocations are present (Allocated Work), load them
    if (details.allocations && details.allocations.length > 0) {
      details.allocations.forEach((item: any, index: number) => {
        const row = this.createRow(index + 1, item.approvalLevel || null);
        row.patchValue({
          designation: item.designationId || null,
          office: item.officeId || null,
          user: null
        });
        this.rows.push(row);

        // Fetch users for this designation and office, then set user value
        if (item.designationId && item.officeId) {
          this.workAssignmentService.getUsers(item.designationId, item.officeId).subscribe({
            next: (users) => {
              this.usersByRow = { ...this.usersByRow, [index]: users || [] };
              if (item.userId) {
                row.get('user')?.setValue(item.userId);
              } else if (users && users.length > 0) {
                row.get('user')?.setValue(users[0].id);
              }
              this.cdr.detectChanges();
            },
            error: (err) => {
              this.loadFailed('users', err);
              this.usersByRow = { ...this.usersByRow, [index]: [] };
              this.cdr.detectChanges();
            }
          });
        }
      });
      this.cdr.detectChanges();
      return;
    }

    // CASE B: Unallocated work -> Generate completely blank rows showing placeholders
    const count = details.requiredCount || 2;
    for (let i = 1; i <= count; i++) {
      this.rows.push(this.createRow(i, null));
    }
    this.cdr.detectChanges();
  }

  private loadFailed(what: string, err: unknown): void {
    console.error(`Error fetching ${what}:`, err);
    this.snackbar.showHttpError(err, `Could not load the ${what}.`);
  }

  onSave(): void {
    if (this.allocationForm.invalid) {
      this.allocationForm.markAllAsTouched();
      this.snackbar.showError('Please fill all required fields in all rows.');
      return;
    }

    if (!this.currentWorkId) {
      this.snackbar.showError('Please select a work before saving allocation.');
      return;
    }

    const rawRows = this.allocationForm.value.rows;
    const payload: WorkAllocationPayload[] = rawRows.map((r: any, idx: number) => {
      const desigObj = this.designations.find(d => d.id === r.designation);
      const officeObj = this.offices.find(o => o.id === r.office);
      const userList = this.usersByRow[idx] || [];
      const userObj = userList.find(u => u.id === r.user);

      return {
        workId: this.currentWorkId!,
        srNo: idx + 1,
        approvalLevel: r.approvalLevel,
        designationId: r.designation,
        designationName: desigObj?.name || '',
        officeId: r.office,
        officeName: officeObj?.name || '',
        userId: r.user,
        userName: userObj?.name || ''
      };
    });

    this.workAssignmentService.saveAllocations(payload).subscribe({
      next: (response) => {
        this.snackbar.showMessage(response);
      },
      error: (err) => {
        console.error('Error saving allocation:', err);
        this.snackbar.showHttpError(err, 'Failed to save work allocation.');
      }
    });
  }
}
