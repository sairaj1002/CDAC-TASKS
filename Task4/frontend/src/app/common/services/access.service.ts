import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map, shareReplay, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { RbacFunction, RbacMenu, RbacModule, RbacSubmodule } from '../forms/rbac-menu';
import { SnackbarService } from './snackbar.service';
import { readableMessage } from '../utilities/http-error-message';

// Function codes and operations: the same words as functionCode and operations in the backend rbac/roles.json
export const FUNCTION = {
  ITEM: 'Item',
  WORK_ASSIGNMENT: 'WorkAssignment',
  WORK_INSPECTION: 'WorkInspectionAndReporting',
} as const;

export const OPERATION = {
  VIEW: 'view',
  ADD: 'add',
  EDIT: 'edit',
  APPROVE: 'approve',
  DELETE: 'delete',
} as const;

// What the logged in user may do, as sent by the backend (GET /rbac/menu). Loaded once; every screen asks it
// "can this user do <operation> on <function>?". An unknown or inactive id gets nothing: everything is greyed out.
@Injectable({ providedIn: 'root' })
export class AccessService {

  private menu: RbacMenu = AccessService.emptyMenu('');
  private menu$?: Observable<RbacMenu>;

  constructor(private readonly http: HttpClient, private readonly snackbar: SnackbarService) { }

  // Loads the menu once; later calls get the same answer. The text for an unknown or inactive id comes from the backend.
  load(): Observable<RbacMenu> {
    if (!this.menu$) {
      this.menu$ = this.http.get<RbacMenu>(`${environment.baseurl}rbac/menu`).pipe(
        catchError(error => of(AccessService.emptyMenu(readableMessage(error, '')))),
        tap(menu => this.menu = menu),
        shareReplay(1),
      );
    }
    return this.menu$;
  }

  // Shows the message when the id is unknown or inactive (the whole screen is then disabled)
  announceInvalidUser(): void {
    if (!this.menu.valid && this.menu.message) {
      this.snackbar.showMessage(this.menu.message);
    }
  }

  // The menu of the user as the backend sent it: only the screens the user may use
  modules(): RbacModule[] {
    return this.menu.modules;
  }

  isValid(): boolean {
    return this.menu.valid;
  }

  operations(functionCode: string): string[] {
    return AccessService.find(this.menu.modules.flatMap(module => module.submodules), functionCode)?.operations ?? [];
  }

  can(functionCode: string, operation: string): boolean {
    return this.operations(functionCode).includes(operation);
  }

  // A screen can be opened when the user may do at least one operation on it
  hasAccess(functionCode: string): boolean {
    return this.operations(functionCode).length > 0;
  }

  // Where a menu click goes. A screen that opens on its Add tab opens on View when the user may not add.
  pathFor(fn: { path: string; functionCode: string }): string {
    if (fn.path.endsWith('/add') && !this.can(fn.functionCode, OPERATION.ADD) && this.can(fn.functionCode, OPERATION.VIEW)) {
      return fn.path.slice(0, -'/add'.length) + '/view';
    }
    return fn.path;
  }

  // The pop-up for a click on something the user may not use (text from the backend)
  notifyNoAccess(): void {
    const text = this.menu.valid ? this.menu.noAccessMessage : this.menu.message?.msg ?? this.menu.noAccessMessage;
    if (text) {
      this.snackbar.showError(text);
    }
  }

  // The same menu, as an observable that finishes after loading; used by the route guard
  ready(): Observable<boolean> {
    return this.load().pipe(map(() => true));
  }

  private static find(submodules: RbacSubmodule[], functionCode: string): RbacFunction | undefined {
    for (const submodule of submodules ?? []) {
      const found = submodule.functions?.find(fn => fn.functionCode === functionCode)
        ?? AccessService.find(submodule.submodules, functionCode);
      if (found) {
        return found;
      }
    }
    return undefined;
  }

  // Nothing allowed: used when the backend says the id is unknown or inactive, or does not answer
  private static emptyMenu(message: string): RbacMenu {
    return {
      userId: null,
      username: null,
      valid: false,
      message: message ? { className: 'text-danger', msg: message } : null,
      noAccessMessage: message,
      modules: [],
    };
  }
}
