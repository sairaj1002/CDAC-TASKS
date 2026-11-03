import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UploadBoqDialogData } from '../../../common/models/app.models';
import { GePNICService } from '../../../common/services/gepnic.service';
import { NotificationService } from '../../../common/services/notification.service';

@Component({
  selector: 'app-upload-boq-dialog',
  standalone: false,
  templateUrl: './upload-boq-dialog.html',
})
export class UploadBoqDialogComponent {
  selectedFile: File | null = null;
  fileName = 'No file chosen';
  uploading = false;

  constructor(
    public dialogRef: MatDialogRef<UploadBoqDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UploadBoqDialogData,
    private gepnicService: GePNICService,
    private notificationService: NotificationService
  ) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.fileName = this.selectedFile.name;
    }
  }

  onSave(): void {
    if (!this.selectedFile) {
      this.notificationService.showError('Please select a Protected BOQ file (*.xls, *.xlsx) first.');
      return;
    }

    this.uploading = true;
    const formData = new FormData();
    formData.append('uniqueFileNo', this.data.uniqueFileNo || this.data.workNumber);
    formData.append('docuniqueid', '2');
    formData.append('file', this.selectedFile, this.selectedFile.name);

    this.gepnicService.uploadDocument(formData).subscribe({
      next: (resp) => {
        this.uploading = false;
        this.notificationService.showSuccess('Protected BOQ file uploaded successfully to GePNIC.');
        this.dialogRef.close(resp);
      },
      error: (err) => {
        this.uploading = false;
        this.notificationService.showError('Protected BOQ file upload failed: ' + (err.error?.message || err.message));
      },
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}

