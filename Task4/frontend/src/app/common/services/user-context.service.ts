import { Injectable } from '@angular/core';

// The id of the user, until login is built. It is given once in the address of the page
// (http://localhost:4200/home/qms/item?userId=11), kept for this browser tab and sent with every request
// to the backend (see user-id-interceptor.ts). Opening the page with another userId is another user.
@Injectable({ providedIn: 'root' })
export class UserContextService {

  private static readonly STORAGE_KEY = 'qms.userId';
  static readonly QUERY_PARAM = 'userId';

  readonly userId: string | null;

  constructor() {
    const fromUrl = new URLSearchParams(window.location.search).get(UserContextService.QUERY_PARAM);
    if (fromUrl !== null && fromUrl.trim() !== '') {
      this.userId = fromUrl.trim();
      UserContextService.store(this.userId);
    } else {
      // The route changes drop the query part of the address, and so does a refresh from another page
      this.userId = UserContextService.stored();
    }
  }

  private static stored(): string | null {
    try {
      return sessionStorage.getItem(UserContextService.STORAGE_KEY);
    } catch {
      return null;
    }
  }

  private static store(userId: string): void {
    try {
      sessionStorage.setItem(UserContextService.STORAGE_KEY, userId);
    } catch {
      // storage not available: the id is then only valid until the page is reloaded without ?userId
    }
  }
}
