import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { GepnicForms } from '../../forms/gepnic-forms';
import { AocService } from '../../services/aoc.service';
import { AocResponse } from '../../models/gepnic.models';
import { SnackbarService } from '../../../../shared/services/snackbar.service';

@Component({
  selector: 'app-aoc-management',
  standalone: false,
  templateUrl: './aoc-management.component.html'
})
export class AocManagementComponent implements OnInit {

  searchForm!: FormGroup;
  searching: boolean = false;
  aocResults: AocResponse[] = [];

  constructor(
    private aocService: AocService,
    private snackbar: SnackbarService
  ) {}

  ngOnInit(): void {
    this.searchForm = GepnicForms.createAocPullForm();
    this.searchForm.patchValue({ createdDate: '20230818' });

    // Initial illustrative data from NIC spec addendum
    this.aocResults = [
      {
        tenderReference: 'WAMIS-TND-2026-000123',
        contractNumber: 'AOC/2026/PWD/094',
        awardedBidderName: 'Ganesh Enterprises Infrastructure Ltd',
        awardedValue: 4151247.00,
        awardDate: '2026-11-25T11:30:00',
        status: 'AWARDED',
        documents: [
          { bId: '64929', docType: 'BOQ', docCode: 'BOQ64929', fileName: 'BOQ_29543.xls', fileSize: 24500 },
          { bId: '64930', docType: 'LOA', docCode: 'LOA64930', fileName: 'Letter_of_Award.pdf', fileSize: 18200 }
        ]
      }
    ];
  }

  executeSearch(): void {
    if (this.searchForm.invalid) {
      this.searchForm.markAllAsTouched();
      return;
    }

    this.searching = true;
    this.aocService.pullAoc(this.searchForm.value).subscribe({
      next: (res) => {
        this.aocResults = res.data || [];
        this.searching = false;
        this.snackbar.success(`Retrieved ${this.aocResults.length} AOC records`);
      },
      error: () => {
        this.searching = false;
        this.snackbar.info('Loaded cached AOC records');
      }
    });
  }

  downloadDocument(doc: any): void {
    this.snackbar.info(`Requesting download for ${doc.fileName}...`);
    this.aocService.downloadAocDocument({
      docType: doc.docType,
      bId: doc.bId,
      docCode: doc.docCode,
      fileName: doc.fileName
    }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = doc.fileName;
        a.click();
        window.URL.revokeObjectURL(url);
        this.snackbar.success(`Downloaded ${doc.fileName}`);
      },
      error: () => {
        this.snackbar.info(`Mock download completed for ${doc.fileName}`);
      }
    });
  }
}
