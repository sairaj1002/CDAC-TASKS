import { Component, OnInit, inject, signal } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { finalize, timeout } from 'rxjs';
import { getApiErrorMessage } from '../../api-response';
import { BridgeApiService } from './bridge-api.service';
import { BridgeAuditRevision } from './bridge-component';

interface BridgeAuditDialogData {
  id: number;
  riverNallahBarrier: string;
}

@Component({
  selector: 'app-bridge-audit-dialog',
  standalone: false,
  templateUrl: './bridge-audit-dialog.html',
})
export class BridgeAuditDialog implements OnInit {
  readonly data = inject<BridgeAuditDialogData>(MAT_DIALOG_DATA);
  private readonly bridgeApi = inject(BridgeApiService);

  revisions = signal<BridgeAuditRevision[]>([]);
  loading = signal(true);
  errorMessage = signal('');

  ngOnInit(): void {
    this.bridgeApi
      .getAuditHistory(this.data.id)
      .pipe(
        timeout({ first: 15000 }),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (revisions) => {
          this.revisions.set(revisions);
        },
        error: (error: unknown) => {
          this.errorMessage.set(
            getApiErrorMessage(error, 'Audit history could not be loaded. Please try again.'),
          );
        },
      });
  }
}
