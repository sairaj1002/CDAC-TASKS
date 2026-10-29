import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

export interface DetailsDialogData {
  title: string;
  details: Record<string, any>;
}

@Component({
  selector: 'app-details-dialog',
  standalone: false,
  templateUrl: './details-dialog.component.html'
})
export class DetailsDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<DetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DetailsDialogData
  ) {}

  getEntries(): { key: string; value: any }[] {
    if (!this.data || !this.data.details) return [];
    return Object.entries(this.data.details).map(([key, value]) => ({
      key: this.formatKey(key),
      value: typeof value === 'object' ? JSON.stringify(value, null, 2) : String(value)
    }));
  }

  formatKey(key: string): string {
    return key.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase());
  }

  close(): void {
    this.dialogRef.close();
  }
}
