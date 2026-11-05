import { ChangeDetectionStrategy, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { APP_CONSTANTS } from '../../../common/constants/app.constants';
import { CorrigendumRecord } from '../../../common/models/app.models';
import { PostTenderService } from '../../../common/services/post-tender.service';
import { NotificationService } from '../../../common/services/notification.service';

@Component({
  selector: 'app-post-tender-corrigendum',
  standalone: false,
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './post-tender-corrigendum.html',
})
export class PostTenderCorrigendumComponent implements OnInit {
  displayedColumns = APP_CONSTANTS.TABLE_COLUMNS.CORRIGENDUM_LIST;
  dataSource = new MatTableDataSource<CorrigendumRecord>([]);
  loading = false;
  searchFilter = '';

  publishedDate: Date | null = new Date();
  maxDate: Date = new Date();

  // linked as soon as the paginator exists: the sample data arrives before the view is drawn,
  // so assigning it after loading left the table without paging
  @ViewChild(MatPaginator) set paginator(paginator: MatPaginator) {
    this.dataSource.paginator = paginator;
  }

  constructor(
    private readonly postTenderService: PostTenderService,
    private readonly notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadCorrigenda();
  }

  loadCorrigenda(): void {
    this.loading = true;
    const formattedDate = this.publishedDate ? this.formatDate(this.publishedDate) : undefined;

    this.postTenderService.getCorrigenda(formattedDate).subscribe({
      next: (records) => {
        this.dataSource.data = records;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.notificationService.showError('Failed to load corrigendum data: ' + (err.error?.message || err.message));
      },
    });
  }

  onPullFromGepnic(): void {
    if (!this.publishedDate) {
      this.notificationService.showWarning('Please select a publish date first.');
      return;
    }

    const formattedDate = this.formatDate(this.publishedDate);
    this.loading = true;

    this.postTenderService.pullCorrigendumFromGepnic(formattedDate).subscribe({
      next: (res) => {
        this.loading = false;
        if (PostTenderService.failed(res)) {
          this.notificationService.showError(`Corrigendum information could not be pulled from GePNIC: ${res.message}`);
          return;
        }
        this.notificationService.showSuccess(`Corrigendum information successfully pulled for date ${formattedDate}`);
        this.loadCorrigenda();
      },
      error: (err) => {
        this.loading = false;
        this.notificationService.showError('Corrigendum Pull error: ' + (err.error?.message || err.message));
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

  onDownloadDoc(record: CorrigendumRecord): void {
    this.notificationService.showInfo(`Downloading document ${record.docName || record.corrigendumRef}...`);
  }

  private formatDate(d: Date): string {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}

