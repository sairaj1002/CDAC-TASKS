import { Component, OnInit } from '@angular/core';
import { SnackbarService } from '../../../../shared/services/snackbar.service';
import { DialogService } from '../../../../shared/services/dialog.service';

export interface ContractAwardingRecord {
  id: number;
  registrationNo: string;
  status: string;
  contractAgreementNo: string;
  aaAmount: number;
  tsAmount: number;
  totalAmountExclusiveGst: number;
  totalAmountWithGst: number;
  item: string;
  auditDetails: string;
  attachmentsCount: number;
  reportCount?: number;
  amendmentCount?: number;
}

@Component({
  selector: 'app-aoc-management',
  standalone: false,
  templateUrl: './aoc-management.component.html',
  styleUrls: ['./aoc-management.component.css']
})
export class AocManagementComponent implements OnInit {

  searchContractQuery: string = '';
  contractPageSize: number = 10;
  contractPageIndex: number = 1;

  // Defaults to empty matching Image 4 ("No Contract records to display!")
  contractRecords: ContractAwardingRecord[] = [];

  // Selected contract record for view details
  selectedContractRecord: ContractAwardingRecord | null = null;
  showContractDetailsModal: boolean = false;

  constructor(
    private readonly snackbar: SnackbarService,
    private readonly dialog: DialogService
  ) {}

  ngOnInit(): void {}

  onAddAwarding(): void {
    this.dialog.confirm({
      title: 'Add New Awarding Contract',
      message: 'Create a new contract awarding record for this work package?',
      okText: 'Proceed',
      cancelText: 'Cancel'
    }).subscribe(confirmed => {
      if (confirmed) {
        const nextId = this.contractRecords.length + 1;
        const newRecord: ContractAwardingRecord = {
          id: nextId,
          registrationNo: `2026100014/${nextId}`,
          status: 'SANCTIONED',
          contractAgreementNo: `CA/2026/PWD/00${nextId}`,
          aaAmount: 6500000.00,
          tsAmount: 6200000.00,
          totalAmountExclusiveGst: 5500000.00,
          totalAmountWithGst: 6490000.00,
          item: 'Infrastructure Civil & Electrical Work',
          auditDetails: 'Pre-audit Verified',
          attachmentsCount: 2,
          reportCount: 1,
          amendmentCount: 0
        };
        this.contractRecords = [...this.contractRecords, newRecord];
        this.snackbar.success(`Added new contract awarding entry ${newRecord.registrationNo}`);
      }
    });
  }

  viewContractDetails(record: ContractAwardingRecord): void {
    this.selectedContractRecord = record;
    this.showContractDetailsModal = true;
  }

  closeContractDetails(): void {
    this.showContractDetailsModal = false;
    this.selectedContractRecord = null;
  }

  refreshContracts(): void {
    this.snackbar.info('Refreshing contract awarding records...');
  }

  exportContractCsv(): void {
    this.snackbar.info('Exporting contract register document...');
  }
}
