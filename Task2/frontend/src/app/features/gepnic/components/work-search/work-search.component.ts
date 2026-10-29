import { Component, EventEmitter, Input, OnInit, Output, HostListener, ChangeDetectorRef } from '@angular/core';
import { PretenderApiService } from '../../services/pretender-api.service';
import { WorkSummary } from '../../models/work-summary.model';
import { SnackbarService } from '../../../../shared/services/snackbar.service';
import { ConfirmDialogService } from '../../../../core/services/confirm-dialog.service';

export interface SubWorkSearchFilter {
  workRegistrationNumber: string;
  workName: string;
  subWorkRegistrationNumber: string;
  subWorkName: string;
  workCategory: string;
  stipulatedStartDate: string;
  stipulatedEndDate: string;
  startDate: string;
  endDate: string;
  contractorName: string;
  agreementAmount: number | null;
  gstAgreementAmount: number | null;
  asset: string;
  status: string;
  workStatus: string;
  isDataPorted: boolean;
}

export interface SubWorkItem extends WorkSummary {
  workRegistrationNumber: string;
  workDescription: string;
  workType: 'MINOR' | 'MAJOR';
  workStatus: string;
  workGisCode: string;
  budgetYear: string;
  status: string;

  workName: string;
  subWorkRegistrationNumber: string;
  subWorkName: string;
  workCategory: string;
  stipulatedStartDate: string;
  stipulatedEndDate: string;
  startDate: string;
  endDate: string;
  contractorName: string;
  agreementAmount: number;
  gstAgreementAmount: number;
  asset: string;
  isDataPorted: boolean;
}

const DEFAULT_FILTERS = (): SubWorkSearchFilter => ({
  workRegistrationNumber: '',
  workName: '',
  subWorkRegistrationNumber: '',
  subWorkName: '',
  workCategory: '',
  stipulatedStartDate: '',
  stipulatedEndDate: '',
  startDate: '',
  endDate: '',
  contractorName: '',
  agreementAmount: null,
  gstAgreementAmount: null,
  asset: '',
  status: '',
  workStatus: '',
  isDataPorted: false,
});

@Component({
  selector: 'app-work-search',
  standalone: false,
  templateUrl: './work-search.component.html',
  styleUrls: ['./work-search.component.css']
})
export class WorkSearchComponent implements OnInit {
  @Input() selectedWorkId: string | null = null;
  @Input() selectedWorkNumber: string | null = null;
  @Input() selectedWorkDescription: string | null = null;

  @Output() workSelected = new EventEmitter<WorkSummary>();
  @Output() workCleared = new EventEmitter<void>();

  isExpanded = false;
  isLoading = false;
  hasSearched = false;
  errorMessage = '';

  // Dialog and pagination state
  isDialogOpen = false;
  currentPage = 1;
  pageSize = 10;

  filters: SubWorkSearchFilter = DEFAULT_FILTERS();

  readonly workCategoryOptions: string[] = [
    'Roads & Bridges',
    'Building Construction',
    'Irrigation & Water Resources',
    'Electrical & Mechanical',
    'Public Health & Sanitation',
    'Urban Infrastructure',
    'Highway Strengthening',
    'Periodic Maintenance',
  ];

  readonly assetOptions: string[] = [
    'State Highway 15 (SH-15)',
    'National Highway 44 (NH-44)',
    'Major District Road 21 (MDR-21)',
    'Village Road 08 (VR-08)',
    'Expressway Corridor 02',
    'Urban Ring Road Section B',
    'PWD Civil Division Complex',
    'District Hospital Access Road',
  ];

  readonly statusOptions: string[] = [
    'APPROVED',
    'REGISTERED',
    'SANCTIONED',
    'DRAFT',
    'ACTIVE',
  ];

  readonly workStatusOptions: string[] = [
    'ONGOING',
    'NEW',
    'COMPLETED',
    'NOT STARTED',
  ];

  // Comprehensive mock works catalog matching Image 2 with exact registration numbers, descriptions and statuses
  private readonly mockCatalog: SubWorkItem[] = [
    {
      id: '2026080005',
      workNumber: '2026080005',
      description: 'test 05082023',
      source: 'WAMIS CORE REGISTRY',
      mock: true,
      workRegistrationNumber: '2026080005',
      workDescription: 'test 05082023',
      workType: 'MINOR',
      workStatus: 'ONGOING',
      workGisCode: '',
      budgetYear: '2026-2027',
      status: 'APPROVED',
      workName: 'test 05082023',
      subWorkRegistrationNumber: 'SWRN-2026-0005',
      subWorkName: 'Excavation and subgrade testing',
      workCategory: 'Roads & Bridges',
      stipulatedStartDate: '2024-04-01',
      stipulatedEndDate: '2024-10-31',
      startDate: '2024-04-15',
      endDate: '2024-10-25',
      contractorName: 'Shree Infrastructure Pvt Ltd',
      agreementAmount: 18500000,
      gstAgreementAmount: 21830000,
      asset: 'State Highway 15 (SH-15)',
      isDataPorted: false,
    },
    {
      id: '2026060183',
      workNumber: '2026060183',
      description: 'Excavation for foundation upto 1.5 m depth including sorting out and stacking of useful materials an...',
      source: 'WAMIS CORE REGISTRY',
      mock: true,
      workRegistrationNumber: '2026060183',
      workDescription: 'Excavation for foundation upto 1.5 m depth including sorting out and stacking of useful materials an...',
      workType: 'MINOR',
      workStatus: 'ONGOING',
      workGisCode: '',
      budgetYear: '2026-2027',
      status: 'APPROVED',
      workName: 'Foundation Excavation & Stacking',
      subWorkRegistrationNumber: 'SWRN-2026-0183',
      subWorkName: 'Excavation for foundation upto 1.5 m depth',
      workCategory: 'Building Construction',
      stipulatedStartDate: '2024-03-01',
      stipulatedEndDate: '2024-09-30',
      startDate: '2024-03-10',
      endDate: '2024-09-20',
      contractorName: 'Apex Buildcon Engineers',
      agreementAmount: 9400000,
      gstAgreementAmount: 11092000,
      asset: 'PWD Civil Division Complex',
      isDataPorted: true,
    },
    {
      id: '2026060178',
      workNumber: '2026060178',
      description: 'Test work for uploading boq',
      source: 'WAMIS CORE REGISTRY',
      mock: true,
      workRegistrationNumber: '2026060178',
      workDescription: 'Test work for uploading boq',
      workType: 'MINOR',
      workStatus: 'ONGOING',
      workGisCode: '',
      budgetYear: '2026-2027',
      status: 'APPROVED',
      workName: 'Test work for uploading boq',
      subWorkRegistrationNumber: 'SWRN-2026-0178',
      subWorkName: 'BOQ Preparation & Validation',
      workCategory: 'Urban Infrastructure',
      stipulatedStartDate: '2024-05-01',
      stipulatedEndDate: '2024-11-30',
      startDate: '2024-05-15',
      endDate: '2024-11-15',
      contractorName: 'Bharat Civil Tech Projects',
      agreementAmount: 6200000,
      gstAgreementAmount: 7316000,
      asset: 'Urban Ring Road Section B',
      isDataPorted: false,
    },
    {
      id: '2026060171',
      workNumber: '2026060171',
      description: 'construction of road',
      source: 'WAMIS CORE REGISTRY',
      mock: true,
      workRegistrationNumber: '2026060171',
      workDescription: 'construction of road',
      workType: 'MINOR',
      workStatus: 'NEW',
      workGisCode: '',
      budgetYear: '2026-2027',
      status: 'REGISTERED',
      workName: 'construction of road',
      subWorkRegistrationNumber: 'SWRN-2026-0171',
      subWorkName: 'Rural road surfacing package',
      workCategory: 'Roads & Bridges',
      stipulatedStartDate: '2024-02-01',
      stipulatedEndDate: '2024-08-31',
      startDate: '2024-02-15',
      endDate: '2024-08-20',
      contractorName: 'Kisan Roadways & Infra',
      agreementAmount: 4800000,
      gstAgreementAmount: 5664000,
      asset: 'Village Road 08 (VR-08)',
      isDataPorted: false,
    },
    {
      id: '2026060109',
      workNumber: '2026060109',
      description: 'TEST WORK HIMMATNAGAR 09062026',
      source: 'WAMIS CORE REGISTRY',
      mock: true,
      workRegistrationNumber: '2026060109',
      workDescription: 'TEST WORK HIMMATNAGAR 09062026',
      workType: 'MAJOR',
      workStatus: 'ONGOING',
      workGisCode: '',
      budgetYear: '2024-2025',
      status: 'APPROVED',
      workName: 'TEST WORK HIMMATNAGAR 09062026',
      subWorkRegistrationNumber: 'SWRN-2026-0109',
      subWorkName: 'Himmatnagar 4-lane bypass section',
      workCategory: 'Highway Strengthening',
      stipulatedStartDate: '2024-01-15',
      stipulatedEndDate: '2025-01-14',
      startDate: '2024-02-01',
      endDate: '2025-01-10',
      contractorName: 'Larsen & Infra Consortium',
      agreementAmount: 42500000,
      gstAgreementAmount: 50150000,
      asset: 'National Highway 44 (NH-44)',
      isDataPorted: false,
    },
    {
      id: '2026020004',
      workNumber: '2026020004',
      description: 'TEST WORK',
      source: 'WAMIS CORE REGISTRY',
      mock: true,
      workRegistrationNumber: '2026020004',
      workDescription: 'TEST WORK',
      workType: 'MAJOR',
      workStatus: 'NEW',
      workGisCode: '',
      budgetYear: '2025-2026',
      status: 'APPROVED',
      workName: 'TEST WORK',
      subWorkRegistrationNumber: 'SWRN-2026-0004',
      subWorkName: 'Civil structural framing package',
      workCategory: 'Building Construction',
      stipulatedStartDate: '2024-06-01',
      stipulatedEndDate: '2024-12-31',
      startDate: '2024-06-15',
      endDate: '2024-12-15',
      contractorName: 'Modern Buildcon Associates',
      agreementAmount: 11200000,
      gstAgreementAmount: 13216000,
      asset: 'PWD Civil Division Complex',
      isDataPorted: false,
    },
    {
      id: '2026010106',
      workNumber: '2026010106',
      description: 'TEST DEPO',
      source: 'WAMIS CORE REGISTRY',
      mock: true,
      workRegistrationNumber: '2026010106',
      workDescription: 'TEST DEPO',
      workType: 'MAJOR',
      workStatus: 'NEW',
      workGisCode: '',
      budgetYear: '2025-2026',
      status: 'APPROVED',
      workName: 'TEST DEPO',
      subWorkRegistrationNumber: 'SWRN-2026-0106',
      subWorkName: 'Depot Yard Electrification & Lighting',
      workCategory: 'Electrical & Mechanical',
      stipulatedStartDate: '2024-05-15',
      stipulatedEndDate: '2024-09-15',
      startDate: '2024-06-01',
      endDate: '2024-09-10',
      contractorName: 'Vidyut Power Solutions Ltd',
      agreementAmount: 5300000,
      gstAgreementAmount: 6254000,
      asset: 'Expressway Corridor 02',
      isDataPorted: false,
    },
    {
      id: '2026010050',
      workNumber: '2026010050',
      description: 'Work for widening of road with multiple sub works',
      source: 'WAMIS CORE REGISTRY',
      mock: true,
      workRegistrationNumber: '2026010050',
      workDescription: 'Work for widening of road with multiple sub works',
      workType: 'MINOR',
      workStatus: 'ONGOING',
      workGisCode: '',
      budgetYear: '2025-2026',
      status: 'APPROVED',
      workName: 'Work for widening of road with multiple sub works',
      subWorkRegistrationNumber: 'SWRN-2026-0050',
      subWorkName: 'Shoulder widening and asphalt overlay',
      workCategory: 'Roads & Bridges',
      stipulatedStartDate: '2024-07-01',
      stipulatedEndDate: '2024-11-30',
      startDate: '2024-07-10',
      endDate: '2024-11-20',
      contractorName: 'Shree Infrastructure Pvt Ltd',
      agreementAmount: 7600000,
      gstAgreementAmount: 8968000,
      asset: 'State Highway 15 (SH-15)',
      isDataPorted: true,
    },
    {
      id: '2026010044',
      workNumber: '2026010044',
      description: 'Test work for adding subwork automatically',
      source: 'WAMIS CORE REGISTRY',
      mock: true,
      workRegistrationNumber: '2026010044',
      workDescription: 'Test work for adding subwork automatically',
      workType: 'MINOR',
      workStatus: 'ONGOING',
      workGisCode: '',
      budgetYear: '2025-2026',
      status: 'APPROVED',
      workName: 'Test work for adding subwork automatically',
      subWorkRegistrationNumber: 'SWRN-2026-0044',
      subWorkName: 'Automated road marking & signage',
      workCategory: 'Periodic Maintenance',
      stipulatedStartDate: '2024-04-10',
      stipulatedEndDate: '2024-08-30',
      startDate: '2024-04-20',
      endDate: '2024-08-15',
      contractorName: 'Apex Buildcon Engineers',
      agreementAmount: 3800000,
      gstAgreementAmount: 4484000,
      asset: 'Major District Road 21 (MDR-21)',
      isDataPorted: false,
    },
    {
      id: '2026010036',
      workNumber: '2026010036',
      description: 'Construction of Rural Hospital building',
      source: 'WAMIS CORE REGISTRY',
      mock: true,
      workRegistrationNumber: '2026010036',
      workDescription: 'Construction of Rural Hospital building',
      workType: 'MINOR',
      workStatus: 'ONGOING',
      workGisCode: '',
      budgetYear: '2025-2026',
      status: 'APPROVED',
      workName: 'Construction of Rural Hospital building',
      subWorkRegistrationNumber: 'SWRN-2026-0036',
      subWorkName: 'Hospital building superstructure',
      workCategory: 'Building Construction',
      stipulatedStartDate: '2024-01-10',
      stipulatedEndDate: '2025-06-30',
      startDate: '2024-01-20',
      endDate: '2025-06-15',
      contractorName: 'Modern Buildcon Associates',
      agreementAmount: 16500000,
      gstAgreementAmount: 19470000,
      asset: 'Civil Hospital Complex',
      isDataPorted: true,
    },
    {
      id: '2026010035',
      workNumber: '2026010035',
      description: 'Periodic Renewal Plan for District Road Network',
      source: 'WAMIS CORE REGISTRY',
      mock: true,
      workRegistrationNumber: '2026010035',
      workDescription: 'Periodic Renewal Plan for District Road Network',
      workType: 'MINOR',
      workStatus: 'ONGOING',
      workGisCode: '',
      budgetYear: '2025-2026',
      status: 'APPROVED',
      workName: 'Periodic Renewal Plan for District Road Network',
      subWorkRegistrationNumber: 'SWRN-2026-0035',
      subWorkName: 'Bituminous Macadam Resurfacing',
      workCategory: 'Periodic Maintenance',
      stipulatedStartDate: '2024-03-15',
      stipulatedEndDate: '2024-09-15',
      startDate: '2024-04-01',
      endDate: '2024-09-01',
      contractorName: 'Shree Infrastructure Pvt Ltd',
      agreementAmount: 8400000,
      gstAgreementAmount: 9912000,
      asset: 'State Highway 15 (SH-15)',
      isDataPorted: false,
    },
    {
      id: '2026010028',
      workNumber: '2026010028',
      description: 'Drainage improvement and box culvert raising on MDR-21',
      source: 'WAMIS CORE REGISTRY',
      mock: true,
      workRegistrationNumber: '2026010028',
      workDescription: 'Drainage improvement and box culvert raising on MDR-21',
      workType: 'MINOR',
      workStatus: 'ONGOING',
      workGisCode: '',
      budgetYear: '2025-2026',
      status: 'APPROVED',
      workName: 'Drainage improvement and box culvert raising on MDR-21',
      subWorkRegistrationNumber: 'SWRN-2026-0028',
      subWorkName: 'Box Culvert Raising Section 2',
      workCategory: 'Roads & Bridges',
      stipulatedStartDate: '2024-02-15',
      stipulatedEndDate: '2024-07-31',
      startDate: '2024-03-01',
      endDate: '2024-07-20',
      contractorName: 'Apex Buildcon Engineers',
      agreementAmount: 5100000,
      gstAgreementAmount: 6018000,
      asset: 'Major District Road 21 (MDR-21)',
      isDataPorted: true,
    },
  ];

  searchResults: SubWorkItem[] = [];

  constructor(
    private readonly api: PretenderApiService,
    private readonly snackbar: SnackbarService,
    private readonly confirmDialog: ConfirmDialogService,
    private readonly cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.isExpanded = false;
  }

  @HostListener('document:keydown.escape')
  handleEscapeKey(): void {
    if (this.isDialogOpen) {
      this.closeDialog();
    }
  }

  toggleSearch(event?: Event): void {
    if (event) {
      event.stopPropagation();
    }
    this.isExpanded = !this.isExpanded;
    this.cdr.markForCheck();
  }

  openSearch(): void {
    this.isExpanded = true;
    this.cdr.markForCheck();
  }

  closeSearch(event?: Event): void {
    if (event) {
      event.stopPropagation();
    }
    this.isExpanded = false;
    this.cdr.markForCheck();
  }

  openDialog(): void {
    this.isDialogOpen = true;
    this.cdr.markForCheck();
  }

  closeDialog(): void {
    this.isDialogOpen = false;
    this.cdr.markForCheck();
  }

  // Pagination getters & methods
  get totalPages(): number {
    return Math.max(1, Math.ceil(this.searchResults.length / this.pageSize));
  }

  get pageStart(): number {
    return this.searchResults.length === 0 ? 0 : (this.currentPage - 1) * this.pageSize + 1;
  }

  get pageEnd(): number {
    return Math.min(this.currentPage * this.pageSize, this.searchResults.length);
  }

  get pagedResults(): SubWorkItem[] {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.searchResults.slice(start, start + this.pageSize);
  }

  goToFirstPage(): void {
    this.currentPage = 1;
  }

  goToPrevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  goToNextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  goToLastPage(): void {
    this.currentPage = this.totalPages;
  }

  onPageSizeChange(newSize: number): void {
    this.pageSize = Number(newSize) || 10;
    this.currentPage = 1;
  }

  resetFilters(): void {
    this.filters = DEFAULT_FILTERS();
    this.searchResults = [];
    this.hasSearched = false;
    this.errorMessage = '';
    this.isDialogOpen = false;
    this.currentPage = 1;
  }

  performSearch(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.hasSearched = true;
    this.currentPage = 1;

    // Check if an API search query can be made for backend works
    const textQuery = (
      this.filters.workRegistrationNumber ||
      this.filters.subWorkRegistrationNumber ||
      this.filters.workName ||
      this.filters.subWorkName
    ).trim();

    if (textQuery.length >= 2) {
      this.api.searchWorks(textQuery).subscribe({
        next: backendWorks => {
          this.applyFilters(backendWorks);
          this.isLoading = false;
          this.isDialogOpen = true; // Open 80% screen dialog with results table
          this.cdr.markForCheck();
          if (this.searchResults.length === 0) {
            this.snackbar.showWarning('No works matched your search criteria.');
          }
        },
        error: () => {
          // Fall back gracefully to catalog search
          this.applyFilters([]);
          this.isLoading = false;
          this.isDialogOpen = true; // Open 80% screen dialog with results table
          this.cdr.markForCheck();
          if (this.searchResults.length === 0) {
            this.snackbar.showWarning('No works matched your search criteria.');
          }
        },
      });
    } else {
      this.applyFilters([]);
      this.isLoading = false;
      this.isDialogOpen = true; // Open 80% screen dialog with results table
      this.cdr.markForCheck();
      if (this.searchResults.length === 0) {
        this.snackbar.showWarning('No works matched your search criteria.');
      }
    }
  }

  private applyFilters(backendWorks: WorkSummary[]): void {
    const combined: SubWorkItem[] = [...this.mockCatalog];

    for (const bw of backendWorks) {
      if (!combined.some(c => c.id === bw.id)) {
        combined.push({
          id: bw.id,
          workNumber: bw.workNumber,
          description: bw.description,
          source: bw.source || 'BACKEND REPOSITORY',
          mock: bw.mock || false,
          workRegistrationNumber: bw.workNumber,
          workDescription: bw.description,
          workType: 'MINOR',
          workStatus: 'ONGOING',
          workGisCode: '',
          budgetYear: '2026-2027',
          status: 'APPROVED',
          workName: bw.description,
          subWorkRegistrationNumber: bw.workNumber + '-SW01',
          subWorkName: bw.description,
          workCategory: 'Roads & Bridges',
          stipulatedStartDate: '2024-04-01',
          stipulatedEndDate: '2024-12-31',
          startDate: '2024-04-15',
          endDate: '2024-12-15',
          contractorName: 'Registered PWD Contractor',
          agreementAmount: 10000000,
          gstAgreementAmount: 11800000,
          asset: 'State Highway 15 (SH-15)',
          isDataPorted: false,
        });
      }
    }

    this.searchResults = combined.filter(item => {
      // 1. Work Registration Number
      if (this.filters.workRegistrationNumber.trim()) {
        const query = this.filters.workRegistrationNumber.trim().toLowerCase();
        if (!item.workRegistrationNumber.toLowerCase().includes(query)) {
          return false;
        }
      }

      // 2. Work Name / Description
      if (this.filters.workName.trim()) {
        const query = this.filters.workName.trim().toLowerCase();
        if (
          !item.workName.toLowerCase().includes(query) &&
          !item.workDescription.toLowerCase().includes(query)
        ) {
          return false;
        }
      }

      // 3. Sub Work Registration Number
      if (this.filters.subWorkRegistrationNumber.trim()) {
        const query = this.filters.subWorkRegistrationNumber.trim().toLowerCase();
        if (!item.subWorkRegistrationNumber.toLowerCase().includes(query)) {
          return false;
        }
      }

      // 4. Sub Work Name
      if (this.filters.subWorkName.trim()) {
        const query = this.filters.subWorkName.trim().toLowerCase();
        if (!item.subWorkName.toLowerCase().includes(query)) {
          return false;
        }
      }

      // 5. Work Category
      if (this.filters.workCategory && item.workCategory !== this.filters.workCategory) {
        return false;
      }

      // 6. Stipulated Start Date
      if (this.filters.stipulatedStartDate) {
        if (!item.stipulatedStartDate || item.stipulatedStartDate < this.filters.stipulatedStartDate) {
          return false;
        }
      }

      // 7. Stipulated End Date
      if (this.filters.stipulatedEndDate) {
        if (!item.stipulatedEndDate || item.stipulatedEndDate > this.filters.stipulatedEndDate) {
          return false;
        }
      }

      // 8. Start Date
      if (this.filters.startDate) {
        if (!item.startDate || item.startDate < this.filters.startDate) {
          return false;
        }
      }

      // 9. End Date
      if (this.filters.endDate) {
        if (!item.endDate || item.endDate > this.filters.endDate) {
          return false;
        }
      }

      // 10. Contractor Name
      if (this.filters.contractorName.trim()) {
        const query = this.filters.contractorName.trim().toLowerCase();
        if (!item.contractorName.toLowerCase().includes(query)) {
          return false;
        }
      }

      // 11. Agreement Amt. (₹)
      if (this.filters.agreementAmount !== null && !isNaN(this.filters.agreementAmount)) {
        if (item.agreementAmount < this.filters.agreementAmount) {
          return false;
        }
      }

      // 12. GST Agreement Amt. (₹)
      if (this.filters.gstAgreementAmount !== null && !isNaN(this.filters.gstAgreementAmount)) {
        if (item.gstAgreementAmount < this.filters.gstAgreementAmount) {
          return false;
        }
      }

      // 13. Asset
      if (this.filters.asset && item.asset !== this.filters.asset) {
        return false;
      }

      // 14. Status
      if (this.filters.status && item.status.toUpperCase() !== this.filters.status.toUpperCase()) {
        return false;
      }

      // 15. Work Status
      if (this.filters.workStatus && item.workStatus.toUpperCase() !== this.filters.workStatus.toUpperCase()) {
        return false;
      }

      // 16. Is Data Ported
      if (this.filters.isDataPorted && !item.isDataPorted) {
        return false;
      }

      return true;
    });
  }

  selectWork(item: SubWorkItem): void {
    this.selectedWorkId = item.id;
    this.selectedWorkNumber = item.workRegistrationNumber;
    this.selectedWorkDescription = item.workDescription;
    this.workSelected.emit({
      id: item.id,
      workNumber: item.workRegistrationNumber,
      description: item.workDescription,
      source: item.source,
      mock: item.mock,
    });
    this.isDialogOpen = false; // Close the dialog upon selecting
    this.snackbar.showSuccess(`Selected ${item.workRegistrationNumber} as Parent Work.`);
  }

  clearSelection(event?: Event): void {
    if (event) {
      event.stopPropagation();
    }
    this.confirmDialog
      .confirm({
        title: 'Delete Work?',
        message: 'Are you sure?',
        okText: 'Ok',
        cancelText: 'Cancel',
      })
      .then(confirmed => {
        if (confirmed) {
          this.selectedWorkId = null;
          this.selectedWorkNumber = null;
          this.selectedWorkDescription = null;
          this.workCleared.emit();
          this.snackbar.showInfo('Parent work selection cleared.');
        }
      });
  }
}
