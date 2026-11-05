import { Injectable } from '@angular/core';
import { SnackbarService } from './snackbar.service';

/**
 * Pop-up messages of the WMS screens. They use the QMS SnackbarService, so every message in the
 * application looks the same (top of the page, green for success, red for errors).
 */
@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  constructor(private snackbar: SnackbarService) {}

  showError(message: string): void {
    this.snackbar.showError(message);
  }

  showSuccess(message: string): void {
    this.snackbar.showSuccess(message);
  }

  // QMS has two styles only: information reads as a success, a warning as an error
  showInfo(message: string): void {
    this.snackbar.showSuccess(message);
  }

  showWarning(message: string): void {
    this.snackbar.showError(message);
  }
}
