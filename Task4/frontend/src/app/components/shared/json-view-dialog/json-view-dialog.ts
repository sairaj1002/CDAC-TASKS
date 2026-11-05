import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { NotificationService } from '../../../common/services/notification.service';

export interface JsonViewDialogData {
  title: string;
  subtitle?: string;
  fileName: string;
  json: unknown;
}

/** Shows a JSON document (e.g. the GePNIC tender JSON) formatted, with Copy and Download. */
@Component({
  selector: 'app-json-view-dialog',
  changeDetection: ChangeDetectionStrategy.Eager,
  standalone: false,
  templateUrl: './json-view-dialog.html',
})
export class JsonViewDialogComponent {
  readonly text: string;

  constructor(
    public dialogRef: MatDialogRef<JsonViewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: JsonViewDialogData,
    private notificationService: NotificationService
  ) {
    this.text = JSON.stringify(data.json, null, 2);
  }

  onCopy(): void {
    navigator.clipboard.writeText(this.text).then(
      () => this.notificationService.showSuccess('JSON copied to the clipboard.'),
      () => this.notificationService.showError('The JSON could not be copied.')
    );
  }

  onDownload(): void {
    const url = URL.createObjectURL(new Blob([this.text], { type: 'application/json' }));
    const link = document.createElement('a');
    link.href = url;
    link.download = this.data.fileName;
    link.click();
    URL.revokeObjectURL(url);
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
