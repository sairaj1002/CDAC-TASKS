import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AocRecord, AocDocumentDetail } from '../../../common/models/app.models';
import { PostTenderService } from '../../../common/services/post-tender.service';
import { NotificationService } from '../../../common/services/notification.service';

@Component({
  selector: 'app-aoc-details-dialog',
  standalone: false,
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './aoc-details-dialog.html',
})
export class AocDetailsDialogComponent {
  qualifiedColumns = ['sNo', 'bidId', 'bidderName', 'gstin', 'quotedValue', 'bidSubmittedDate'];
  nonQualifiedColumns = ['sNo', 'bidId', 'bidderName', 'gstin', 'bidSubmittedDate'];

  constructor(
    public dialogRef: MatDialogRef<AocDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AocRecord,
    private postTenderService: PostTenderService,
    private notificationService: NotificationService
  ) {}

  onClose(): void {
    this.dialogRef.close();
  }

  onInsertInAoc(): void {
    this.postTenderService.insertInAoc(this.data.id).subscribe({
      next: (res) => {
        this.notificationService.showSuccess(res.message);
        this.data.isInsertedInAoc = true;
      },
      error: (err) => {
        this.notificationService.showError('Failed to insert in AOC: ' + (err.error?.message || err.message));
      },
    });
  }

  onDownloadDoc(doc: AocDocumentDetail): void {
    this.postTenderService
      .downloadAocDocument({
        docType: doc.docType,
        bId: doc.bId,
        docCode: doc.docCode,
        fileName: doc.fileName,
      })
      .subscribe({
        next: (res) => {
          this.notificationService.showSuccess(`Downloading ${doc.docType} file: ${doc.fileName}`);
        },
      });
  }
}

