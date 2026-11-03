import { ErrorHandler, Injectable, Injector, NgZone } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { SnackbarService } from '../services/snackbar.service';

// Catches every error that no screen handled (a bug in the page code, an HTTP call nobody listens to)
// and tells the user in the same pop-up as everything else, so the page never fails silently.
@Injectable()
export class GlobalErrorHandler implements ErrorHandler {

  private lastShown = 0;

  // The services are looked up when needed; asking for them in the constructor would make a circular dependency
  constructor(private readonly injector: Injector, private readonly zone: NgZone) { }

  handleError(error: unknown): void {
    console.error(error);

    // One pop-up at a time: a burst of identical errors must not bury the page
    const now = Date.now();
    if (now - this.lastShown < 3000) {
      return;
    }
    this.lastShown = now;

    const message = error instanceof HttpErrorResponse
      ? SnackbarService.messageOf(error, 'The request could not be completed. Please try again.')
      : 'An unexpected error occurred. Please try again; if it keeps happening, reload the page.';
    this.zone.run(() => this.injector.get(SnackbarService).showError(message));
  }
}
