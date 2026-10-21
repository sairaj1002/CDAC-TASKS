import {
  ChangeDetectionStrategy,
  Component,
  computed,
  ElementRef,
  inject,
  OnDestroy,
  signal,
  ViewChild,
} from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

import {
  WorkImportPreview,
  WorkReportFilters,
  WorkReportSummary,
  WorkRequest,
  WorkResponse,
} from '../../models/work.model';
import { WorkService } from '../../services/work.service';
import { AuditHistoryDialogComponent } from './work-audit-dialog.component';
import { WorkReportDetailsDialogComponent } from './work-report-details-dialog.component';
import Chart from 'chart.js/auto';

type WorkReportChartType = 'bar' | 'line' | 'pie' | 'doughnut';

const dateOrderValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const startDate = control.get('stipulatedStartDate')?.value;
  const endDate = control.get('stipulatedEndDate')?.value;

  return startDate instanceof Date &&
    endDate instanceof Date &&
    endDate.getTime() < startDate.getTime()
    ? { dateOrder: true }
    : null;
};

@Component({
  selector: 'app-wms',
  standalone: false,
  templateUrl: './wms-dashboard.component.html',
  styleUrl: './wms-dashboard.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WmsDashboardComponent implements OnDestroy {
  private readonly fb = inject(FormBuilder);
  private readonly workService = inject(WorkService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  readonly works = signal<WorkResponse[]>([]);
  readonly loading = signal(false);
  readonly reportLoading = signal(false);
  readonly reportSummary = signal<WorkReportSummary | null>(null);
  readonly reportChartType = signal<WorkReportChartType>('bar');
  readonly showReportChartLegend = signal(true);
  readonly excelPreview = signal<WorkImportPreview | null>(null);
  readonly selectedExcelFile = signal<File | null>(null);
  readonly excelLoading = signal(false);
  readonly editingWorkId = signal<number | null>(null);
  readonly workSearch = signal('');
  readonly pageIndex = signal(0);
  readonly pageSize = signal(10);
  readonly displayedColumns = [
    'workRegNo',
    'workDetails',
    'contractorName',
    'actualStartDate',
    'stipulatedStartDate',
    'stipulatedEndDate',
    'createdAt',
    'updatedAt',
    'audit',
    'actions',
  ];
  readonly filteredWorks = computed(() => {
    const query = this.workSearch().trim().toLocaleLowerCase();
    if (!query) {
      return this.works();
    }
    return this.works().filter((work) =>
      [
        work.workRegNo,
        work.workDetails,
        work.contractorName,
        work.actualStartDate,
        work.stipulatedStartDate,
        work.stipulatedEndDate,
        work.createdAt,
        work.updatedAt,
      ].some((value) => String(value ?? '').toLocaleLowerCase().includes(query)),
    );
  });
  readonly visibleWorks = computed(() => {
    const start = this.pageIndex() * this.pageSize();
    return this.filteredWorks().slice(start, start + this.pageSize());
  });

  selectedTab = 0;

  @ViewChild('contractorChart')
  set contractorChartRef(element: ElementRef<HTMLCanvasElement> | undefined) {
    this.contractorChart = element;
    const summary = this.reportSummary();
    if (element && summary) {
      this.renderContractorChart(summary);
    }
  }
  private contractorChart?: ElementRef<HTMLCanvasElement>;
  private chart: Chart | null = null;

  readonly workForm = this.fb.nonNullable.group(
    {
      workDetails: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(500)]],
      contractorName: [
        '',
        [Validators.required, Validators.minLength(2), Validators.maxLength(150)],
      ],
      actualStartDate: this.fb.control<Date | null>(null),
      stipulatedStartDate: this.fb.control<Date | null>(null, Validators.required),
      stipulatedEndDate: this.fb.control<Date | null>(null, Validators.required),
    },
    { validators: dateOrderValidator },
  );

  readonly reportFilters = this.fb.group({
    fromDate: this.fb.control<Date | null>(null),
    toDate: this.fb.control<Date | null>(null),
    contractor: this.fb.nonNullable.control(''),
  });

  constructor() {
    this.loadWorks();
  }

  ngOnDestroy(): void {
    this.chart?.destroy();
  }

  onTabChange(index: number): void {
    this.selectedTab = index;
    if (index === 2) {
      this.refreshReport();
    }
  }

  isEditing(): boolean {
    return this.editingWorkId() !== null;
  }

  loadWorks(): void {
    this.loading.set(true);
    this.workService.getAll().subscribe({
      next: (works) => {
        this.works.set(works);
        this.pageIndex.set(
          Math.min(this.pageIndex(), Math.max(0, Math.ceil(works.length / this.pageSize()) - 1)),
        );
        this.loading.set(false);
      },
      error: (error: unknown) => {
        this.loading.set(false);
        this.showMessage(this.getErrorMessage(error), true);
      },
    });
  }

  submitWork(): void {
    if (this.workForm.invalid) {
      this.workForm.markAllAsTouched();
      this.showMessage('Please correct the highlighted validation errors.', true);
      return;
    }

    const value = this.workForm.getRawValue();
    const payload: WorkRequest = {
      workDetails: value.workDetails,
      contractorName: value.contractorName,
      actualStartDate: this.toApiDate(value.actualStartDate),
      stipulatedStartDate: this.toApiDate(value.stipulatedStartDate)!,
      stipulatedEndDate: this.toApiDate(value.stipulatedEndDate)!,
    };
    const workId = this.editingWorkId();

    this.loading.set(true);
    const request =
      workId === null ? this.workService.create(payload) : this.workService.update(workId, payload);

    request.subscribe({
      next: (savedWork) => {
        this.works.update((works) => {
          if (workId === null) {
            return [...works, savedWork];
          }
          return works.map((work) => (work.workRegNo === workId ? savedWork : work));
        });
        this.loading.set(false);
        this.resetForm();
        this.selectedTab = 1;
        this.showMessage(
          workId === null
            ? `Work ${savedWork.workRegNo} created successfully.`
            : `Work ${savedWork.workRegNo} updated successfully.`,
        );
        if (workId === null) {
          this.pageIndex.set(Math.floor((this.works().length - 1) / this.pageSize()));
        }
      },
      error: (error: unknown) => {
        this.loading.set(false);
        this.showMessage(this.getErrorMessage(error), true);
      },
    });
  }

  editWork(work: WorkResponse): void {
    this.editingWorkId.set(work.workRegNo);
    this.workForm.reset({
      workDetails: work.workDetails,
      actualStartDate: this.toDate(work.actualStartDate),
      contractorName: work.contractorName,
      stipulatedStartDate: this.toDate(work.stipulatedStartDate),
      stipulatedEndDate: this.toDate(work.stipulatedEndDate),
    });
    this.selectedTab = 0;
  }

  resetForm(): void {
    this.editingWorkId.set(null);
    this.workForm.reset({
      workDetails: '',
      contractorName: '',
      actualStartDate: null,
      stipulatedStartDate: null,
      stipulatedEndDate: null,
    });
  }

  deleteWork(work: WorkResponse): void {
    if (!window.confirm(`Delete work ${work.workRegNo}? This cannot be undone.`)) {
      return;
    }

    this.loading.set(true);
    this.workService.delete(work.workRegNo).subscribe({
      next: () => {
        this.works.update((works) => works.filter((item) => item.workRegNo !== work.workRegNo));
        this.pageIndex.set(
          Math.min(
            this.pageIndex(),
            Math.max(0, Math.ceil(this.works().length / this.pageSize()) - 1),
          ),
        );
        this.loading.set(false);
        this.showMessage(`Work ${work.workRegNo} deleted successfully.`);
      },
      error: (error: unknown) => {
        this.loading.set(false);
        this.showMessage(this.getErrorMessage(error), true);
      },
    });
  }

  openAuditHistory(work: WorkResponse): void {
    this.dialog.open(AuditHistoryDialogComponent, {
      width: 'min(1400px, 96vw)',
      maxWidth: '96vw',
      data: { work },
      ariaLabel: `Audit history for work ${work.workRegNo}`,
    });
  }

  private openContractorDetails(contractor: string): void {
    const filters = this.getReportFilters();
    if (!filters) {
      return;
    }
    this.dialog.open(WorkReportDetailsDialogComponent, {
      width: 'min(1100px, 96vw)',
      maxWidth: '96vw',
      data: { contractor, filters: { ...filters, contractor } },
      ariaLabel: `Work records for ${contractor}`,
    });
  }

  refreshReport(): void {
    const filters = this.getReportFilters();
    if (!filters) {
      return;
    }
    this.reportLoading.set(true);
    this.workService.getReportSummary(filters).subscribe({
      next: (summary) => {
        this.reportSummary.set(summary);
        this.reportLoading.set(false);
        this.renderContractorChart(summary);
      },
      error: (error: unknown) => {
        this.reportLoading.set(false);
        this.showMessage(this.getErrorMessage(error), true);
      },
    });
  }

  setReportChartType(chartType: WorkReportChartType): void {
    this.reportChartType.set(chartType);
    const summary = this.reportSummary();
    if (summary) {
      this.renderContractorChart(summary);
    }
  }

  setReportChartLegend(visible: boolean): void {
    this.showReportChartLegend.set(visible);
    const summary = this.reportSummary();
    if (summary) {
      this.renderContractorChart(summary);
    }
  }

  downloadReport(): void {
    const filters = this.getReportFilters();
    if (!filters) {
      return;
    }
    this.reportLoading.set(true);
    this.workService.downloadWorkRegister(filters).subscribe({
      next: (pdf) => {
        const url = URL.createObjectURL(pdf);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'work-register.pdf';
        link.click();
        URL.revokeObjectURL(url);
        this.reportLoading.set(false);
      },
      error: (error: unknown) => {
        this.reportLoading.set(false);
        this.showMessage(this.getErrorMessage(error), true);
      },
    });
  }

  downloadSampleExcel(): void {
    this.reportLoading.set(true);
    this.workService.downloadSampleExcel().subscribe({
      next: (workbook) => {
        const url = URL.createObjectURL(workbook);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'sample-work-register.xlsx';
        link.click();
        URL.revokeObjectURL(url);
        this.reportLoading.set(false);
      },
      error: (error: unknown) => {
        this.reportLoading.set(false);
        this.showMessage(this.getErrorMessage(error), true);
      },
    });
  }

  onExcelSelected(event: Event): void {
    const input = event.currentTarget;
    if (!(input instanceof HTMLInputElement)) {
      return;
    }
    const file = input.files?.[0] ?? null;
    this.excelPreview.set(null);
    this.selectedExcelFile.set(file);
    if (!file) {
      return;
    }
    this.excelLoading.set(true);
    this.workService.previewExcel(file).subscribe({
      next: (preview) => {
        this.excelPreview.set(preview);
        this.excelLoading.set(false);
      },
      error: (error: unknown) => {
        this.excelLoading.set(false);
        this.selectedExcelFile.set(null);
        input.value = '';
        this.showMessage(this.getErrorMessage(error), true);
      },
    });
  }

  importExcel(): void {
    const file = this.selectedExcelFile();
    const preview = this.excelPreview();
    if (!file || !preview || preview.validRows.length === 0) {
      return;
    }
    if (!window.confirm(`Import ${preview.validRows.length} valid work record(s)?`)) {
      return;
    }

    this.excelLoading.set(true);
    this.workService.importExcel(file).subscribe({
      next: (result) => {
        this.excelLoading.set(false);
        this.showMessage(
          `${result.importedCount} work record(s) imported` +
            (result.errors.length ? `; ${result.errors.length} row(s) skipped.` : '.'),
        );
        this.selectedExcelFile.set(null);
        this.excelPreview.set(null);
        this.loadWorks();
      },
      error: (error: unknown) => {
        this.excelLoading.set(false);
        this.showMessage(this.getErrorMessage(error), true);
      },
    });
  }

  onPageChange(event: { pageIndex: number; pageSize: number }): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
  }

  onWorkSearchChange(event: Event): void {
    const input = event.currentTarget;
    if (!(input instanceof HTMLInputElement)) {
      return;
    }
    this.workSearch.set(input.value);
    this.pageIndex.set(0);
  }

  private getReportFilters(): WorkReportFilters | null {
    const { fromDate, toDate, contractor } = this.reportFilters.getRawValue();
    if (fromDate && toDate && toDate.getTime() < fromDate.getTime()) {
      this.showMessage('To date must be on or after from date.', true);
      return null;
    }
    return {
      fromDate: this.toApiDate(fromDate),
      toDate: this.toApiDate(toDate),
      contractor,
    };
  }

  private renderContractorChart(summary: WorkReportSummary): void {
    const canvas = this.contractorChart?.nativeElement;
    if (!canvas) {
      return;
    }
    this.chart?.destroy();
    const chartType = this.reportChartType();
    const labels = summary.contractorLabels;
    const data = summary.contractorCounts;
    const legend = this.showReportChartLegend();
    const colors = ['#1976d2', '#43a047', '#fb8c00', '#8e24aa', '#e53935', '#00897b'];

    if (chartType === 'bar') {
      this.chart = new Chart(canvas, {
        type: 'bar',
        data: {
          labels,
          datasets: [{ label: 'Work records', data, backgroundColor: '#1976d2' }],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: { y: { beginAtZero: true, ticks: { precision: 0 } } },
          plugins: { legend: { display: legend } },
          onClick: (_event, elements) => {
            const contractor = labels[elements[0]?.index];
            if (contractor) {
              this.openContractorDetails(contractor);
            }
          },
        },
      });
      return;
    }

    if (chartType === 'line') {
      this.chart = new Chart(canvas, {
        type: 'line',
        data: {
          labels,
          datasets: [{
            label: 'Work records',
            data,
            borderColor: '#1976d2',
            backgroundColor: 'rgba(25, 118, 210, 0.18)',
            fill: true,
            tension: 0.3,
          }],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: { y: { beginAtZero: true, ticks: { precision: 0 } } },
          plugins: { legend: { display: legend } },
          onClick: (_event, elements) => {
            const contractor = labels[elements[0]?.index];
            if (contractor) {
              this.openContractorDetails(contractor);
            }
          },
        },
      });
      return;
    }

    if (chartType === 'pie') {
      this.chart = new Chart(canvas, {
        type: 'pie',
        data: {
          labels,
          datasets: [{ label: 'Work records', data, backgroundColor: colors }],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { display: legend, position: 'bottom' } },
          onClick: (_event, elements) => {
            const contractor = labels[elements[0]?.index];
            if (contractor) {
              this.openContractorDetails(contractor);
            }
          },
        },
      });
      return;
    }

    this.chart = new Chart(canvas, {
      type: 'doughnut',
      data: {
        labels,
        datasets: [{ label: 'Work records', data, backgroundColor: colors }],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: legend, position: 'bottom' } },
        onClick: (_event, elements) => {
          const contractor = labels[elements[0]?.index];
          if (contractor) {
            this.openContractorDetails(contractor);
          }
        },
      },
    });
  }

  private toDate(value: string | null): Date | null {
    if (!value) {
      return null;
    }
    const [year, month, day] = value.slice(0, 10).split('-').map(Number);
    if (!year || !month || !day) {
      return null;
    }
    return new Date(year, month - 1, day);
  }

  private toApiDate(value: Date | null): string | null {
    if (!value || Number.isNaN(value.getTime())) {
      return null;
    }
    const year = value.getFullYear();
    const month = String(value.getMonth() + 1).padStart(2, '0');
    const day = String(value.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private showMessage(message: string, isError = false): void {
    this.snackBar.open(message, 'Dismiss', {
      duration: isError ? 6000 : 3500,
      horizontalPosition: 'center',
      panelClass: isError ? ['toast-error'] : ['toast-success'],
      politeness: isError ? 'assertive' : 'polite',
      verticalPosition: 'top',
    });
  }

  private getErrorMessage(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'The operation failed. Please try again.';
    }
    const body: unknown = error.error;
    if (typeof body === 'string' && body.trim()) {
      return body;
    }
    if (
      typeof body === 'object' &&
      body !== null &&
      'data' in body &&
      typeof body.data === 'object' &&
      body.data !== null &&
      'validationErrors' in body.data &&
      typeof body.data.validationErrors === 'object' &&
      body.data.validationErrors !== null
    ) {
      const validationMessages = Object.values(body.data.validationErrors).filter(
        (message): message is string => typeof message === 'string',
      );
      if (validationMessages.length > 0) {
        return validationMessages.join(' ');
      }
    }
    if (typeof body === 'object' && body !== null && 'message' in body) {
      const message = body.message;
      if (typeof message === 'string' && message.trim()) {
        return message;
      }
    }

    if (error.status === 0) {
      return 'Unable to connect to the server. Check whether the Spring Boot application is running.';
    }
    if (error.status === 400) {
      return 'The server rejected the work data. Please review the entered values.';
    }
    if (error.status === 404) {
      return 'The requested work record was not found.';
    }
    if (error.status >= 500) {
      return 'The server could not complete the request. Please try again later.';
    }
    return `The request failed with status ${error.status}.`;
  }
}
