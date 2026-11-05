import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Message } from '../forms/message';
import { readableMessage } from '../utilities/http-error-message';

// Pop-up message at the top of the page, set up like the QMS SnackbarService (10 seconds, top, same style classes).
// Every screen uses it instead of the browser's alert(), so messages look the same everywhere.
@Injectable({ providedIn: 'root' })
export class SnackbarService {

  constructor(private readonly snackBar: MatSnackBar) { }

  // The answer of the backend after an action: its text, in the style it names (text-success / text-danger)
  showMessage(message: Message): void {
    this.showMessageSnackbar(message.msg, message.className);
  }

  // Same name and meaning as the original QMS SnackbarService
  showMessageSnackbar(message: string, className: string): void {
    this.open(message, className !== 'text-success');
  }

  showSuccess(message: string): void {
    this.open(message, false);
  }

  showError(message: string): void {
    this.open(message, true);
  }

  // Shows the message the server sent (the exception interceptor makes sure there always is one)
  showHttpError(error: unknown, fallback: string): void {
    this.open(SnackbarService.messageOf(error, fallback), true);
  }

  // The readable message of a failed HTTP call (see http-error-message.ts)
  static messageOf(error: unknown, fallback: string): string {
    return readableMessage(error, fallback);
  }

  private open(message: string, isError: boolean): void {
    this.snackBar.open(message, 'Close', {
      duration: 10000,
      verticalPosition: 'top',
      panelClass: [isError ? 'text-danger' : 'text-success', 'mt-5', isError ? 'snackbar-danger' : 'snackbar-success'],
    });
  }
}
