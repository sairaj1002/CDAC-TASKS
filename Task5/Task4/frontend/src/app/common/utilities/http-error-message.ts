// The readable message of a failed HTTP call. Plain functions, so they are easy to test.

// {"message": "..."} sent by the backend, as an object or as JSON text; the fallback when there is none
export function readableMessage(error: unknown, fallback: string): string {
  // No answer at all (server down, network cut): the browser's own text ("Failed to fetch") means nothing to a user
  if ((error as { status?: unknown } | null | undefined)?.status === 0) {
    return messageForStatus(0);
  }
  let body = (error as { error?: unknown } | null | undefined)?.error;
  if (typeof body === 'string') {
    try {
      body = JSON.parse(body);
    } catch {
      // plain text, used as it is below
    }
  }
  const fields = body as { msg?: unknown; message?: unknown } | null | undefined;
  const message = typeof body === 'string' ? body : (fields?.msg ?? fields?.message);
  return typeof message === 'string' && message.trim() && message.length < 300 ? message : fallback;
}

// A plain sentence for the status, used when the server sent no message of its own
export function messageForStatus(status: number): string {
  if (status === 0) {
    return 'Unable to reach the server. Please check your connection and try again.';
  }
  if (status === 401) {
    return 'Your session has expired. Please sign in again.';
  }
  if (status === 403) {
    return 'You are not allowed to do this.';
  }
  if (status === 404) {
    return 'The requested data was not found.';
  }
  if (status === 408 || status === 504) {
    return 'The server took too long to respond. Please try again.';
  }
  if (status === 409) {
    return 'This record was just changed by someone else. Please reload and try again.';
  }
  if (status >= 500) {
    return 'Something went wrong on the server. Please try again.';
  }
  return 'The request could not be completed. Please check the entered data and try again.';
}
