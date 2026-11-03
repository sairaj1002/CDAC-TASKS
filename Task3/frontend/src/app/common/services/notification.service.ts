import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { APP_CONSTANTS } from '../constants/app.constants';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  constructor(private snackBar: MatSnackBar) {}

  showError(message: string): void {
    const snackRef = this.snackBar.open(`•  ${message}`, 'Copy', {
      duration: APP_CONSTANTS.SNACKBAR.DURATION_MS,
      panelClass: [APP_CONSTANTS.SNACKBAR.PANEL_CLASSES.ERROR],
      horizontalPosition: 'right',
      verticalPosition: 'top',
    });
    snackRef.onAction().subscribe(() => {
      navigator.clipboard.writeText(message);
    });
  }

  showSuccess(message: string): void {
    const snackRef = this.snackBar.open(`✓  ${message}`, 'Close', {
      duration: APP_CONSTANTS.SNACKBAR.DURATION_MS,
      panelClass: [APP_CONSTANTS.SNACKBAR.PANEL_CLASSES.SUCCESS],
      horizontalPosition: 'right',
      verticalPosition: 'top',
    });
    snackRef.onAction().subscribe(() => snackRef.dismiss());
  }

  showInfo(message: string): void {
    const snackRef = this.snackBar.open(`ℹ  ${message}`, 'Copy', {
      duration: APP_CONSTANTS.SNACKBAR.DURATION_MS,
      panelClass: [APP_CONSTANTS.SNACKBAR.PANEL_CLASSES.INFO],
      horizontalPosition: 'right',
      verticalPosition: 'top',
    });
    snackRef.onAction().subscribe(() => {
      navigator.clipboard.writeText(message);
    });
  }

  showWarning(message: string): void {
    const snackRef = this.snackBar.open(`⚠  ${message}`, 'Close', {
      duration: APP_CONSTANTS.SNACKBAR.DURATION_MS,
      panelClass: [APP_CONSTANTS.SNACKBAR.PANEL_CLASSES.WARNING],
      horizontalPosition: 'right',
      verticalPosition: 'top',
    });
    snackRef.onAction().subscribe(() => snackRef.dismiss());
  }
}
