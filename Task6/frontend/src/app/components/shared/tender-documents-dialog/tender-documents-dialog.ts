import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { concatMap, from } from 'rxjs';
import { APP_CONSTANTS } from '../../../common/constants/app.constants';
import { DropdownMasterResponseForm, PreTenderDocumentResponse, PreTenderResponseForm } from '../../../common/models/app.models';
import { DropdownService } from '../../../common/services/dropdown.service';
import { NotificationService } from '../../../common/services/notification.service';
import { PreTenderService } from '../../../common/services/pretender.service';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog';

export interface TenderDocumentsDialogData {
  tender: PreTenderResponseForm;
  title: string;
  // category chosen for the next upload (DOC_CODE code); the first category when empty
  uploadCategory?: string;
  // documents shown at first: one category, or all when empty
  showCategory?: string;
}

/**
 * Documents of a saved tender (View tab: Upload Protected BOQ, Download Pre-Tender documents, Download BOQ):
 * upload files under a category, see the stored documents, download or remove them.
 */
@Component({
  selector: 'app-tender-documents-dialog',
  changeDetection: ChangeDetectionStrategy.Eager,
  standalone: false,
  templateUrl: './tender-documents-dialog.html',
})
export class TenderDocumentsDialogComponent implements OnInit {
  readonly allCategories = '';

  categories: DropdownMasterResponseForm[] = [];
  documents: PreTenderDocumentResponse[] = [];
  uploadCategory = '';
  showCategory: string;
  uploading = false;
  // set when a document was added or removed, so the list behind the dialog is reloaded
  private changed = false;

  constructor(
    public dialogRef: MatDialogRef<TenderDocumentsDialogComponent, boolean>,
    @Inject(MAT_DIALOG_DATA) public data: TenderDocumentsDialogData,
    private preTenderService: PreTenderService,
    private dropdownService: DropdownService,
    private notificationService: NotificationService,
    private dialog: MatDialog
  ) {
    this.uploadCategory = data.uploadCategory ?? '';
    this.showCategory = data.showCategory ?? this.allCategories;
  }

  ngOnInit(): void {
    this.dropdownService.getByCategory(APP_CONSTANTS.DROPDOWN_CATEGORIES.DOC_CODE).subscribe((categories) => {
      this.categories = categories;
      if (!this.uploadCategory) {
        this.uploadCategory = categories[0]?.code ?? '';
      }
    });
    this.loadDocuments();
  }

  get shownDocuments(): PreTenderDocumentResponse[] {
    return this.showCategory ? this.documents.filter((d) => d.docCode === this.showCategory) : this.documents;
  }

  categoryLabel(code: string): string {
    return this.categories.find((c) => c.code === code)?.displayName ?? code;
  }

  loadDocuments(): void {
    this.preTenderService.getDocuments(this.data.tender.id).subscribe({
      next: (documents) => (this.documents = documents),
      error: (err) => this.notificationService.showError('Documents could not be loaded: ' + (err.error?.message || err.message)),
    });
  }

  /** Uploads the picked files one after the other under the chosen category. */
  onFilesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const files = Array.from(input.files ?? []);
    input.value = ''; // the same file can be picked again
    if (!files.length || !this.uploadCategory) {
      return;
    }
    this.uploading = true;
    let done = 0;
    from(files)
      .pipe(concatMap((file) => this.preTenderService.uploadDocument(this.data.tender.id, this.uploadCategory, file)))
      .subscribe({
        next: () => done++,
        error: (err) => {
          this.uploading = false;
          this.changed ||= done > 0;
          this.notificationService.showError('Upload failed: ' + (err.error?.message || err.message));
          this.loadDocuments();
        },
        complete: () => {
          this.uploading = false;
          this.changed = true;
          this.notificationService.showSuccess(`${done} document(s) uploaded as ${this.categoryLabel(this.uploadCategory)}.`);
          // show what was just uploaded, unless every document is shown anyway
          if (this.showCategory) {
            this.showCategory = this.uploadCategory;
          }
          this.loadDocuments();
        },
      });
  }

  onDownload(doc: PreTenderDocumentResponse): void {
    this.preTenderService.downloadDocument(this.data.tender.id, doc.id).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = doc.docName;
        link.click();
        URL.revokeObjectURL(url);
      },
      error: (err) => this.notificationService.showError('Download failed: ' + (err.error?.message || err.message)),
    });
  }

  onDelete(doc: PreTenderDocumentResponse): void {
    this.dialog
      .open(ConfirmDialogComponent, {
        data: { ...APP_CONSTANTS.CONFIRMATION.DELETE, message: `Remove "${doc.docName}" from this tender?` },
        width: '400px',
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed) {
          return;
        }
        this.preTenderService.deleteDocument(this.data.tender.id, doc.id).subscribe({
          next: () => {
            this.changed = true;
            this.notificationService.showSuccess(`"${doc.docName}" removed.`);
            this.loadDocuments();
          },
          error: (err) => this.notificationService.showError('Remove failed: ' + (err.error?.message || err.message)),
        });
      });
  }

  formatSize(bytes: number): string {
    if (!bytes) {
      return '-';
    }
    return bytes < 1024 * 1024 ? `${Math.max(1, Math.round(bytes / 1024))} KB` : `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }

  onClose(): void {
    this.dialogRef.close(this.changed);
  }
}
