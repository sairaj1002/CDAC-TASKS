import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { PreTenderDocumentResponse, PreTenderResponseForm } from '../../../common/models/app.models';
import { PreTenderService } from '../../../common/services/pretender.service';
import {
  TenderDocumentsDialogComponent,
  TenderDocumentsDialogData,
} from '../tender-documents-dialog/tender-documents-dialog';

/** Read-only view of a saved Pre-Tender (View tab: "View Pre-tender details"). */
@Component({
  selector: 'app-view-details-dialog',
  changeDetection: ChangeDetectionStrategy.Eager,
  standalone: false,
  templateUrl: './view-details-dialog.html',
})
export class ViewDetailsDialogComponent implements OnInit {
  documents: PreTenderDocumentResponse[] = [];
  documentsLoaded = false;

  constructor(
    public dialogRef: MatDialogRef<ViewDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public tender: PreTenderResponseForm,
    private preTenderService: PreTenderService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadDocuments();
  }

  loadDocuments(): void {
    this.preTenderService.getDocuments(this.tender.id).subscribe({
      next: (documents) => {
        this.documents = documents;
        this.documentsLoaded = true;
      },
      error: () => (this.documentsLoaded = true),
    });
  }

  /** Opens the documents dialog of this tender (upload, download); the list here follows its changes. */
  openDocuments(): void {
    const data: TenderDocumentsDialogData = { tender: this.tender, title: 'Pre-Tender Documents' };
    this.dialog
      .open(TenderDocumentsDialogComponent, { data, width: '960px', maxWidth: '95vw' })
      .afterClosed()
      .subscribe((changed) => {
        if (changed) {
          this.loadDocuments();
        }
      });
  }

  formatSize(bytes: number): string {
    if (!bytes) {
      return '-';
    }
    return bytes < 1024 * 1024 ? `${Math.max(1, Math.round(bytes / 1024))} KB` : `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
