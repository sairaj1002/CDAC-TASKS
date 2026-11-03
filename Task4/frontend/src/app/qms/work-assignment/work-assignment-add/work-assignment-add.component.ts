import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { WorkAssignmentService, DropdownItem, WorkItem } from './work-assignment.service';
import { SnackbarService } from '../../../common/services/snackbar.service';

@Component({
  selector: 'app-work-assignment-add',
  standalone: false,
  templateUrl: './work-assignment-add.component.html',
})
export class WorkAssignmentAddComponent implements OnInit {

  workAssignmentForm!: FormGroup;

  // Master data populated from backend
  districtList: DropdownItem[] = [];
  divisionList: DropdownItem[] = [];
  workTypeList: DropdownItem[] = [];
  workCategoryList: DropdownItem[] = [];

  // Filled dynamically when clicking "Get Works"
  workList: WorkItem[] = [];
  showWorkDetails = false;

  // Allocation data passed to child component <app-allocation-details>
  selectedWorkAllocation: any = null;

  // Database-driven row count (from qms_parameter_config)
  configuredRowCount = 2;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly workAssignmentService: WorkAssignmentService,
    private readonly cdr: ChangeDetectorRef,
    private readonly snackbar: SnackbarService
  ) { }

  ngOnInit(): void {
    this.workAssignmentForm = this.formBuilder.group({
      district: [null, Validators.required],
      division: [null, Validators.required],
      workType: [null, Validators.required],
      workCategory: [null, Validators.required],
      workDescription: ['', Validators.maxLength(200)],
      workDetails: [null, Validators.required],
    });

    this.loadFilterDropdowns();
    this.loadConfiguredRowCount();
  }

  loadFilterDropdowns(): void {
    this.workAssignmentService.getDistricts().subscribe({
      next: (data) => this.districtList = data,
      error: (err) => this.loadFailed('districts', err)
    });

    this.workAssignmentService.getDivisions().subscribe({
      next: (data) => this.divisionList = data,
      error: (err) => this.loadFailed('divisions', err)
    });

    this.workAssignmentService.getWorkTypes().subscribe({
      next: (data) => this.workTypeList = data,
      error: (err) => this.loadFailed('work types', err)
    });

    this.workAssignmentService.getWorkCategories().subscribe({
      next: (data) => this.workCategoryList = data,
      error: (err) => this.loadFailed('work categories', err)
    });
  }

  loadConfiguredRowCount(): void {
    this.workAssignmentService.getAllocationRowCount().subscribe({
      next: (count) => {
        this.configuredRowCount = count || 2;
        console.log('Dynamic row count loaded from DB parameter:', this.configuredRowCount);
      },
      error: (err) => {
        console.warn('Could not load row count from backend, fallback to 2:', err);
        this.configuredRowCount = 2;
        this.snackbar.showError('Could not load the number of allocation rows. The default of 2 is used.');
      }
    });
  }

  clearFilter(fieldName: string, event?: MouseEvent): void {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    this.workAssignmentForm.get(fieldName)?.setValue(null);
    this.workAssignmentForm.get(fieldName)?.markAsUntouched();

    if (fieldName === 'workDetails') {
      this.selectedWorkAllocation = null;
    } else if (fieldName !== 'workDescription') {
      this.workList = [];
      this.showWorkDetails = false;
      this.selectedWorkAllocation = null;
    }
  }

  getWorks(): void {
    const filters = ['district', 'division', 'workType', 'workCategory'];
    filters.forEach(name => this.workAssignmentForm.get(name)?.markAsTouched());
    if (filters.some(name => this.workAssignmentForm.get(name)?.invalid)) {
      return;
    }

    const divisionId = this.workAssignmentForm.get('division')?.value;
    const workTypeId = this.workAssignmentForm.get('workType')?.value;
    const workCategoryId = this.workAssignmentForm.get('workCategory')?.value;

    this.workAssignmentService.getWorks(divisionId, workTypeId, workCategoryId).subscribe({
      next: (works) => {
        this.workList = works;
        this.showWorkDetails = true;
        // Do NOT auto-select work; let the user pick from the dropdown
        this.workAssignmentForm.patchValue({ workDetails: null });
        this.selectedWorkAllocation = null;
      },
      error: (err) => {
        console.error('Error fetching works:', err);
        this.snackbar.showHttpError(err, 'Could not fetch works from server.');
      }
    });
  }

  onWorkSelect(workId: number): void {
    if (workId) {
      this.loadAllocationForWork(workId);
    } else {
      this.selectedWorkAllocation = null;
    }
  }

  loadAllocationForWork(workId: number): void {
    // Clear previous allocation state before loading the selected work
    this.selectedWorkAllocation = null;
    this.cdr.detectChanges();

    this.workAssignmentService.getAllocationsByWorkId(workId).subscribe({
      next: (existingAllocations) => {
        if (existingAllocations && existingAllocations.length > 0) {
          // If work has already been allocated in DB, display the saved rows
          this.selectedWorkAllocation = {
            workId: workId,
            allocations: existingAllocations
          };
        } else {
          // Otherwise, unallocated work: fresh blank rows with placeholders
          this.selectedWorkAllocation = {
            workId: workId,
            requiredCount: this.configuredRowCount,
            allocations: []
          };
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.selectedWorkAllocation = null;
        this.snackbar.showHttpError(err, 'Could not load the allocation of this work.');
        this.cdr.detectChanges();
      }
    });
  }

  // Dropdown lists the page needs; the user is told, the page keeps working with what it has
  private loadFailed(what: string, err: unknown): void {
    console.error(`Error fetching ${what}:`, err);
    this.snackbar.showHttpError(err, `Could not load the ${what}.`);
  }

  cancel(): void {
    this.workAssignmentForm.reset({ workDescription: '' });
    this.workList = [];
    this.showWorkDetails = false;
    this.selectedWorkAllocation = null;
  }
}
