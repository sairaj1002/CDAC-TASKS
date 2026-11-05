import { ChangeDetectionStrategy, Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { APP_CONSTANTS } from '../../../common/constants/app.constants';
import { NegotiationRecord } from '../../../common/models/app.models';
import { PostTenderService } from '../../../common/services/post-tender.service';
import { NotificationService } from '../../../common/services/notification.service';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog';

@Component({
  selector: 'app-post-tender-negotiation',
  standalone: false,
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './post-tender-negotiation.html',
})
export class PostTenderNegotiationComponent implements OnInit {
  displayedColumns = APP_CONSTANTS.TABLE_COLUMNS.NEGOTIATION_LIST;
  dataSource = new MatTableDataSource<NegotiationRecord>([]);
  loading = false;
  searchFilter = '';

  // linked as soon as the paginator exists: the sample data arrives before the view is drawn,
  // so assigning it after loading left the table without paging
  @ViewChild(MatPaginator) set paginator(paginator: MatPaginator) {
    this.dataSource.paginator = paginator;
  }

  constructor(
    private readonly postTenderService: PostTenderService,
    private readonly notificationService: NotificationService,
    private readonly dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadNegotiations();
  }

  loadNegotiations(): void {
    this.loading = true;
    this.postTenderService.getNegotiations().subscribe({
      next: (records) => {
        this.dataSource.data = records;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.notificationService.showError('Failed to load negotiations: ' + (err.error?.message || err.message));
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

  onApprove(record: NegotiationRecord): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: APP_CONSTANTS.CONFIRMATION.NEGOTIATION_APPROVE,
      width: '420px',
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.postTenderService.approveNegotiation(record.id).subscribe({
          next: (res) => {
            this.notificationService.showSuccess(res.message);
            this.loadNegotiations();
          },
        });
      }
    });
  }

  onDownloadNegoDoc(record: NegotiationRecord): void {
    this.postTenderService
      .downloadAocDocument({
        docType: 'NEGO',
        bId: String(record.id),
        docCode: record.negoDocCode || 'NEGO',
        fileName: record.negoDocName || 'Negotiation_Minutes.pdf',
      })
      .subscribe({
        next: () => {
          this.notificationService.showSuccess(`Downloading NEGO document: ${record.negoDocName}`);
        },
      });
  }
}

