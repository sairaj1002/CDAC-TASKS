import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { AccessService, OPERATION } from '../services/access.service';

// Opening a screen by typing its address needs the same right as the menu entry. The route says which screen it is
// (data.functionCode) and, for the Add and View tabs, which operation (data.operation).
//  - unknown or inactive user id: back to the home page; the "inactive or unknown" message is already shown there
//  - no operation on the screen at all: back to the home page with the "access denied" message
//  - a tab the user may not use but the other one yes (only view): opens the other tab, with the "access denied" message
export const accessGuard: CanActivateFn = route => {
  const access = inject(AccessService);
  const router = inject(Router);
  return access.ready().pipe(
    map(() => {
      if (!access.isValid()) {
        return router.parseUrl('/home');
      }
      const functionCode: string = route.data['functionCode'];
      if (!access.hasAccess(functionCode)) {
        access.notifyNoAccess();
        return router.parseUrl('/home');
      }
      const operation: string | undefined = route.data['operation'];
      if (operation && !access.can(functionCode, operation)) {
        const other = operation === OPERATION.ADD ? OPERATION.VIEW : OPERATION.ADD;
        access.notifyNoAccess();
        return router.parseUrl('/home/qms/' + route.routeConfig!.path!.replace(/\/(add|view)$/, '/' + other));
      }
      return true;
    })
  );
};
