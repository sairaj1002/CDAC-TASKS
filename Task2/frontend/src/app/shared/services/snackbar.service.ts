import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class SnackbarService {

  constructor(private snackBar: MatSnackBar) {}

  showSuccess(message: string): void {
    this.success(message);
  }

  showError(message: string): void {
    this.error(message);
  }

  showWarning(message: string): void {
    this.warning(message);
  }

  showInfo(message: string): void {
    this.info(message);
  }

  success(message: string, duration: number = 4000): void {
    const formatted = message.startsWith('•') ? message : `•  ${message}`;
    const ref = this.snackBar.open(formatted, '▢ Copy', {
      duration,
      panelClass: ['wamis-snackbar-success'],
      horizontalPosition: 'right',
      verticalPosition: 'top'
    });
    ref.onAction().subscribe(() => {
      this.copyToClipboard(message);
    });
  }

  error(message: string, duration: number = 6000): void {
    const formatted = message.startsWith('•') ? message : `•  ${message}`;
    const ref = this.snackBar.open(formatted, '▢ Copy', {
      duration,
      panelClass: ['wamis-snackbar-error'],
      horizontalPosition: 'right',
      verticalPosition: 'top'
    });
    ref.onAction().subscribe(() => {
      this.copyToClipboard(message);
    });
  }

  warning(message: string, duration: number = 5000): void {
    const formatted = message.startsWith('•') ? message : `•  ${message}`;
    const ref = this.snackBar.open(formatted, '▢ Copy', {
      duration,
      panelClass: ['wamis-snackbar-warning'],
      horizontalPosition: 'right',
      verticalPosition: 'top'
    });
    ref.onAction().subscribe(() => {
      this.copyToClipboard(message);
    });
  }

  info(message: string, duration: number = 4000): void {
    const formatted = message.startsWith('•') ? message : `•  ${message}`;
    const ref = this.snackBar.open(formatted, '▢ Copy', {
      duration,
      panelClass: ['wamis-snackbar-info'],
      horizontalPosition: 'right',
      verticalPosition: 'top'
    });
    ref.onAction().subscribe(() => {
      this.copyToClipboard(message);
    });
  }

  private copyToClipboard(text: string): void {
    if (navigator.clipboard) {
      navigator.clipboard.writeText(text).catch(() => {});
    }
  }
}
