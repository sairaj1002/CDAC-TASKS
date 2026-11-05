import { Component, EventEmitter, OnInit, Output, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { APP_CONSTANTS } from '../../../common/constants/app.constants';
import { PreTenderResponseForm } from '../../../common/models/app.models';
import { PreTenderService } from '../../../common/services/pretender.service';
import { GePNICService } from '../../../common/services/gepnic.service';
import { NotificationService } from '../../../common/services/notification.service';
import { ViewDetailsDialogComponent } from '../../shared/view-details-dialog/view-details-dialog';
import { AuditDetailsDialogComponent } from '../../shared/audit-details-dialog/audit-details-dialog';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog';
import { UploadBoqDialogComponent } from '../../shared/upload-boq-dialog/upload-boq-dialog';

@Component({
  selector: 'app-work-list',
  standalone: false,
  templateUrl: './work-list.html',
})
export class WorkListChildComponent implements OnInit {
  displayedColumns = APP_CONSTANTS.TABLE_COLUMNS.WORK_LIST;
  dataSource = new MatTableDataSource<PreTenderResponseForm>([]);
  loading = false;
  searchFilter = '';

  // Protected BOQ inline panel matching Screenshot 2
  selectedRowForBoq: PreTenderResponseForm | null = null;
  boqFile: File | null = null;
  boqFileName = 'No file chosen';
  boqUploading = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @Output() editPreTender = new EventEmitter<PreTenderResponseForm>();
  @Output() navigateToForm = new EventEmitter<void>();

  constructor(
    private preTenderService: PreTenderService,
    private gepnicService: GePNICService,
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
        if (tenders.length > 0 && !this.selectedRowForBoq) {
          this.selectedRowForBoq = tenders[0];
        }
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.notificationService.showError('Road data could not be loaded: ' + (err.error?.message || err.message));
      },
    });
  }

  onBoqFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.boqFile = input.files[0];
      this.boqFileName = this.boqFile.name;
    }
  }

  saveBoqFile(): void {
    if (!this.boqFile || !this.selectedRowForBoq) {
      this.notificationService.showError('Please select a Protected BOQ file (*.xls, *.xlsx) first.');
      return;
    }

    this.boqUploading = true;
    const formData = new FormData();
    formData.append('uniqueFileNo', this.selectedRowForBoq.uniqueFileNo || '3/9434/1');
    formData.append('docuniqueid', '2');
    formData.append('file', this.boqFile, this.boqFile.name);

    this.gepnicService.uploadDocument(formData).subscribe({
      next: () => {
        this.boqUploading = false;
        this.notificationService.showSuccess('Protected BOQ file uploaded successfully to GePNIC.');
        this.boqFile = null;
        this.boqFileName = 'No file chosen';
      },
      error: (err) => {
        this.boqUploading = false;
        this.notificationService.showError('Protected BOQ file upload failed: ' + (err.error?.message || err.message));
      },
    });
  }

  cancelBoqUpload(): void {
    this.boqFile = null;
    this.boqFileName = 'No file chosen';
  }

  applyFilter(): void {
    this.dataSource.filter = this.searchFilter.trim().toLowerCase();
  }

  onViewDetails(row: PreTenderResponseForm): void {
    this.dialog.open(ViewDetailsDialogComponent, {
      data: row,
      width: '900px',
      panelClass: 'component-details-panel',
    });
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

  onDownloadXml(row: PreTenderResponseForm): void {
    this.notificationService.showInfo(`Downloading canonical XML specification for ${row.tenderReferenceNo}...`);
  }

  onDownloadDoc(docType: string, row: PreTenderResponseForm): void {
    this.notificationService.showInfo(`Downloading ${docType} for work ${row.uniqueFileNo}...`);
  }

  onTransfer(row: PreTenderResponseForm): void {
    this.notificationService.showInfo(`Initiated work transfer protocol for ${row.uniqueFileNo}.`);
  }

  onSendBack(row: PreTenderResponseForm): void {
    this.notificationService.showInfo(`Work ${row.uniqueFileNo} sent back to drafting division.`);
  }
}

