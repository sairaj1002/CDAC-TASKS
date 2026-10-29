import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { ConfirmDialogComponent, ConfirmDialogData } from '../components/confirm-dialog/confirm-dialog.component';
import { DetailsDialogComponent } from '../components/details-dialog/details-dialog.component';
import { AuditDialogComponent } from '../components/audit-dialog/audit-dialog.component';

@Injectable({
  providedIn: 'root'
})
export class DialogService {

  constructor(private dialog: MatDialog) {}

  confirm(data: ConfirmDialogData): Observable<boolean> {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data,
      disableClose: true,
      panelClass: 'wamis-dialog-panel'
    });
    return dialogRef.afterClosed();
  }

  showDetails(title: string, details: Record<string, any>): void {
    this.dialog.open(DetailsDialogComponent, {
      data: { title, details },
      width: '750px',
      panelClass: 'wamis-dialog-panel'
    });
  }

  showAudit(transactionId: string): void {
    this.dialog.open(AuditDialogComponent, {
      data: { transactionId },
      width: '800px',
      panelClass: 'wamis-dialog-panel'
    });
  }
}
