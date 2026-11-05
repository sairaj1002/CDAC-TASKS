import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { WorkAssignmentService, DropdownItem, WorkAllocationPayload } from '../work-assignment-add/work-assignment.service';
import { SnackbarService } from '../../../common/services/snackbar.service';

@Component({
  selector: 'app-work-assignment-view',
  templateUrl: './work-assignment-view.component.html',
  changeDetection: ChangeDetectionStrategy.Default,
  standalone: false
})
export class WorkAssignmentViewComponent implements OnInit {

  viewForm!: FormGroup;

  // Dropdown list containing ONLY allocated works
  allocatedWorks: DropdownItem[] = [];

  // Saved officer allocations for the selected work
  allocations: WorkAllocationPayload[] = [];
  selectedWorkId: number | null = null;
  isLoading = false;

  constructor(
    private readonly fb: FormBuilder,
    private readonly workAssignmentService: WorkAssignmentService,
    private readonly snackbar: SnackbarService
  ) { }

  ngOnInit(): void {
    this.viewForm = this.fb.group({
      workDetails: [null, Validators.required]
    });

    this.loadAllocatedWorks();
  }

  // Refreshes the allocated works list whenever called
  loadAllocatedWorks(): void {
    this.workAssignmentService.getAllocatedWorks().subscribe({
      next: (works) => {
        this.allocatedWorks = works || [];
      },
      error: (err) => {
        console.error('Error fetching allocated works:', err);
        this.snackbar.showHttpError(err, 'Could not load the allocated works.');
        this.allocatedWorks = [];
      }
    });
  }

  onWorkSelect(workId: number): void {
    if (!workId) {
      this.allocations = [];
      this.selectedWorkId = null;
      return;
    }

    this.selectedWorkId = workId;
    this.isLoading = true;

    this.workAssignmentService.getAllocationsByWorkId(workId).subscribe({
      next: (data) => {
        this.allocations = data || [];
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading allocations for view:', err);
        this.snackbar.showHttpError(err, 'Could not load the allocation of this work.');
        this.allocations = [];
        this.isLoading = false;
      }
    });
  }

  clearWork(event?: MouseEvent): void {
    if (event) {
      event.stopPropagation();
    }
    this.viewForm.get('workDetails')?.setValue(null);
    this.allocations = [];
    this.selectedWorkId = null;
  }
}
