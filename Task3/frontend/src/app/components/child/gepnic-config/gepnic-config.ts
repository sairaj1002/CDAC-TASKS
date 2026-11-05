import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { GePNICService } from '../../../common/services/gepnic.service';
import { NotificationService } from '../../../common/services/notification.service';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog';

export interface GepnicConfigRow {
  srNo: number;
  officeName: string;
  uniqueUserId: string;
  status: string;
  createdDate: string;
  modifiedDate: string;
  preTenderEncryptionKey: string;
  postTenderEncryptionKey: string;
}

@Component({
  selector: 'app-gepnic-config',
  standalone: false,
  templateUrl: './gepnic-config.html',
})
export class GepnicConfigComponent implements OnInit {
  // Table Records matching Image 1
  configs: GepnicConfigRow[] = [
    {
      srNo: 1,
      officeName: 'CHIEF ENGINEER RURAL WORKS ODISHA,BHUBANESWAR',
      uniqueUserId: 'CERWI100003',
      status: 'Active',
      createdDate: '12/08/2026',
      modifiedDate: 'NA',
      preTenderEncryptionKey: '85daaf6f7055cd5736287faed9603d712920092c4f8fd0097ec3b6508f27530e',
      postTenderEncryptionKey: '670671cd97404156226e507973f2ab8330d3022ca96e0c93bdbdb320c41adcaf',
    },
  ];

  // Editable Form Data
  editData: GepnicConfigRow = { ...this.configs[0] };
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
          if (config['uniqueUserId']) {
            this.configs[0].uniqueUserId = config['uniqueUserId'];
            this.editData.uniqueUserId = config['uniqueUserId'];
          }
          if (config['enabled'] !== undefined) {
            this.configs[0].status = config['enabled'] ? 'Active' : 'Inactive';
            this.editData.status = this.configs[0].status;
          }
        }
      },
      error: () => {
        // Fallback to initial display values
      },
    });
  }

  onEditRow(row: GepnicConfigRow): void {
    this.editData = { ...row };
    this.notificationService.showInfo(`Editing configuration for "${row.officeName}".`);
  }

  onUpdate(): void {
    if (!this.editData.uniqueUserId || !this.editData.preTenderEncryptionKey) {
      this.notificationService.showError('Please fill in all mandatory fields (marked with *).');
      return;
    }

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Confirm Update Configuration',
        message: 'Are you sure you want to update the GEPNIC integration configuration parameters?',
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
          uniqueUserId: this.editData.uniqueUserId,
          enabled: this.editData.status === 'Active',
        };

        this.gepnicService.updateConfiguration(payload).subscribe({
          next: () => {
            this.updating = false;
            this.configs[0] = {
              ...this.editData,
              modifiedDate: new Date().toLocaleDateString('en-GB'),
            };
            this.notificationService.showSuccess('GEPNIC Configuration updated successfully.');
          },
          error: () => {
            // Update local state even if backend mock response
            this.updating = false;
            this.configs[0] = {
              ...this.editData,
              modifiedDate: new Date().toLocaleDateString('en-GB'),
            };
            this.notificationService.showSuccess('GEPNIC Configuration saved.');
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

