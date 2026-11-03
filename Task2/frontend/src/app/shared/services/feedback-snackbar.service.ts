import { inject, Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { COMMON_UI } from '../constants/common-constants';
import { FeedbackSnackBar } from '../components/feedback-snackbar';

@Injectable({ providedIn: 'root' })
export class FeedbackSnackBarService {
  private readonly snackBar = inject(MatSnackBar);

  success(message: string): void {
    this.show(message, 'success', COMMON_UI.SNACKBAR_DURATION_SUCCESS);
  }

  error(message: string): void {
    this.show(message, 'error', COMMON_UI.SNACKBAR_DURATION_ERROR);
  }

  warning(message: string): void {
    this.show(message, 'error', COMMON_UI.SNACKBAR_DURATION_WARNING);
  }

  private show(message: string, type: 'success' | 'error', duration: number): void {
    this.snackBar.openFromComponent(FeedbackSnackBar, {
      data: { message, type },
      duration,
      horizontalPosition: COMMON_UI.SNACKBAR_HORIZONTAL_POSITION,
      verticalPosition: COMMON_UI.SNACKBAR_VERTICAL_POSITION,
      panelClass: [COMMON_UI.SNACKBAR_PANEL_CLASS],
    });
  }
}
