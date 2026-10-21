import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

import { WorkAuditResponse, WorkResponse } from '../../models/work.model';
import { WorkService } from '../../services/work.service';

export interface WorkAuditDialogData {
  work: WorkResponse;
}

@Component({
  selector: 'app-work-audit-dialog',
  standalone: false,
  templateUrl: './work-audit-dialog.component.html',
  styleUrl: './work-audit-dialog.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuditHistoryDialogComponent {
  readonly data = inject<WorkAuditDialogData>(MAT_DIALOG_DATA);
  private readonly dialogRef = inject(MatDialogRef<AuditHistoryDialogComponent>);
  private readonly workService = inject(WorkService);
  private readonly snackBar = inject(MatSnackBar);
  readonly history = signal<WorkAuditResponse[]>([]);
  readonly loading = signal(true);

  constructor() {
    this.workService.getAuditHistory(this.data.work.workRegNo).subscribe({
      next: (history) => {
        this.history.set(history);
        this.loading.set(false);
      },
      error: () => {
        this.snackBar.open('Audit history could not be loaded. Please try again later.', 'Dismiss', {
          duration: 6000,
          horizontalPosition: 'center',
          panelClass: ['toast-error'],
          politeness: 'assertive',
          verticalPosition: 'top',
        });
        this.loading.set(false);
      },
    });
  }

  close(): void {
    this.dialogRef.close();
  }
}
