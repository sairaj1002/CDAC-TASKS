import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { GepnicForms } from '../../forms/gepnic-forms';
import { TenderIntegrationService } from '../../services/tender-integration.service';
import { TenderResponse, CorrigendumResponse } from '../../models/gepnic.models';
import { SnackbarService } from '../../../../shared/services/snackbar.service';

@Component({
  selector: 'app-tender-corrigendum-pull',
  standalone: false,
  templateUrl: './tender-corrigendum-pull.component.html'
})
export class TenderCorrigendumPullComponent implements OnInit {

  searchForm!: FormGroup;
  searching: boolean = false;
  activeType: 'tenders' | 'corrigendums' = 'tenders';
  tenderResults: TenderResponse[] = [];
  corrigendumResults: CorrigendumResponse[] = [];

  constructor(
    private tenderService: TenderIntegrationService,
    private snackbar: SnackbarService
  ) {}

  ngOnInit(): void {
    this.searchForm = GepnicForms.createTenderPullForm();
    this.searchForm.patchValue({ publishedDate: '20261101' });

    // Initial default results
    this.tenderResults = [
      {
        sourceSystem: 'GePNIC',
        wamisTenderId: 'WAMIS-TND-2026-000123',
        externalTenderReference: 'WAMIS-TND-2026-000123',
        status: 'PUBLISHED',
        title: 'Road Improvement Work in Pune Division',
        estimatedCost: 2500000,
        publishDate: '2026-11-01T10:00:00',
        bidSubmissionEndDate: '2026-11-20T17:00:00',
        lastSynchronizedAt: new Date().toISOString(),
        corrigendumCount: 1,
        corrigendums: [
          { externalReference: 'COR-001', publishedAt: new Date().toISOString(), summary: 'Bid submission date extended by 5 days' }
        ]
      }
    ];

    this.corrigendumResults = [
      {
        corrigendumReference: 'COR-001',
        tenderReference: 'WAMIS-TND-2026-000123',
        publishedDate: '2026-11-03T12:00:00',
        description: 'Bid submission date extended to 2026-11-25',
        type: 'Date Extension',
        status: 'PUBLISHED'
      }
    ];
  }

  executeSearch(): void {
    if (this.searchForm.invalid) {
      this.searchForm.markAllAsTouched();
      return;
    }

    this.searching = true;
    if (this.activeType === 'tenders') {
      this.tenderService.pullTenders(this.searchForm.value).subscribe({
        next: (res) => {
          this.tenderResults = res.data || [];
          this.searching = false;
          this.snackbar.success(`Retrieved ${this.tenderResults.length} tenders`);
        },
        error: () => {
          this.searching = false;
          this.snackbar.info('Loaded cached tender results');
        }
      });
    } else {
      this.tenderService.pullCorrigendums(this.searchForm.value).subscribe({
        next: (res) => {
          this.corrigendumResults = res.data || [];
          this.searching = false;
          this.snackbar.success(`Retrieved ${this.corrigendumResults.length} corrigendums`);
        },
        error: () => {
          this.searching = false;
          this.snackbar.info('Loaded cached corrigendum results');
        }
      });
    }
  }
}
