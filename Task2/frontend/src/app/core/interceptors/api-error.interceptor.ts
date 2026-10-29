import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { SnackbarService } from '../../shared/services/snackbar.service';

@Injectable()
export class ApiErrorInterceptor implements HttpInterceptor {

  constructor(private snackbar: SnackbarService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMsg = 'An unexpected error occurred';
        if (error.error && error.error.message) {
          errorMsg = error.error.message;
        } else if (error.status === 0) {
          errorMsg = 'Cannot connect to integration backend (localhost:8080). Please ensure backend is running.';
        } else {
          errorMsg = `Server error HTTP ${error.status}: ${error.statusText}`;
        }
        this.snackbar.error(errorMsg);
        return throwError(() => error);
      })
    );
  }
}
