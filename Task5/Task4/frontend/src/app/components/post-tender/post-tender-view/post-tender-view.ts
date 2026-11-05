import { ChangeDetectionStrategy, Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { APP_CONSTANTS } from '../../../common/constants/app.constants';
import { AocRecord, AocDocumentDetail } from '../../../common/models/app.models';
import { PostTenderService } from '../../../common/services/post-tender.service';
import { NotificationService } from '../../../common/services/notification.service';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog';
import { AuditDetailsDialogComponent } from '../../shared/audit-details-dialog/audit-details-dialog';
import { AocDetailsDialogComponent } from '../aoc-details-dialog/aoc-details-dialog';

@Component({
  selector: 'app-post-tender-view',
  standalone: false,
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './post-tender-view.html',
})
export class PostTenderViewComponent implements OnInit {
  displayedColumns = APP_CONSTANTS.TABLE_COLUMNS.POST_TENDER_AOC_LIST;
  dataSource = new MatTableDataSource<AocRecord>([]);
  loading = false;
  searchFilter = '';

  // Filter parameter from PPTX Slide 48: "Get AOC Info By Pre-tender created date"
  aocUploadDate: Date | null = new Date(2022, 11, 10); // Defaults to 10/12/2022 matching screenshot
  maxDate: Date = new Date(); // Created date must be lesser than current date

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private readonly postTenderService: PostTenderService,
    private readonly notificationService: NotificationService,
    private readonly dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadAocData();
  }

  loadAocData(): void {
    this.loading = true;
    const formattedDate = this.aocUploadDate ? this.formatDate(this.aocUploadDate) : undefined;

    this.postTenderService.getAocRecords(formattedDate).subscribe({
      next: (records) => {
        this.dataSource.data = records;
        this.dataSource.paginator = this.paginator;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.notificationService.showError('Failed to load AOC data: ' + (err.error?.message || err.message));
      },
    });
  }

  onGetAocFromGepnic(): void {
    if (!this.aocUploadDate) {
      this.notificationService.showWarning('Please select an AOC Upload Date first.');
      return;
    }

    const formattedDate = this.formatDate(this.aocUploadDate);
    this.loading = true;

    this.postTenderService.pullAocFromGepnic(formattedDate).subscribe({
      next: (res) => {
        this.loading = false;
        this.notificationService.showSuccess(`AOC information retrieved from GePNIC for ${formattedDate}`);
        this.loadAocData();
      },
      error: (err) => {
        this.loading = false;
        this.notificationService.showError('GePNIC Pull error: ' + (err.error?.message || err.message));
      },
    });
  }

  applyFilter(): void {
    this.dataSource.filter = this.searchFilter.trim().toLowerCase();
  }

  resetFilter(): void {
    this.searchFilter = '';
    this.applyFilter();
  }

  // Codes such as 3/9211/1 or WAMIS/PWD/2026/001 may wrap after a slash (zero-width space)
  breakAtSlashes(code: string | null | undefined): string {
    return (code ?? '').replace(/\//g, '/' + String.fromCharCode(0x200b));
  }

  onViewAocDetails(record: AocRecord): void {
    this.dialog.open(AocDetailsDialogComponent, {
      data: record,
      width: '950px',
      panelClass: 'component-details-panel',
    });
  }
  onInsertInAoc(record: AocRecord): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: APP_CONSTANTS.CONFIRMATION.AOC_INSERT,
      width: '420px',
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.loading = true;
        this.postTenderService.insertInAoc(record.id).subscribe({
          next: (res) => {
            this.loading = false;
            this.notificationService.showSuccess(res.message);
            this.loadAocData();
          },
          error: (err) => {
            this.loading = false;
            this.notificationService.showError('Insert in AOC failed: ' + (err.error?.message || err.message));
          },
        });
      }
    });
  }

  onDownloadDoc(doc: AocDocumentDetail, record: AocRecord): void {
    this.postTenderService
      .downloadAocDocument({
        docType: doc.docType,
        bId: doc.bId,
        docCode: doc.docCode,
        fileName: doc.fileName,
      })
      .subscribe({
        next: () => {
          this.notificationService.showSuccess(`Downloading ${doc.docType}: ${doc.fileName}`);
        },
      });
  }

  onAuditDetails(record: AocRecord): void {
    this.dialog.open(AuditDetailsDialogComponent, {
      data: {
        preTenderId: record.id,
        transactionId: record.id,
        workNumber: record.uniqueFileNo,
      },
      width: '850px',
      panelClass: 'audit-details-dialog',
    });
  }

  private formatDate(d: Date): string {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}

