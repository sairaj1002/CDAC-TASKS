import { ChangeDetectionStrategy, Component, EventEmitter, OnInit, Output, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { APP_CONSTANTS } from '../../../common/constants/app.constants';
import { PreTenderResponseForm } from '../../../common/models/app.models';
import { PreTenderService } from '../../../common/services/pretender.service';
import { NotificationService } from '../../../common/services/notification.service';
import { ViewDetailsDialogComponent } from '../../shared/view-details-dialog/view-details-dialog';
import { AuditDetailsDialogComponent } from '../../shared/audit-details-dialog/audit-details-dialog';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog';
import { UploadBoqDialogComponent } from '../../shared/upload-boq-dialog/upload-boq-dialog';
import { JsonViewDialogComponent, JsonViewDialogData } from '../../shared/json-view-dialog/json-view-dialog';
import {
  TenderDocumentsDialogComponent,
  TenderDocumentsDialogData,
} from '../../shared/tender-documents-dialog/tender-documents-dialog';

@Component({
  selector: 'app-work-list',
  changeDetection: ChangeDetectionStrategy.Eager,
  standalone: false,
  templateUrl: './work-list.html',
})
export class WorkListChildComponent implements OnInit {
  displayedColumns = APP_CONSTANTS.TABLE_COLUMNS.WORK_LIST;
  dataSource = new MatTableDataSource<PreTenderResponseForm>([]);
  loading = false;
  searchFilter = '';

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @Output() editPreTender = new EventEmitter<PreTenderResponseForm>();

  constructor(
    private preTenderService: PreTenderService,
    private notificationService: NotificationService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadWorks();
  }

  loadWorks(): void {
    this.loading = true;
    this.preTenderService.getAll().subscribe({
      next: (tenders) => {
        this.dataSource.data = tenders;
        this.dataSource.paginator = this.paginator;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.notificationService.showError('Road data could not be loaded: ' + (err.error?.message || err.message));
      },
    });
  }

  // Codes such as WAMIS/PWD/2026/001 may wrap after a slash (zero-width space), never inside a part
  breakAtSlashes(code: string | null | undefined): string {
    return (code ?? '').replace(/\//g, '/' + String.fromCharCode(0x200b));
  }

  applyFilter(): void {
    this.dataSource.filter = this.searchFilter.trim().toLowerCase();
  }

  onViewDetails(row: PreTenderResponseForm): void {
    this.dialog
      .open(ViewDetailsDialogComponent, { data: row, width: '1000px', maxWidth: '95vw' })
      .afterClosed()
      // documents may have been added or removed from the details screen
      .subscribe(() => this.loadWorks());
  }

  onUploadBoq(row: PreTenderResponseForm): void {
    const dialogRef = this.dialog.open(UploadBoqDialogComponent, {
      data: {
        workNumber: row.uniqueFileNo || '3/9434/1',
        uniqueFileNo: row.uniqueFileNo,
        tenderReferenceNumber: row.tenderReferenceNo,
      },
      width: '600px',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadWorks();
      }
    });
  }

  onAuditDetails(row: PreTenderResponseForm): void {
    this.dialog.open(AuditDetailsDialogComponent, {
      data: {
        preTenderId: row.id,
        transactionId: row.id,
        workNumber: row.uniqueFileNo,
      },
      width: '850px',
      panelClass: 'audit-details-dialog',
    });
  }

  onSendToGepnic(row: PreTenderResponseForm): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: APP_CONSTANTS.CONFIRMATION.GEPNIC_PUSH,
      width: '400px',
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.loading = true;
        this.preTenderService.submitToGepnic(row.id).subscribe({
          next: (res) => {
            this.loading = false;
            this.notificationService.showSuccess(`Pre-Tender dispatched to GePNIC! Status: ${res.status}`);
            this.loadWorks();
          },
          error: (err) => {
            this.loading = false;
            this.notificationService.showError('GePNIC Submission Error: ' + (err.error?.message || err.message));
          },
        });
      }
    });
  }

  onEdit(row: PreTenderResponseForm): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: APP_CONSTANTS.CONFIRMATION.EDIT,
      width: '400px',
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.editPreTender.emit(row);
      }
    });
  }

  onDelete(row: PreTenderResponseForm): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: APP_CONSTANTS.CONFIRMATION.DELETE,
      width: '400px',
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.loading = true;
        this.preTenderService.delete(row.id).subscribe({
          next: () => {
            this.loading = false;
            this.notificationService.showSuccess('Work record deleted successfully.');
            this.loadWorks();
          },
          error: (err) => {
            this.loading = false;
            this.notificationService.showError('Failed to delete work: ' + (err.error?.message || err.message));
          },
        });
      }
    });
  }

  onFinalize(row: PreTenderResponseForm): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: APP_CONSTANTS.CONFIRMATION.FINALIZE,
      width: '400px',
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.notificationService.showSuccess('Pre-Tender finalized successfully.');
      }
    });
  }

  /** Opens the tender JSON exactly as it would be sent to GePNIC (built by the backend; nothing is sent). */
  onViewJson(row: PreTenderResponseForm): void {
    this.preTenderService.getGePNICJson(row.id).subscribe({
      next: (json) => {
        const data: JsonViewDialogData = {
          title: 'GePNIC Tender JSON',
          subtitle: `${row.tenderReferenceNo} · ${row.uniqueFileNo}`,
          fileName: `gepnic-tender-${(row.tenderReferenceNo || String(row.id)).replace(/[^A-Za-z0-9_-]+/g, '_')}.json`,
          json,
        };
        this.dialog.open(JsonViewDialogComponent, { data, width: '900px', maxWidth: '95vw' });
      },
      error: (err) => {
        this.notificationService.showError('Could not build the GePNIC JSON: ' + (err.error?.message || err.message));
      },
    });
  }

  /** The documents dialog of a tender: upload (protected BOQ, NIT, ...), see and download its documents. */
  openDocuments(row: PreTenderResponseForm): void {
    const data: TenderDocumentsDialogData = { tender: row, title: 'Pre-Tender Documents' };
    this.dialog
      .open(TenderDocumentsDialogComponent, { data, width: '960px', maxWidth: '95vw' })
      .afterClosed()
      .subscribe((changed) => {
        // the document count in the list changes after an upload or a removal
        if (changed) {
          this.loadWorks();
        }
      });
  }

  onTransfer(row: PreTenderResponseForm): void {
    this.notificationService.showInfo(`Initiated work transfer protocol for ${row.uniqueFileNo}.`);
  }

  onSendBack(row: PreTenderResponseForm): void {
    this.notificationService.showInfo(`Work ${row.uniqueFileNo} sent back to drafting division.`);
  }
}

