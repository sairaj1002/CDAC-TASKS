import { Component } from '@angular/core';
import { TenderIntegrationService } from '../../services/tender-integration.service';
import { DialogService } from '../../../../shared/services/dialog.service';
import { SnackbarService } from '../../../../shared/services/snackbar.service';

export interface PushedTenderSummary {
  wamisTenderId: string;
  tenderTitle: string;
  officeId: string;
  estimatedCost: number;
  publishDate: string;
  submissionEndDate: string;
  status: string;
}

@Component({
  selector: 'app-tender-push',
  standalone: false,
  templateUrl: './tender-push.component.html'
})
export class TenderPushComponent {

  primaryTab: 'Add' | 'View' = 'Add';

  submitting: boolean = false;
  lastResult: any = null;
  requestPayload: any = null;
  responsePayload: any = null;
  initialTenderData: any = null;

  // View Tab Pushed Tenders Inventory
  pushedTenders: PushedTenderSummary[] = [
    {
      wamisTenderId: 'WAMIS-TND-2026-000123',
      tenderTitle: 'Road Improvement Work in Pune Division',
      officeId: 'OFF-001 (Pune Division)',
      estimatedCost: 6660150,
      publishDate: '2026-11-01 10:00',
      submissionEndDate: '2026-11-20 17:00',
      status: 'PUSHED_TO_GEPNIC'
    },
    {
      wamisTenderId: 'WAMIS-TND-2026-000124',
      tenderTitle: 'Construction of Cross Drainage Structures on MDR-12',
      officeId: 'OFF-002 (Baramati Division)',
      estimatedCost: 3450000,
      publishDate: '2026-10-25 11:30',
      submissionEndDate: '2026-11-15 17:00',
      status: 'PUSHED_TO_GEPNIC'
    },
    {
      wamisTenderId: 'WAMIS-TND-2026-000125',
      tenderTitle: 'Bridge Strengthening and Bituminous Resurfacing',
      officeId: 'OFF-003 (Haveli Sub-Division)',
      estimatedCost: 8900000,
      publishDate: '2026-10-20 09:00',
      submissionEndDate: '2026-11-10 18:00',
      status: 'PUBLISHED_IN_NIC'
    }
  ];

  constructor(
    private tenderService: TenderIntegrationService,
    private dialog: DialogService,
    private snackbar: SnackbarService
  ) {}

  setPrimaryTab(tab: 'Add' | 'View'): void {
    this.primaryTab = tab;
  }

  onFormSubmit(payload: any): void {
    this.dialog.confirm({
      title: 'Push Tender to GePNIC?',
      message: `Are you sure you want to encrypt and dispatch ${payload.wamisTenderId} to GePNIC?`,
      okText: 'Ok',
      cancelText: 'Cancel'
    }).subscribe(confirmed => {
      if (confirmed) {
        this.executePush(payload);
      }
    });
  }

  private executePush(payload: any): void {
    this.submitting = true;
    this.tenderService.pushTender(payload).subscribe({
      next: (res) => {
        this.submitting = false;
        this.lastResult = res.data;
        this.requestPayload = {
          uniqueUserId: 'WAMIS-REGISTERED-USER-ID',
          encryptData: 'V1RNU19URU5ERVJfRU5DUllQVEVEX1BBWUxPQUQ=',
          signData: 'V1RNU19TSEEyNTZfRElHSVRBTF9TSUdOQVRVUkU=',
          encryptKey: 'QUVTMjU2X1NFU1NJT05fS0VZX1dSQVBQRUQ=',
          keyId: 'GEPNIC-CERTIFICATE-THUMBPRINT-001'
        };
        this.responsePayload = {
          responseCode: res.data.responseCode || 1,
          responseMessage: {
            tenderReference: payload.wamisTenderId,
            status: res.data.status,
            message: res.data.message || 'Tender request processed successfully'
          }
        };

        // Add to view list
        this.pushedTenders.unshift({
          wamisTenderId: payload.wamisTenderId,
          tenderTitle: payload.tenderTitle || 'Civil Engineering Works',
          officeId: payload.officeId || 'OFF-001',
          estimatedCost: payload.estimatedCost || 0,
          publishDate: payload.publishDate || new Date().toISOString(),
          submissionEndDate: payload.submissionEndDate || new Date().toISOString(),
          status: 'PUSHED_TO_GEPNIC'
        });

        this.snackbar.success('Tender submitted to GePNIC successfully!');
      },
      error: (err) => {
        this.submitting = false;
        this.snackbar.error('Failed to submit tender: ' + (err.error?.message || err.message));
      }
    });
  }

  viewTenderDetails(t: PushedTenderSummary): void {
    this.dialog.showDetails(`Pushed Tender: ${t.wamisTenderId}`, t);
  }
}
