import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { messageForStatus, readableMessage } from '../../utilities/http-error-message';

// Every failed HTTP call leaves this interceptor with a readable message in error.message:
// the server's own message when it sent one, otherwise a plain sentence for the status
// (server not reachable, not found, server error ...). Screens only read error.error.msg.
export const exceptionInterceptor: HttpInterceptorFn = (request, next) =>
  next(request).pipe(
    catchError((error: unknown) =>
      throwError(() => (error instanceof HttpErrorResponse ? withReadableMessage(error) : error))
    )
  );

function withReadableMessage(error: HttpErrorResponse): HttpErrorResponse {
  // A real message from the server stays as it is; "no answer at all" (status 0) is always rewritten,
  // because its own text is the browser's technical "Failed to fetch"
  if (error.status !== 0 && readableMessage(error, '')) {
    return error;
  }
  return new HttpErrorResponse({
    // the same shape the backend uses for its errors (className and msg)
    error: { className: 'text-danger', msg: messageForStatus(error.status) },
    headers: error.headers,
    status: error.status,
    statusText: error.statusText,
    url: error.url ?? undefined,
  });
}
