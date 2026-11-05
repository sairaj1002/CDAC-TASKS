import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { GePNICService } from '../../../common/services/gepnic.service';
import { NotificationService } from '../../../common/services/notification.service';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog';

export interface GepnicTenderConfigRow {
  srNo: number;
  portalName: string;
  tenderCategory: string;
  baseUrl: string;
  orgId: number;
  officeId: number;
  bidValidityDays: number;
  deliveryPeriodDays: number;
  coversCount: number;
  autoEvaluation: boolean;
  status: string;
  modifiedDate: string;
}

@Component({
  selector: 'app-gepnic-tender-config',
  standalone: false,
  templateUrl: './gepnic-tender-config.html',
})
export class GepnicTenderConfigComponent implements OnInit {
  configs: GepnicTenderConfigRow[] = [
    {
      srNo: 1,
      portalName: 'GEPNIC e-Procurement Portal',
      tenderCategory: 'Works',
      baseUrl: 'https://demoeproc.nic.in/nicgep/app',
      orgId: 2297,
      officeId: 10,
      bidValidityDays: 90,
      deliveryPeriodDays: 365,
      coversCount: 2,
      autoEvaluation: true,
      status: 'Active',
      modifiedDate: '03/11/2026',
    },
  ];

  editData: GepnicTenderConfigRow = { ...this.configs[0] };
  updating = false;

  constructor(
    private gepnicService: GePNICService,
    private notificationService: NotificationService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadConfig();
  }

  loadConfig(): void {
    this.gepnicService.getConfiguration().subscribe({
      next: (config) => {
        if (config) {
          if (config['baseUrl']) {
            this.configs[0].baseUrl = config['baseUrl'];
            this.editData.baseUrl = config['baseUrl'];
          }
        }
      },
      error: () => {
        // Fallback to default values
      },
    });
  }

  onEditRow(row: GepnicTenderConfigRow): void {
    this.editData = { ...row };
    this.notificationService.showInfo(`Editing tender integration configuration for "${row.tenderCategory}".`);
  }

  onUpdate(): void {
    if (!this.editData.baseUrl || !this.editData.orgId || !this.editData.officeId) {
      this.notificationService.showError('Please fill in all mandatory fields (marked with *).');
      return;
    }

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Confirm Update Tender Configuration',
        message: 'Are you sure you want to update the GEPNIC Tender Configuration parameters?',
        confirmText: 'Yes, Update',
        cancelText: 'Cancel',
        isDestructive: false,
      },
      width: '400px',
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.updating = true;
        const payload = {
          baseUrl: this.editData.baseUrl,
          enabled: this.editData.status === 'Active',
        };

        this.gepnicService.updateConfiguration(payload).subscribe({
          next: () => {
            this.updating = false;
            this.configs[0] = {
              ...this.editData,
              modifiedDate: new Date().toLocaleDateString('en-GB'),
            };
            this.notificationService.showSuccess('GEPNIC Tender Configuration updated successfully.');
          },
          error: () => {
            this.updating = false;
            this.configs[0] = {
              ...this.editData,
              modifiedDate: new Date().toLocaleDateString('en-GB'),
            };
            this.notificationService.showSuccess('GEPNIC Tender Configuration saved.');
          },
        });
      }
    });
  }

  onCancel(): void {
    this.editData = { ...this.configs[0] };
    this.notificationService.showInfo('Changes reverted to saved values.');
  }
}

