import { HttpErrorResponse } from '@angular/common/http';

export interface ApiSuccessResponse<T> {
  timestamp: string;
  status: number;
  success: true;
  message: string;
  data: T;
}

export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  success: false;
  error: string;
  message: string;
  fieldErrors: Record<string, string>;
}

function isApiErrorResponse(body: unknown): body is ApiErrorResponse {
  if (
    typeof body !== 'object' ||
    body === null ||
    !('success' in body) ||
    body.success !== false ||
    !('timestamp' in body) ||
    typeof body.timestamp !== 'string' ||
    !('status' in body) ||
    typeof body.status !== 'number' ||
    !('error' in body) ||
    typeof body.error !== 'string' ||
    !('message' in body) ||
    typeof body.message !== 'string' ||
    !('fieldErrors' in body) ||
    typeof body.fieldErrors !== 'object' ||
    body.fieldErrors === null ||
    Array.isArray(body.fieldErrors)
  ) {
    return false;
  }

  return Object.values(body.fieldErrors).every((message) => typeof message === 'string');
}

export function getApiErrorMessage(error: unknown, fallback: string): string {
  if (!(error instanceof HttpErrorResponse) || !isApiErrorResponse(error.error)) {
    return fallback;
  }

  return error.error.message;
}
