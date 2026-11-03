import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { PreTenderResponseForm } from '../../../common/models/app.models';

@Component({
  selector: 'app-view-details-dialog',
  changeDetection: ChangeDetectionStrategy.Eager,
  standalone: false,
  templateUrl: './view-details-dialog.html',
})
export class ViewDetailsDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ViewDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public tender: PreTenderResponseForm
  ) {}

  onClose(): void {
    this.dialogRef.close();
  }
}

