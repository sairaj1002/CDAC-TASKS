import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

import { WorkReportFilters, WorkResponse } from '../../models/work.model';
import { WorkService } from '../../services/work.service';

export interface WorkReportDetailsDialogData {
  contractor: string;
  filters: WorkReportFilters;
}

@Component({
  selector: 'app-work-report-details-dialog',
  standalone: false,
  templateUrl: './work-report-details-dialog.component.html',
  styleUrl: './work-report-details-dialog.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkReportDetailsDialogComponent {
  readonly data = inject<WorkReportDetailsDialogData>(MAT_DIALOG_DATA);
  private readonly dialogRef = inject(MatDialogRef<WorkReportDetailsDialogComponent>);
  private readonly workService = inject(WorkService);
  private readonly snackBar = inject(MatSnackBar);
  readonly works = signal<WorkResponse[]>([]);
  readonly loading = signal(true);
  readonly loadFailed = signal(false);

  constructor() {
    this.workService.getReportDetails(this.data.filters).subscribe({
      next: (works) => {
        this.works.set(works);
        this.loading.set(false);
      },
      error: () => {
        this.loadFailed.set(true);
        this.loading.set(false);
        this.snackBar.open('Work details could not be loaded. Please try again later.', 'Dismiss', {
          duration: 6000,
          horizontalPosition: 'center',
          panelClass: ['toast-error'],
          politeness: 'assertive',
          verticalPosition: 'top',
        });
      },
    });
  }

  close(): void {
    this.dialogRef.close();
  }
}
