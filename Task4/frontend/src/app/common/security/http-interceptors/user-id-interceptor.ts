import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { UserContextService } from '../../services/user-context.service';

// Adds the id of the user (X-User-Id) to every request to the backend. The backend uses it to find the
// roles of the user and to refuse what the roles do not allow. Not security: only a stand-in until login.
export const USER_ID_HEADER = 'X-User-Id';

export const userIdInterceptor: HttpInterceptorFn = (request, next) => {
  const userId = inject(UserContextService).userId;
  if (userId === null || !request.url.startsWith(environment.baseurl)) {
    return next(request);
  }
  return next(request.clone({ setHeaders: { [USER_ID_HEADER]: userId } }));
};
